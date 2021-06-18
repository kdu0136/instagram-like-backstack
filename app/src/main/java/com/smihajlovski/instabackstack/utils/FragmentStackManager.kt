package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants
import com.smihajlovski.instabackstack.tmp.applyIf
import java.io.Serializable
import java.util.*

class FragmentStackManager<TabType : Serializable>(
    @IdRes private val containerLayoutId: Int,
    private val fragmentManager: FragmentManager,
    private val menuTabFragmentStacks: HashMap<TabType, Fragment>,
    private val menuTabList: MutableList<TabType>,
) {
    data class FragmentAnimation(val enter: Int, val exit: Int)

    // fragment 추가 & 삭제 기본 애니메이션
    private val defaultAddFragmentAnimations: FragmentAnimation =
        FragmentAnimation(
            enter = R.anim.anim_slide_in_right,
            exit = R.anim.anim_slide_out_left,
        )
    private val defaultRemoveFragmentAnimations: FragmentAnimation =
        FragmentAnimation(
            enter = R.anim.anim_slide_in_left,
            exit = R.anim.anim_slide_out_right,
        )

    private val menuTabFragmentTagStacks: HashMap<TabType, Stack<String>>
    private val menuStacks: MutableList<TabType> = mutableListOf(menuTabList.first())

    private lateinit var currentTab: TabType
    private lateinit var currentFragment: Fragment

    init {
        val tagStacks = hashMapOf<TabType, Stack<String>>()
        menuTabFragmentStacks.forEach {
            tagStacks[it.key] = Stack<String>()
        }
        this.menuTabFragmentTagStacks = tagStacks
    }

    // 탭 선택
    fun selectTab(tabType: TabType) {
        currentTab = tabType

        val tagStack: Stack<String> = menuTabFragmentTagStacks[tabType] ?: return
        currentFragment = if (tagStack.size == 0) {
            // 해당 탭을 처음 눌렀을 경우
            val menuTabFragment: Fragment = menuTabFragmentStacks[tabType] ?: return
            addTabFragment(
                tag = tabType,
                fragment = menuTabFragment,
            )
            menuTabFragment
        } else {
            // 해당 탑을 한번 이상 눌렀을 경우, 해당 탭에 해당하는 마지막 프래그먼트를 보여줌
            showHideTabFragment(tabType = tabType) ?: return
        }
        resolveStackLists(tabType = tabType)
    }

    // 탭 추가
    private fun addTabFragment(
        tag: TabType,
        fragment: Fragment,
    ) {
        val fragmentTag = fragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .add(containerLayoutId, fragment, fragmentTag)
            .applyIf(::currentFragment.isInitialized) {
                // currentFragment 가 초기화 되어 있을 경우 currentFragment 상태를 hide 로 변경후
                // 새로운 fragment 를 show
                show(fragment)
                hide(currentFragment)
            }
            .commit()
        menuTabFragmentTagStacks[tag]?.push(fragmentTag)
    }

    // 탭 변경 후 변경 된 탭 fragment 반환
    private fun showHideTabFragment(tabType: TabType): Fragment? {
        if (!::currentFragment.isInitialized) return null

        val tagStack: Stack<String> = menuTabFragmentTagStacks[tabType] ?: return null
        val showTabFragment: Fragment =
            fragmentManager.findFragmentByTag(tagStack.peek()) ?: return null

        fragmentManager
            .beginTransaction()
            .hide(currentFragment)
            .show(showTabFragment)
            .commit()
        return showTabFragment
    }

    // 탭 리스트 순서 재정렬
    // tabType 에 해당하는 탭을 탭 리스트 맨 뒤으로
    private fun resolveStackLists(tabType: TabType) {
//        menuTabList.updateDataToFirst(data = tabType)
        menuStacks.updateExistDataToFirst(data = tabType)
    }

    // 백버튼 process logic
    fun resolveBackPressed(finish: () -> Unit, currentTabType: (TabType) -> Unit) {
        var stackValue = 0
        val tagStack: Stack<String> = menuTabFragmentTagStacks[currentTab] ?: return
        if (tagStack.size == 1) { // 현재 탭 프래그먼트 스택의 사이즈가 1개인 경우 탭 변경 or 앱 종료
            val value: Stack<String> = menuTabFragmentTagStacks[menuTabList[1]] ?: return
            if (value.size > 1) {
                stackValue = value.size
                popAndNavigateToPreviousMenu(currentTabType = currentTabType)
            }
            if (stackValue <= 1) {
                if (menuStacks.size > 1) navigateToPreviousMenu(currentTabType = currentTabType)
                else finish()
            }
        } else // 현재 탭 프래그먼트 스택에 여러 화면이 쌓여있는 경우 해당 탭 프래그먼트 스택에서 pop
            popFragment(tabType = currentTab)
    }

    private fun popAndNavigateToPreviousMenu(currentTabType: (TabType) -> Unit) {
        val tempCurrent: TabType = menuTabList.firstOrNull() ?: return
        currentTab = menuTabList[1]
        currentTabType(currentTab)

        currentFragment = showHideTabFragment(tabType = currentTab) ?: return

        menuTabList.updateStackToIndexFirst(tabId = tempCurrent)
        menuStacks.removeFirst()
    }

    private fun navigateToPreviousMenu(currentTabType: (TabType) -> Unit) {
        menuStacks.removeFirst()
        currentTab = menuStacks.firstOrNull() ?: return
        currentTabType(currentTab)

        currentFragment = showHideTabFragment(tabType = currentTab) ?: return
    }

    // 프래그먼트 스택에서 맨 마지막 fragment 제거
    private fun popFragment(tabType: TabType) {
        val tagStack: Stack<String> = menuTabFragmentTagStacks[tabType] ?: return
        // 스택에서 마지막 프래그먼트 태그 제거
        tagStack.pop()

        // 제거 후 보여질 fragment tag
        val fragmentTag: String = tagStack.peek()
        val fragment: Fragment = fragmentManager.findFragmentByTag(fragmentTag) ?: return

        removeCurrentFragment(showFragment = fragment)

        currentFragment = fragment
    }

    // 현재 탭 프래그먼트 스택에서 root fragment 만 놔두고 전부 pop
    fun popStackExceptFirst() {
        val tagStack: Stack<String> = menuTabFragmentTagStacks[currentTab] ?: return
        if (tagStack.size == 1) return

        fragmentManager
            .beginTransaction()
            .apply {
                // 현재 탭 tag stack 에서 root fragment 나올때까지 stack 탐색하며 fragment manager 에서 제거
                // root fragment 나오면 show & currentFragment 변경
                do {
                    val peekFragment: Fragment =
                        fragmentManager.findFragmentByTag(tagStack.peek()) ?: return

                    val findRoot: Boolean = if (tagStack.size > 1) {
                        remove(peekFragment)
                        tagStack.pop()
                        false
                    } else {
                        show(peekFragment)
                        currentFragment = peekFragment
                        true
                    }
                } while (!findRoot)
            }
            .commit()
    }

    fun showFragment(bundle: Bundle, fragment: Fragment, shareView: View?) {
        val shouldAdd: Boolean = bundle.getBoolean(Constants.DATA_KEY_2)

        if (shareView == null)
            addShowHideFragment(
                fragmentManager = fragmentManager,
                tag = currentTab,
                showFragment = fragment,
                hideFragment = getCurrentFragmentFromShownStack(),
                shouldAddToStack = shouldAdd,
            )
        else
            addShowHideFragment2(
                fragmentManager = fragmentManager,
                tag = currentTab,
                showFragment = fragment,
                hideFragment = getCurrentFragmentFromShownStack(),
                shouldAddToStack = shouldAdd,
                shareView = shareView,
            )
        currentFragment = fragment
    }

    private fun getCurrentFragmentFromShownStack(): Fragment {
        val tagStack: Stack<String> =
            menuTabFragmentTagStacks[currentTab]
                ?: throw NullPointerException("tagStacks[currentTab] is NULL")
        return fragmentManager.findFragmentByTag(tagStack.elementAt(tagStack.size - 1))
            ?: throw NullPointerException("can not find findFragmentByTag")
    }

    /**
     * Add fragment in the particular tab stack and show it, while hiding the one that was before
     */
    private fun addShowHideFragment(
        fragmentManager: FragmentManager,
        tag: TabType,
        showFragment: Fragment,
        hideFragment: Fragment,
        shouldAddToStack: Boolean,
    ) {
        val fragmentTag = showFragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_left,
            )
            .add(containerLayoutId, showFragment, fragmentTag)
            .show(showFragment)
            .hide(hideFragment)
            .commit()
        if (shouldAddToStack)
            menuTabFragmentTagStacks[tag]?.push(fragmentTag)
    }

    private fun addShowHideFragment2(
        fragmentManager: FragmentManager,
        tag: TabType,
        showFragment: Fragment,
        hideFragment: Fragment,
        shouldAddToStack: Boolean,
        shareView: View,
    ) {
        val fragmentTag = showFragment.createFragmentTag()
        fragmentManager
            .beginTransaction()
            .addSharedElement(shareView, "testTransition")
            .setReorderingAllowed(true)
            .add(containerLayoutId, showFragment, fragmentTag)
            .show(showFragment)
            .hide(hideFragment)
            .commit()
        if (shouldAddToStack)
            menuTabFragmentTagStacks[tag]?.push(fragmentTag)
    }

    // 현재 보여지고있는 fragment fragment manager 에서 제거
    private fun removeCurrentFragment(
        showFragment: Fragment,
        animations: FragmentAnimation? = null,
    ) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(
                animations?.enter ?: defaultRemoveFragmentAnimations.enter,
                animations?.exit ?: defaultRemoveFragmentAnimations.exit
            )
            .remove(currentFragment)
            .show(showFragment)
            .commit()
    }

    private fun Fragment.createFragmentTag(): String = "${javaClass.simpleName}:${hashCode()}"
}