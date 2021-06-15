package com.smihajlovski.instabackstack.tmp

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

data class PostThumb(
    val userId: String,
    @DrawableRes val profileImage: Int, // TODO(FIX): test 용으로 int (res id)
    val nickName: String,
    val postId: String,
    @DrawableRes val postThumbnail: Int, // TODO(FIX): test 용으로 int (res id)
    val ratio: String,
    val topics: List<Int>, // TODO(FIX): test 용으로 int (color)
) {
    companion object {
        val DiffCallBack = object : DiffUtil.ItemCallback<PostThumb>() {
            override fun areItemsTheSame(oldItem: PostThumb, newItem: PostThumb): Boolean =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: PostThumb, newItem: PostThumb): Boolean =
                oldItem == newItem
        }
    }
}
