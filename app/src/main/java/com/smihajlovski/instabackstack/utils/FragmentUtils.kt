package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.common.Constants
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction
import java.io.Serializable
import java.util.*

object FragmentUtils {
    /**
     * Send action from fragment to activity
     */
    fun <T: Serializable>sendActionToActivity(
        action: T,
        shouldAdd: Boolean,
        fragmentInteractionCallback: IFragmentInteraction?,
    ) {
        val bundle = Bundle().apply {
            putSerializable(Constants.ACTION, action)
            putBoolean(Constants.DATA_KEY_2, shouldAdd)
        }
        fragmentInteractionCallback?.onInteractionCallback(bundle)
    }

    enum class FragmentDirection {
        HOME, DASH_BOARD, NOTIFICATION,
    }
}