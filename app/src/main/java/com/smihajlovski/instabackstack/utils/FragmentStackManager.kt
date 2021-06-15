package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants
import java.io.Serializable
import java.util.*

class FragmentStackManager<TabType : Serializable>(
    private val fragmentManager: FragmentManager,
    private val tabFragments: HashMap<TabType, Fragment>,
    tabs: List<TabType>
) {
    private val tagStacks: HashMap<TabType, Stack<String>>
    private val stackList: List<TabType>
    private val menuStacks: MutableList<TabType> = mutableListOf()

    private lateinit var currentTab: TabType
    private lateinit var currentFragment: Fragment

    init {
        val tagStacks = hashMapOf<TabType, Stack<String>>()
        val stackList = mutableListOf<TabType>()
        tabs.forEach {
            tagStacks[it] = Stack<String>()
            stackList.add(it)
        }
        this.tagStacks = tagStacks
        this.stackList = stackList
        this.menuStacks.add(tabs.first())
    }

    fun selectTab(tabType: TabType, isFirstTab: Boolean) {
        currentTab = tabType

        val tagStack: Stack<String> = tagStacks[tabType] ?: return
        if (tagStack.size == 0) {
            /*
              First time this tab is selected. So add first fragment of that tab.
              We are adding a new fragment which is not present in stack. So add to stack is true.
             */
            val tabFragment: Fragment = tabFragments[tabType] ?: return
            if (isFirstTab)
                addInitialTabFragment(
                    fragmentManager = fragmentManager,
                    tag = tabType,
                    fragment = tabFragment,
                    layoutId = R.id.frame_layout,
                    shouldAddToStack = true,
                )
            else
                addAdditionalTabFragment(
                    fragmentManager = fragmentManager,
                    tag = tabType,
                    showFragment = tabFragment,
                    hideFragment = currentFragment,
                    layoutId = R.id.frame_layout,
                    shouldAddToStack = true,
                )

            resolveStackLists(tabId = tabType)
            currentFragment = tabFragment
        } else {
            /*
             * We are switching tabs, and target tab already has at least one fragment.
             * Show the target fragment
             */
            val targetFragment: Fragment =
                fragmentManager.findFragmentByTag(tagStack.lastElement()) ?: return
            showHideTabFragment(
                fragmentManager = fragmentManager,
                showFragment = targetFragment,
                hideFragment = currentFragment
            )
            resolveStackLists(tabId = tabType)
            currentFragment = targetFragment
        }
    }

    private fun resolveStackLists(tabId: TabType) {
        stackList.updateStackIndex(tabId = tabId)
        menuStacks.updateTabStackIndex(tabId = tabId)
    }

    fun resolveBackPressed(finish: () -> Unit, currentTabType: (TabType) -> Unit) {
        var stackValue = 0
        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        if (tagStack.size == 1) {
            val value: Stack<String> = tagStacks[stackList[1]] ?: return
            if (value.size > 1) {
                stackValue = value.size
                popAndNavigateToPreviousMenu(currentTabType = currentTabType)
            }
            if (stackValue <= 1) {
                if (menuStacks.size > 1) navigateToPreviousMenu(currentTabType = currentTabType)
                else finish()
            }
        } else {
            popFragment()
        }
    }

    private fun popAndNavigateToPreviousMenu(currentTabType: (TabType) -> Unit) {
        val tempCurrent: TabType = stackList.firstOrNull() ?: return
        currentTab = stackList[1]
        currentTabType(currentTab)

        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        val targetFragment: Fragment =
            fragmentManager.findFragmentByTag(tagStack.lastElement()) ?: return
        showHideTabFragment(
            fragmentManager = fragmentManager,
            showFragment = targetFragment,
            hideFragment = currentFragment,
        )
        currentFragment = targetFragment
        stackList.updateStackToIndexFirst(tabId = tempCurrent)
        menuStacks.removeFirst()
    }

    private fun navigateToPreviousMenu(currentTabType: (TabType) -> Unit) {
        menuStacks.removeFirst()
        currentTab = menuStacks.firstOrNull() ?: return
        currentTabType(currentTab)

        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        val targetFragment: Fragment =
            fragmentManager.findFragmentByTag(tagStack.lastElement()) ?: return
        showHideTabFragment(
            fragmentManager = fragmentManager,
            showFragment = targetFragment,
            hideFragment = currentFragment,
        )
        currentFragment = targetFragment
    }

    private fun popFragment() {
        /*
         * Select the second last fragment in current tab's stack,
         * which will be shown after the fragment transaction given below
         */
        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        val fragmentTag: String = tagStack.elementAt(tagStack.size - 2)
        val fragment: Fragment = fragmentManager.findFragmentByTag(fragmentTag) ?: return

        /*pop current fragment from stack */
        tagStack.pop()

        removeFragment(
            fragmentManager = fragmentManager,
            showFragment = fragment,
            removeFragment = currentFragment,
        )

        currentFragment = fragment
    }

    fun popStackExceptFirst() {
        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        if (tagStack.size == 1) return

        do {
            val peekFragment: Fragment =
                fragmentManager.findFragmentByTag(tagStack.peek()) ?: return
            val peekFragmentArgs: Bundle = peekFragment.arguments ?: return

            fragmentManager.beginTransaction().remove(peekFragment)
            tagStack.pop()
        } while (tagStack.isNotEmpty() && peekFragmentArgs.getBoolean("EXTRA_IS_ROOT_FRAGMENT"))

        val fragment: Fragment =
            fragmentManager.findFragmentByTag(tagStack.firstElement()) ?: return
        removeFragment(
            fragmentManager = fragmentManager,
            showFragment = fragment,
            removeFragment = currentFragment
        )
        currentFragment = fragment
    }

    fun showFragment(bundle: Bundle, fragment: Fragment) {
        val shouldAdd: Boolean = bundle.getBoolean(Constants.DATA_KEY_2)

        addShowHideFragment(
            fragmentManager = fragmentManager,
            tag = currentTab,
            showFragment = fragment,
            hideFragment = getCurrentFragmentFromShownStack(),
            layoutId = R.id.frame_layout,
            shouldAddToStack = shouldAdd,
        )
        currentFragment = fragment
    }

    private fun getCurrentFragmentFromShownStack(): Fragment {
        val tagStack: Stack<String> =
            tagStacks[currentTab] ?: throw NullPointerException("tagStacks[currentTab] is NULL")
        return fragmentManager.findFragmentByTag(tagStack.elementAt(tagStack.size - 1))
            ?: throw NullPointerException("can not find findFragmentByTag")
    }


    /**
     * Add the initial fragment, in most cases the first tab in BottomNavigationView
     */
    private fun addInitialTabFragment(
        fragmentManager: FragmentManager,
        tag: TabType,
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
    private fun addAdditionalTabFragment(
        fragmentManager: FragmentManager,
        tag: TabType,
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
    private fun showHideTabFragment(
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
    private fun addShowHideFragment(
        fragmentManager: FragmentManager,
        tag: TabType,
        showFragment: Fragment,
        hideFragment: Fragment,
        @IdRes layoutId: Int,
        shouldAddToStack: Boolean,
    ) {
        val fragmentTag = showFragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_left,
            )
            .add(layoutId, showFragment, fragmentTag)
            .show(showFragment)
            .hide(hideFragment)
            .commit()
        if (shouldAddToStack)
            tagStacks[tag]?.push(fragmentTag)
    }

    private fun removeFragment(
        fragmentManager: FragmentManager,
        showFragment: Fragment,
        removeFragment: Fragment,
    ) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_right
            )
            .remove(removeFragment)
            .show(showFragment)
            .commit()
    }

    private fun Fragment.createFragmentTag(): String = "${javaClass.simpleName}:${hashCode()}"
}