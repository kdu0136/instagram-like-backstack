package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT
import com.smihajlovski.instabackstack.databinding.FragmentHomeBinding
import com.smihajlovski.instabackstack.tmp.Dummy
import com.smihajlovski.instabackstack.tmp.FeedMainAdapter
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils

class HomeFragment :
    BaseFragment<FragmentHomeBinding, NavigatorDestination>(resId = R.layout.fragment_home) {

    private val adapter by lazy {
        FeedMainAdapter(click = { view, image ->
            FragmentUtils.sendActionToActivity(
                action = FragmentUtils.FragmentType.POST,
                shouldAdd = true,
                view = view,
                image = image,
                fragmentInteractionCallback = fragmentInteractionCallback
            )
        })
    }

    override fun onSetupUI() {
        binding.recyclerView.adapter = adapter

        binding.button.setOnClickListener {
            FragmentUtils.sendActionToActivity(
                action = FragmentUtils.FragmentType.DASH_BOARD,
                shouldAdd = true,
                fragmentInteractionCallback = fragmentInteractionCallback
            )
        }
    }

    override fun observeViewModel() {
        adapter.submitList(Dummy.getMainFeed(1, 200))
    }

    override fun runNavigate(navigate: NavigatorDestination) {
    }

    companion object {
        fun newFragment(isRoot: Boolean): HomeFragment {
            val bundle = Bundle().apply {
                putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot)
            }
            return HomeFragment().apply {
                arguments = bundle
            }
        }
    }
}