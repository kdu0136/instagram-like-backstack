package com.smihajlovski.instabackstack.tmp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.databinding.ItemFeedMainBinding

class FeedMainAdapter(private val click: (View, Int) -> Unit)
    : ListAdapter<PostThumb, RecyclerView.ViewHolder>(PostThumb.DiffCallBack) {
    private val constraintSet = ConstraintSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolder(
                    binding = DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.item_feed_main,
                            parent,
                            false
                    )
            )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        when (holder) {
            is ViewHolder -> holder.display(item = item)
        }
    }

    inner class ViewHolder(private val binding: ItemFeedMainBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun display(item: PostThumb) {
            with(binding) {
                constraintSet.apply {
                    clone(constraintLayout)
                    setDimensionRatio(imageView.id, item.ratio)
                    applyTo(constraintLayout)
                }

                data = item
            }

            binding.imageView.post {
                Glide.with(binding.imageView)
                        .load(item.postThumbnail)
                        .thumbnail(0.33f)
                        .apply(
                                RequestOptions()
                                        .centerCrop()
                        )
                        .into(binding.imageView)
            }

            binding.root.setOnClickListener {
                click(binding.imageView, item.postThumbnail)
            }
        }
    }
}