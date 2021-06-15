package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT
import com.smihajlovski.instabackstack.databinding.FragmentDashboardBinding
import com.smihajlovski.instabackstack.databinding.FragmentHomeBinding
import com.smihajlovski.instabackstack.databinding.FragmentNotificationsBinding
import com.smihajlovski.instabackstack.tmp.Dummy
import com.smihajlovski.instabackstack.tmp.FeedMainAdapter
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment

class NotificationFragment:
    BaseFragment<FragmentNotificationsBinding, NavigatorDestination>(resId = R.layout.fragment_notifications) {

    override fun onSetupUI() {
        binding.button.setOnClickListener {

        }
    }

    override fun observeViewModel() {
    }

    override fun runNavigate(navigate: NavigatorDestination) {
    }

    companion object {
        fun newFragment(isRoot: Boolean): NotificationFragment {
            val bundle = Bundle().apply {
                putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot)
            }
            return NotificationFragment().apply {
                arguments = bundle
            }
        }
    }
}