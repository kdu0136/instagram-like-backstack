package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.*
import com.smihajlovski.instabackstack.ui.base.BaseFragment2
import java.util.*

class FragmentStackManager(
    private val fragmentManager: FragmentManager,
    private val tabFragments: HashMap<String, Fragment>,
    tabs: List<String>
) {
    private val tagStacks: HashMap<String, Stack<String>>
    private val stackList: List<String>
    private val menuStacks: MutableList<String> = mutableListOf()

    private var currentTab: String = ""
    private lateinit var currentFragment: Fragment

    init {
        val tagStacks = hashMapOf<String, Stack<String>>()
        val stackList = mutableListOf<String>()
        tabs.forEach {
            tagStacks[it] = Stack<String>()
            stackList.add(it)
        }
        this.tagStacks = tagStacks
        this.stackList = stackList
        this.menuStacks.add(tabs.first())
    }

    fun selectTab(tabId: String) {
        currentTab = tabId
        BaseFragment2.setCurrentTab(currentTab)

        val tagStack: Stack<String> = tagStacks[tabId] ?: return
        if (tagStack.size == 0) {
            /*
              First time this tab is selected. So add first fragment of that tab.
              We are adding a new fragment which is not present in stack. So add to stack is true.
             */
            val tabFragment: Fragment = tabFragments[tabId] ?: return
            when (tabId) {
                TAB_HOME -> {
                    FragmentUtils.addInitialTabFragment(
                        fragmentManager = fragmentManager,
                        tagStacks = tagStacks,
                        tag = tabId,
                        fragment = tabFragment,
                        layoutId = R.id.frame_layout,
                        shouldAddToStack = true,
                    )
                }
                TAB_DASHBOARD,
                TAB_NOTIFICATIONS -> {
                    FragmentUtils.addAdditionalTabFragment(
                        fragmentManager = fragmentManager,
                        tagStacks = tagStacks,
                        tag = tabId,
                        showFragment = tabFragment,
                        hideFragment = currentFragment,
                        layoutId = R.id.frame_layout,
                        shouldAddToStack = true,
                    )
                }
            }
            resolveStackLists(tabId = tabId)
            currentFragment = tabFragment
        } else {
            /*
             * We are switching tabs, and target tab already has at least one fragment.
             * Show the target fragment
             */
            val targetFragment: Fragment =
                fragmentManager.findFragmentByTag(tagStack.lastElement()) ?: return
            FragmentUtils.showHideTabFragment(
                fragmentManager = fragmentManager,
                showFragment = targetFragment,
                hideFragment = currentFragment
            )
            resolveStackLists(tabId = tabId)
            currentFragment = targetFragment
        }
    }

    private fun resolveStackLists(tabId: String) {
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
        val tempCurrent: String = stackList.firstOrNull() ?: return
        currentTab = stackList[1]
        BaseFragment2.setCurrentTab(currentTab)
        selectItemId(resolveTabPositions(currentTab = currentTab))

        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        val targetFragment: Fragment =
            fragmentManager.findFragmentByTag(tagStack.lastElement()) ?: return
        FragmentUtils.showHideTabFragment(
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
        BaseFragment2.setCurrentTab(currentTab)
        selectItemId(resolveTabPositions(currentTab = currentTab))

        val tagStack: Stack<String> = tagStacks[currentTab] ?: return
        val targetFragment: Fragment =
            fragmentManager.findFragmentByTag(tagStack.lastElement()) ?: return
        FragmentUtils.showHideTabFragment(
            fragmentManager = fragmentManager,
            showFragment = targetFragment,
            hideFragment = currentFragment,
        )
        currentFragment = targetFragment
    }

    private fun resolveTabPositions(currentTab: String) = when (currentTab) {
        TAB_HOME -> R.id.tab_home
        TAB_DASHBOARD -> R.id.tab_dashboard
        TAB_NOTIFICATIONS -> R.id.tab_notifications
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

        FragmentUtils.removeFragment(
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
            val peekFragment: Fragment = fragmentManager.findFragmentByTag(tagStack.peek()) ?: return
            val peekFragmentArgs: Bundle = peekFragment.arguments ?: return

            fragmentManager.beginTransaction().remove(peekFragment)
            tagStack.pop()
        } while (tagStack.isNotEmpty() && peekFragmentArgs.getBoolean("EXTRA_IS_ROOT_FRAGMENT"))

        val fragment: Fragment = fragmentManager.findFragmentByTag(tagStack.firstElement()) ?: return
        FragmentUtils.removeFragment(
            fragmentManager = fragmentManager,
            showFragment = fragment,
            removeFragment = currentFragment
        )
        currentFragment = fragment
    }

    fun showFragment(bundle: Bundle, fragment: Fragment) {
        val tab: String = bundle.getString(DATA_KEY_1) ?: return
        val shouldAdd: Boolean = bundle.getBoolean(DATA_KEY_2)

        FragmentUtils.addShowHideFragment(
            fragmentManager = fragmentManager,
            tagStacks = tagStacks,
            tag = tab,
            showFragment = fragment,
            hideFragment = getCurrentFragmentFromShownStack(),
            layoutId = R.id.frame_layout,
            shouldAddToStack = shouldAdd,
        )
        currentFragment = fragment
    }

    private fun getCurrentFragmentFromShownStack(): Fragment {
        val tagStack: Stack<String> = tagStacks[currentTab] ?: throw NullPointerException("tagStacks[currentTab] is NULL")
        return fragmentManager.findFragmentByTag(tagStack.elementAt(tagStack.size - 1)) ?: throw NullPointerException("can not find findFragmentByTag")
    }
}

/**
 * Keeps track of clicked tabs and their respective stacks
 * Swaps the tabs to first position as they're clicked
 * Ensures proper navigation when back presses occur
 */
fun List<String>.updateStackIndex(tabId: String) {
    while (indexOf(tabId) != 0) {
        val i = indexOf(tabId)
        Collections.swap(this, i, i - 1)
    }
}

/**
 * Keeps track of when switching between tabs occur
 * The next tab to be shown is pushed on top
 * The tab which was current before is now pushed as last
 */
fun List<String>.updateStackToIndexFirst(tabId: String) {
    var moveUp = 1
    while (indexOf(tabId) != size - 1) {
        val i = indexOf(tabId)
        Collections.swap(this, moveUp++, i)
    }
}

/**
 * Keeps track of the clicked tabs and ensures proper navigation if there are no nested fragments in the tabs
 * When navigating back, the user will end up on the first clicked tab
 * If the first tab is clicked again while navigating, the user will end up on the second tab clicked
 */
fun MutableList<String>.updateTabStackIndex(tabId: String) {
    if (!contains(tabId))
        add(tabId)
    while (indexOf(tabId) != 0) {
        val i = indexOf(tabId)
        Collections.swap(this, i, i - 1)
    }
}