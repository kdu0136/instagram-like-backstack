package com.smihajlovski.instabackstack.tmp

import com.smihajlovski.instabackstack.R
import kotlin.random.Random

object Dummy {
    private data class Post(val resource: Int, val ratio: String)

    private val postImages = listOf(
        Post(R.drawable.dummy_1_1_1, "1:1"), // 0
        Post(R.drawable.dummy_1_1_2, "1:1"),
        Post(R.drawable.dummy_1_1_3, "1:1"),
        Post(R.drawable.dummy_1_1_4, "1:1"),
        Post(R.drawable.dummy_1_1_5, "1:1"),
        Post(R.drawable.dummy_1_1_6, "1:1"),
        Post(R.drawable.dummy_1_1_7, "1:1"),
        Post(R.drawable.dummy_1_1_8, "1:1"),
        Post(R.drawable.dummy_1_1_9, "1:1"),
        Post(R.drawable.dummy_1_1_10, "1:1"),
        Post(R.drawable.dummy_4_5_1, "4:5"), // 10
        Post(R.drawable.dummy_4_5_2, "4:5"),
        Post(R.drawable.dummy_4_5_3, "4:5"),
        Post(R.drawable.dummy_4_5_4, "4:5"),
        Post(R.drawable.dummy_4_5_5, "4:5"),
        Post(R.drawable.dummy_4_5_6, "4:5"),
        Post(R.drawable.dummy_4_5_7, "4:5"),
        Post(R.drawable.dummy_4_5_8, "4:5"),
        Post(R.drawable.dummy_4_5_9, "4:5"),
        Post(R.drawable.dummy_4_5_10, "4:5"),
        Post(R.drawable.dummy_16_9_1, "16:9"), // 20
        Post(R.drawable.dummy_16_9_2, "16:9"),
        Post(R.drawable.dummy_16_9_3, "16:9"),
        Post(R.drawable.dummy_16_9_4, "16:9"),
        Post(R.drawable.dummy_16_9_5, "16:9"),
        Post(R.drawable.dummy_16_9_6, "16:9"),
        Post(R.drawable.dummy_16_9_7, "16:9"),
        Post(R.drawable.dummy_16_9_8, "16:9"),
        Post(R.drawable.dummy_16_9_9, "16:9"),
        Post(R.drawable.dummy_16_9_10, "16:9"),
    )

    fun getMainFeed(page: Int, size: Int): List<PostThumb> {
        val startIndex = (page - 1) * size
        return ArrayList<PostThumb>().apply {
            for (i in 0 until size) {
                val id = startIndex + i
                val profileImage = postImages[Random.nextInt(postImages.size)]
                val postImage = postImages[Random.nextInt(postImages.size)]
                val imageRatio = if (postImage.ratio == "16:9") "1:1" else postImage.ratio
                add(
                    PostThumb(
                        userId = "userId$id",
                        profileImage = profileImage.resource,
                        nickName = "${postImage.ratio} nickName$id",
                        postId = "$id",
                        postThumbnail = postImage.resource,
                        ratio = imageRatio,
                        topics = listOf(),
                    )
                )
            }
        }
    }
}