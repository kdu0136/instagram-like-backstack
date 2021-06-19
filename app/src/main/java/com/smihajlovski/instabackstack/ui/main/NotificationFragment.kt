package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.databinding.FragmentNotificationsBinding
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils

class NotificationFragment :
        BaseFragment<FragmentNotificationsBinding, NavigatorDestination>(resId = R.layout.fragment_notifications) {

    override fun onSetupUI() {
        binding.button.setOnClickListener {
            FragmentUtils.sendActionToActivity(
                    actionBundle = FragmentUtils.FragmentActionBundle(action = FragmentType.DASH_BOARD),
                    fragmentInteractionCallback = fragmentInteractionCallback
            )
        }
    }

    override fun observeViewModel() {
    }

    override fun runNavigate(navigate: NavigatorDestination) {
    }

    companion object {
        fun newFragment(bundle: Bundle? = null): NotificationFragment =
                NotificationFragment().apply {
                    arguments = bundle
                }
    }
}