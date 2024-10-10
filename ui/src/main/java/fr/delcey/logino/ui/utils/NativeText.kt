@file:Suppress("unused")

package fr.delcey.logino.ui.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible

sealed class NativeText {

    data class Simple(val text: String) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = text
    }

    data class Html(val html: String) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence =
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    data class Resource(@StringRes val id: Int) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = context.getString(id)
    }

    data class Plural(
        @PluralsRes val id: Int,
        val number: Int,
        val args: List<Any>,
    ) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence =
            context.resources.getQuantityString(
                id,
                number,
                *args.toTypedArray()
            )
    }

    data class Argument(
        @StringRes val id: Int,
        val arg: Any,
    ) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = if (arg is NativeText) {
            context.getString(id, arg.toCharSequence(context))
        } else {
            context.getString(id, arg)
        }
    }

    data class Arguments(
        @StringRes val id: Int,
        val args: List<Any>,
    ) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = context.getString(
            id,
            *args.map { arg ->
                if (arg is NativeText) {
                    arg.toCharSequence(context)
                } else {
                    arg
                }
            }.toTypedArray()
        )
    }

    data class Multi(val texts: List<NativeText>) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = StringBuilder().apply {
            for (item in texts) {
                append(item.toCharSequence(context))
            }
        }
    }

    abstract fun toCharSequence(context: Context): CharSequence
}

fun TextView.setText(nativeText: NativeText?) {
    text = nativeText?.toCharSequence(context)
}

fun TextView.setTextOrHide(nativeText: NativeText?) {
    val resolved = nativeText?.toCharSequence(context)
    isVisible = !resolved.isNullOrBlank()
    text = resolved
}

/**
 * Show the NativeText as Toast and return the instance of the shown toast, should you cancel it
 */
fun NativeText.showAsToast(context: Context, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(context, toCharSequence(context), duration).also {
        it.show()
    }