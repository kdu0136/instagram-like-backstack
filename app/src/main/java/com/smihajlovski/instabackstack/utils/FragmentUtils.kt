package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import android.view.View
import com.smihajlovski.instabackstack.common.Constants
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction
import java.io.Serializable

object FragmentUtils {
    /**
     * Send action from fragment to activity
     */
    fun <T : Serializable> sendActionToActivity(
        action: T,
        shouldAdd: Boolean,
        view: View? = null,
        image: Int? = null,
        fragmentInteractionCallback: IFragmentInteraction?,
    ) {
        val bundle = Bundle().apply {
            putSerializable(Constants.ACTION, action)
            putBoolean(Constants.DATA_KEY_2, shouldAdd)
            if (image != null) putInt("image", image)
        }
        fragmentInteractionCallback?.onInteractionCallback(bundle, view)
    }

    enum class FragmentType {
        HOME, DASH_BOARD, NOTIFICATION, POST
    }
}