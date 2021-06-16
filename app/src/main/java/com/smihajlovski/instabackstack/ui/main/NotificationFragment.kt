package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT
import com.smihajlovski.instabackstack.databinding.FragmentNotificationsBinding
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils

class NotificationFragment:
    BaseFragment<FragmentNotificationsBinding, NavigatorDestination>(resId = R.layout.fragment_notifications) {

    override fun onSetupUI() {
        binding.button.setOnClickListener {
            FragmentUtils.sendActionToActivity(
                action = FragmentUtils.FragmentType.DASH_BOARD,
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