package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants
import java.io.Serializable
import java.util.*

class FragmentStackManager<FragmentType : Serializable>(
    private val fragmentManager: FragmentManager,
    private val tabFragments: HashMap<FragmentType, Fragment>,
    tabs: List<FragmentType>
) {
    private val tagStacks: HashMap<FragmentType, Stack<String>>
    private val stackList: List<FragmentType>
    private val menuStacks: MutableList<FragmentType> = mutableListOf()

    private lateinit var currentTab: FragmentType
    private lateinit var currentFragment: Fragment

    init {
        val tagStacks = hashMapOf<FragmentType, Stack<String>>()
        val stackList = mutableListOf<FragmentType>()
        tabs.forEach {
            tagStacks[it] = Stack<String>()
            stackList.add(it)
        }
        this.tagStacks = tagStacks
        this.stackList = stackList
        this.menuStacks.add(tabs.first())
    }

    fun selectTab(tabType: FragmentType) {
        currentTab = tabType

        val tagStack: Stack<String> = tagStacks[tabType] ?: return
        if (tagStack.size == 0) {
            /*
              First time this tab is selected. So add first fragment of that tab.
              We are adding a new fragment which is not present in stack. So add to stack is true.
             */
            val tabFragment: Fragment = tabFragments[tabType] ?: return
            when (tabType) {
                FragmentUtils.FragmentDirection.HOME -> {
                    addInitialTabFragment(
                        fragmentManager = fragmentManager,
                        tag = tabType,
                        fragment = tabFragment,
                        layoutId = R.id.frame_layout,
                        shouldAddToStack = true,
                    )
                }
                FragmentUtils.FragmentDirection.DASH_BOARD,
                FragmentUtils.FragmentDirection.NOTIFICATION, -> {
                    addAdditionalTabFragment(
                        fragmentManager = fragmentManager,
                        tag = tabType,
                        showFragment = tabFragment,
                        hideFragment = currentFragment,
                        layoutId = R.id.frame_layout,
                        shouldAddToStack = true,
                    )
                }
            }
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

    private fun resolveStackLists(tabId: FragmentType) {
        stackList.updateStackIndex(tabId = tabId)
        menuStacks.updateTabStackIndex(tabId = tabId)
    }

    fun resolveBackPressed(finish: () -> Unit, selectItemId: (Int) -> Unit) {
        var stackValue = 0
        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        if (tagStack.size == 1) {
            val value: Stack<String> = tagStacks[stackList[1]] ?: return
            if (value.size > 1) {
                stackValue = value.size
                popAndNavigateToPreviousMenu(selectItemId = selectItemId)
            }
            if (stackValue <= 1) {
                if (menuStacks.size > 1) navigateToPreviousMenu(selectItemId = selectItemId)
                else finish()
            }
        } else {
            popFragment()
        }
    }

    private fun popAndNavigateToPreviousMenu(selectItemId: (Int) -> Unit) {
        val tempCurrent: FragmentType = stackList.firstOrNull() ?: return
        currentTab = stackList[1]
        selectItemId(resolveTabPositions(currentTab = currentTab))

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

    private fun navigateToPreviousMenu(selectItemId: (Int) -> Unit) {
        menuStacks.removeFirst()
        currentTab = menuStacks.firstOrNull() ?: return
        selectItemId(resolveTabPositions(currentTab = currentTab))

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

    private fun resolveTabPositions(currentTab: FragmentType) = when (currentTab) {
        FragmentUtils.FragmentDirection.HOME -> R.id.tab_home
        FragmentUtils.FragmentDirection.DASH_BOARD -> R.id.tab_dashboard
        FragmentUtils.FragmentDirection.NOTIFICATION -> R.id.tab_notifications
        else -> 0
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
        tag: FragmentType,
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
        tag: FragmentType,
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
        tag: FragmentType,
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

    private fun removeFragment(
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

    private fun Fragment.createFragmentTag(): String = "${javaClass.simpleName}:${hashCode()}"
}