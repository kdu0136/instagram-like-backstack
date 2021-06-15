package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT
import com.smihajlovski.instabackstack.databinding.FragmentDashboardBinding
import com.smihajlovski.instabackstack.tmp.Dummy
import com.smihajlovski.instabackstack.tmp.FeedMainAdapter
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils

class DashboardFragment:
    BaseFragment<FragmentDashboardBinding, NavigatorDestination>(resId = R.layout.fragment_dashboard) {

    private val adapter = FeedMainAdapter()

    override fun onSetupUI() {
        binding.recyclerView.adapter = adapter

        binding.button.setOnClickListener {
            FragmentUtils.sendActionToActivity(
                action = FragmentUtils.FragmentDirection.NOTIFICATION,
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
        fun newFragment(isRoot: Boolean): DashboardFragment {
            val bundle = Bundle().apply {
                putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot)
            }
            return DashboardFragment().apply {
                arguments = bundle
            }
        }
    }
}