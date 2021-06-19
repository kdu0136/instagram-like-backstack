package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction
import java.io.Serializable

object FragmentUtils {
    const val ACTION = "action"
    const val ENTER_ANIMATION = "enterAnimation"
    const val EXIT_ANIMATION = "exitAnimation"

    /**
     * Send action from fragment to activity
     */
    fun <T : Serializable> sendActionToActivity(
            actionBundle: FragmentActionBundle<T>,
            fragmentBundle: Bundle? = null,
            fragmentInteractionCallback: IFragmentInteraction?,
    ) {
        fragmentInteractionCallback?.onInteractionCallback(
                actionBundle = Bundle().apply {
                    putSerializable(ACTION, actionBundle.action)
                    putSerializable(ENTER_ANIMATION, actionBundle.enterAnimation)
                    putSerializable(EXIT_ANIMATION, actionBundle.exitAnimation)
                },
                fragmentBundle = fragmentBundle
        )
    }

    data class FragmentActionBundle<T : Serializable>(
            val action: T,
            val enterAnimation: FragmentStackManager.FragmentAnimation? = null,
            val exitAnimation: FragmentStackManager.FragmentAnimation? = null,
    )
}