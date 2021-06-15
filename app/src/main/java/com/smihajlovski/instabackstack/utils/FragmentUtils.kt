package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.common.Constants
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction
import java.util.*

object FragmentUtils {
    /**
     * Add the initial fragment, in most cases the first tab in BottomNavigationView
     */
    fun addInitialTabFragment(
        fragmentManager: FragmentManager,
        tagStacks: Map<String, Stack<String>>,
        tag: String,
        fragment: Fragment,
        @IdRes layoutId: Int,
        shouldAddToStack: Boolean,
    ) {
        val fragmentTag = fragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .add(layoutId, fragment, fragmentTag)
            .commit()
        if (shouldAddToStack)
            tagStacks[tag]?.push(fragmentTag)
    }

    /**
     * Add additional tab in BottomNavigationView on click, apart from the initial one and for the first time
     */
    fun addAdditionalTabFragment(
        fragmentManager: FragmentManager,
        tagStacks: Map<String, Stack<String>>,
        tag: String,
        showFragment: Fragment,
        hideFragment: Fragment,
        @IdRes layoutId: Int,
        shouldAddToStack: Boolean,
    ) {
        val fragmentTag = showFragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .add(layoutId, showFragment, fragmentTag)
            .show(showFragment)
            .hide(hideFragment)
            .commit()
        if (shouldAddToStack)
            tagStacks[tag]?.push(fragmentTag)
    }

    /**
     * Hide previous and show current tab fragment if it has already been added
     * In most cases, when tab is clicked again, not for the first time
     */
    fun showHideTabFragment(
        fragmentManager: FragmentManager,
        showFragment: Fragment,
        hideFragment: Fragment,
    ) {
        fragmentManager
            .beginTransaction()
            .hide(hideFragment)
            .show(showFragment)
            .commit()
    }

    /**
     * Add fragment in the particular tab stack and show it, while hiding the one that was before
     */
    fun addShowHideFragment(
        fragmentManager: FragmentManager,
        tagStacks: Map<String, Stack<String>>,
        tag: String,
        showFragment: Fragment,
        hideFragment: Fragment,
        @IdRes layoutId: Int,
        shouldAddToStack: Boolean,
    ) {
        val fragmentTag = showFragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .add(layoutId, showFragment, fragmentTag)
            .show(showFragment)
            .hide(hideFragment)
            .commit()
        if (shouldAddToStack)
            tagStacks[tag]?.push(fragmentTag)
    }

    fun removeFragment(
        fragmentManager: FragmentManager,
        showFragment: Fragment,
        removeFragment: Fragment,
    ) {
        fragmentManager
            .beginTransaction()
            .remove(removeFragment)
            .show(showFragment)
            .commit()
    }

    /**
     * Send action from fragment to activity
     */
    fun sendActionToActivity(
        action: String,
        tab: String,
        shouldAdd: Boolean,
        fragmentInteractionCallback: IFragmentInteraction,
    ) {
        val bundle = Bundle().apply {
            putString(Constants.ACTION, action)
            putString(Constants.DATA_KEY_1, tab)
            putBoolean(Constants.DATA_KEY_2, shouldAdd)
        }
        fragmentInteractionCallback.onInteractionCallback(bundle)
    }

    private fun Fragment.createFragmentTag(): String = "${javaClass.simpleName}:${hashCode()}"

    enum class FragmentDirection {
        FEED, SHOP, PROFILE, POST_DETAIL,
    }
}