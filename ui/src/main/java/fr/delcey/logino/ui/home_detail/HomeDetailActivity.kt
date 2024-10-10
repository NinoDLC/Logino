package fr.delcey.logino.ui.home_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.databinding.HomeDetailActivityBinding
import fr.delcey.logino.ui.utils.setText
import fr.delcey.logino.ui.utils.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class HomeDetailActivity : AppCompatActivity() {

    companion object {
        const val ARG_HOME_ID = "ARG_HOME_ID"
        private const val ARG_UNIQUE_SHARED_ELEMENT_TRANSITION_NAME = "ARG_PHOTO_SHARED_ELEMENT_TRANSITION_NAME"

        fun navigate(
            context: Context,
            homeId: Long,
            uniqueSharedTransitionName: String?,
        ): Intent = Intent(context, HomeDetailActivity::class.java).apply {
            putExtra(ARG_HOME_ID, homeId)
            putExtra(ARG_UNIQUE_SHARED_ELEMENT_TRANSITION_NAME, uniqueSharedTransitionName)
        }
    }

    private val binding by viewBinding { HomeDetailActivityBinding.inflate(it) }
    private val viewModel by viewModels<HomeDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        enableEdgeToEdge()

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.homeDetailCoordinatorLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configureSharedElementTransition()

        binding.homeDetailSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onPullToRefresh()
        }

        viewModel.uiStateLiveData.observe(this) { uiState ->
            binding.homeDetailSwipeRefreshLayout.isRefreshing = uiState is HomeDetailUiState.Loading
            binding.homeDetailConstraintLayoutSuccess.isVisible = uiState !is HomeDetailUiState.Error
            binding.homeDetailConstraintLayoutFailure.isVisible = uiState is HomeDetailUiState.Error

            when (uiState) {
                is HomeDetailUiState.Content -> {
                    binding.homeDetailImageViewPhoto.load(uiState.photoUrl) {
                        error(R.drawable.logo_seloger)
                        listener(
                            onCancel = { startPostponedEnterTransition() },
                            onError = { _, _ -> startPostponedEnterTransition() },
                            onSuccess = { _, _ -> startPostponedEnterTransition() }
                        )
                    }
                    binding.homeDetailTextViewVendor.setText(uiState.vendor)
                    binding.homeDetailTextViewPrice.setText(uiState.price)
                    binding.homeDetailTextViewPricePerSquareMeter.setText(uiState.pricePerSquareMeter)
                    binding.homeDetailTextViewPropertyType.setText(uiState.propertyType)
                    binding.homeDetailTextViewRoomsAndSize.setText(uiState.roomsAndSize)
                    binding.homeDetailTextViewCity.setText(uiState.city)
                }

                is HomeDetailUiState.Error -> binding.homeDetailTextViewError.setText(uiState.errorMessage)
                is HomeDetailUiState.Loading -> {}
            }
        }
    }

    private fun configureSharedElementTransition() {
        postponeEnterTransition()

        lifecycleScope.launch {
            // Let maximum 200ms for the shared element transition image to load before drawing the activity
            delay(200.milliseconds)
            startPostponedEnterTransition()
        }

        val uniqueSharedElementTransitionName = intent.getStringExtra(ARG_UNIQUE_SHARED_ELEMENT_TRANSITION_NAME)
        binding.homeDetailImageViewPhoto.transitionName += uniqueSharedElementTransitionName
        binding.homeDetailViewVendorBackground.transitionName += uniqueSharedElementTransitionName
        binding.homeDetailTextViewVendor.transitionName += uniqueSharedElementTransitionName
    }
}