package me.mking.biirr.me.mking.biirr.ui.beer.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.mking.biirr.R
import me.mking.biirr.databinding.BeerViewHolderBinding
import me.mking.biirr.databinding.MoreViewHolderBinding

typealias BeerListClickListener = (BeerListClickEvent) -> Unit

class BeerListAdapter : ListAdapter<BeerListViewItem, ItemViewHolder<*, *>>(DIFF_CALLBACK) {

    var listener: BeerListClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder<*, *> {
        return when (ViewType.from(viewType)) {
            ViewType.BEER -> BeerItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.beer_view_holder, parent, false
                )
            )
            ViewType.MORE -> MoreItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.more_view_holder, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<*, *>, position: Int) {
        when (val item = currentList.getOrNull(position)) {
            is BeerListViewItem.Beer -> (holder as BeerItemViewHolder).bind(item, listener)
            is BeerListViewItem.More -> (holder as MoreItemViewHolder).bind(item, listener)
            else -> Unit
        }
    }

    override fun getItemCount() = currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (currentList.getOrNull(position)) {
            is BeerListViewItem.Beer -> ViewType.BEER.ordinal
            is BeerListViewItem.More -> ViewType.MORE.ordinal
            else -> throw Exception("Unknown view type")
        }
    }

    enum class ViewType {
        BEER,
        MORE;

        companion object {
            fun from(ord: Int) = when (ord) {
                BEER.ordinal -> BEER
                MORE.ordinal -> MORE
                else -> throw Exception("Unknown view type")
            }
        }
    }

    private companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<BeerListViewItem> =
            object : DiffUtil.ItemCallback<BeerListViewItem>() {
                override fun areItemsTheSame(
                    oldItem: BeerListViewItem,
                    newItem: BeerListViewItem
                ): Boolean {
                    if (oldItem.javaClass != newItem.javaClass) return false
                    return when (oldItem) {
                        is BeerListViewItem.Beer -> oldItem.clickEvent == (newItem as BeerListViewItem.Beer).clickEvent
                        is BeerListViewItem.More -> oldItem.clickEvent == (newItem as BeerListViewItem.More).clickEvent
                        else -> false
                    }
                }

                override fun areContentsTheSame(
                    oldItem: BeerListViewItem,
                    newItem: BeerListViewItem
                ): Boolean {
                    return when (oldItem) {
                        is BeerListViewItem.Beer -> oldItem == (newItem as BeerListViewItem.Beer)
                        is BeerListViewItem.More -> oldItem == (newItem as BeerListViewItem.More)
                        else -> false
                    }
                }
            }
    }
}

abstract class ItemViewHolder<T, L>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: T, listener: L)
}

class BeerItemViewHolder(itemView: View) :
    ItemViewHolder<BeerListViewItem.Beer, BeerListClickListener?>(itemView) {
    private val _binding = BeerViewHolderBinding.bind(itemView)

    override fun bind(data: BeerListViewItem.Beer, listener: BeerListClickListener?) =
        with(_binding) {
            nameTextView.text = data.name
            when (data.imageUrl) {
                is ImageHolder.Drawable -> imageView.setImageResource(data.imageUrl.resId)
                is ImageHolder.Url -> Glide.with(itemView.context)
                    .load(data.imageUrl.url)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(imageView)
            }

            root.setOnClickListener(
                listener?.let {
                    { listener.invoke(data.clickEvent) }
                }
            )
        }
}

class MoreItemViewHolder(itemView: View) :
    ItemViewHolder<BeerListViewItem.More, BeerListClickListener?>(itemView) {

    private val _binding = MoreViewHolderBinding.bind(itemView)

    override fun bind(data: BeerListViewItem.More, listener: BeerListClickListener?) =
        with(_binding) {
            loadMoreButton.setOnClickListener(
                listener?.let {
                    { listener.invoke(data.clickEvent) }
                }
            )
            loadMoreButton.isEnabled = !data.loadingMore
            progressBar.isVisible = data.loadingMore
        }
}
