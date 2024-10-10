package fr.delcey.logino.ui.main

import android.app.ActivityOptions
import android.os.Bundle
import android.util.Pair
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.logino.ui.databinding.MainActivityBinding
import fr.delcey.logino.ui.home_detail.HomeDetailActivity
import fr.delcey.logino.ui.navigation.To
import fr.delcey.logino.ui.utils.Event.Companion.observeEvent
import fr.delcey.logino.ui.utils.setText
import fr.delcey.logino.ui.utils.viewBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding { MainActivityBinding.inflate(it) }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainCoordinatorLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mainSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onPullToRefresh()
        }

        val adapter = MainAdapter()
        binding.mainRecyclerView.adapter = adapter
        binding.mainRecyclerView.itemAnimator = null

        viewModel.uiStateLiveData.observe(this) { uiState ->
            binding.mainSwipeRefreshLayout.isRefreshing = uiState is MainUiState.Loading
            binding.mainRecyclerView.isVisible = uiState !is MainUiState.Error
            binding.mainTextViewErrorMessage.isVisible = uiState is MainUiState.Error

            when (uiState) {
                is MainUiState.Content -> adapter.submitList(uiState.items)
                is MainUiState.Error -> binding.mainTextViewErrorMessage.setText(uiState.errorMessage)
                is MainUiState.Loading -> {}
            }
        }

        viewModel.eventLiveData.observeEvent(this) { event ->
            when (event) {
                is MainEvent.Navigate -> when (event.to) {
                    is To.HomeDetail -> {
                        val intent = HomeDetailActivity.navigate(
                            context = this,
                            homeId = event.to.id,
                            uniqueSharedTransitionName = event.to.uniqueSharedElementName
                        )

                        if (event.to.uniqueSharedElementName == null) {
                            startActivity(intent)
                        } else {
                            startActivity(
                                intent,
                                ActivityOptions.makeSceneTransitionAnimation(
                                    this,
                                    *event.to.sharedElements.map { view ->
                                        Pair(view, view.transitionName)
                                    }.toTypedArray()
                                ).toBundle()
                            )
                        }
                    }
                }
            }
        }
    }
}