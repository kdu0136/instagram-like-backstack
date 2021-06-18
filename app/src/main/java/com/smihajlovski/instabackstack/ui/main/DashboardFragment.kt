package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT
import com.smihajlovski.instabackstack.databinding.FragmentDashboardBinding
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils

class DashboardFragment:
    BaseFragment<FragmentDashboardBinding, NavigatorDestination>(resId = R.layout.fragment_dashboard) {

    override fun onSetupUI() {
        binding.button.setOnClickListener {
            FragmentUtils.sendActionToActivity(
                action = NavigationMenuType.NOTIFICATION,
                shouldAdd = true,
                fragmentInteractionCallback = fragmentInteractionCallback
            )
        }
    }

    override fun observeViewModel() {
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