package fr.delcey.logino.ui.utils

/**
 * Utility class to help Unit Test the exposed models by the ViewModel.
 * In Kotlin, every lambda created is an anonymous class, so it is not equal to any other. This is a problem when trying to match the
 * equality of 2 models (the expected one and the tested one) in unit testing.
 *
 * So every EquatableCallback are equals to each other, allowing the use of a simple "assertEquals()" in unit test instead of complex custom
 * matchers.
 *
 * Don't use this class if you don't unit test or if you use more complex or non-exhaustive assertions in your ViewModel Unit Tests.
 */
class EquatableCallback(private val callback: () -> Unit) {

    operator fun invoke() {
        callback.invoke()
    }

    override fun equals(other: Any?): Boolean = if (other is EquatableCallback) {
        true
    } else {
        super.equals(other)
    }

    override fun hashCode(): Int = 2051656923
}
