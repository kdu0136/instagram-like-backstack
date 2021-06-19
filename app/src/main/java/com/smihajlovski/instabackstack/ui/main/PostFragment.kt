package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.core.view.doOnPreDraw
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.databinding.FragmentPostBinding
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment

class PostFragment :
        BaseFragment<FragmentPostBinding, NavigatorDestination>(resId = R.layout.fragment_post) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
                    duration = 1000L
                }
    }

    override fun onSetupUI() {
        postponeEnterTransition()
        binding.imageView.doOnPreDraw { startPostponedEnterTransition() }

        binding.imageView.post {
            Glide.with(binding.imageView)
                    .load(image)
                    .thumbnail(0.33f)
                    .apply(
                            RequestOptions()
                                    .centerCrop()
                    )
                    .into(binding.imageView)
        }
    }

    override fun observeViewModel() {
    }

    override fun runNavigate(navigate: NavigatorDestination) {
    }

    val image: Int
        get() = arguments?.getInt("image") ?: 0

    companion object {
        fun newFragment(bundle: Bundle? = null): PostFragment =
                PostFragment().apply {
                    arguments = bundle
                }
    }
}