package fr.delcey.logino.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.databinding.MainItemBinding
import fr.delcey.logino.ui.utils.setText

class MainAdapter : ListAdapter<MainItemUiState, MainAdapter.MainViewHolder>(MainDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder = MainViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MainViewHolder(private val binding: MainItemBinding) : ViewHolder(binding.root) {
        companion object {
            fun newInstance(parent: ViewGroup) = MainViewHolder(
                MainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(uiState: MainItemUiState) {
            binding.mainItemCardView.setOnClickListener {
                uiState.onClick(
                    listOf(
                        binding.mainItemImageViewPhoto,
                        binding.mainItemViewVendorBackground,
                        binding.mainItemTextViewVendor,
                    )
                )
            }

            binding.mainItemImageViewPhoto.transitionName =
                binding.root.context.getString(R.string.shared_element_transition_home_photo) + uiState.photoUrl
            binding.mainItemViewVendorBackground.transitionName =
                binding.root.context.getString(R.string.shared_element_transition_home_vendor_background) + uiState.photoUrl
            binding.mainItemTextViewVendor.transitionName =
                binding.root.context.getString(R.string.shared_element_transition_home_vendor) + uiState.photoUrl

            binding.mainItemImageViewPhoto.load(uiState.photoUrl) {
                error(R.drawable.logo_seloger)
            }
            binding.mainItemTextViewVendor.setText(uiState.vendor)
            binding.mainItemTextViewPrice.setText(uiState.price)
            binding.mainItemTextViewPricePerSquareMeter.setText(uiState.pricePerSquareMeter)
            binding.mainItemTextViewPropertyType.setText(uiState.propertyType)
            binding.mainItemTextViewRoomsAndSize.setText(uiState.roomsAndSize)
            binding.mainItemTextViewCity.setText(uiState.city)
        }
    }

    private object MainDiffCallback : DiffUtil.ItemCallback<MainItemUiState>() {
        override fun areItemsTheSame(oldItem: MainItemUiState, newItem: MainItemUiState): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MainItemUiState, newItem: MainItemUiState): Boolean = oldItem == newItem
    }
}
