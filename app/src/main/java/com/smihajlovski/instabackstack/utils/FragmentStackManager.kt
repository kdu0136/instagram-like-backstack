package com.smihajlovski.instabackstack.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.tmp.applyIf
import java.io.Serializable
import java.util.*

class FragmentStackManager<TabType : Serializable>(
        @IdRes private val containerLayoutId: Int,
        private val fragmentManager: FragmentManager,
        private val menuTabFragmentStacks: HashMap<TabType, Fragment>,
        private val mainTabType: TabType,
) {
    data class FragmentAnimation(val enter: Int, val exit: Int) : Serializable
    data class FragmentTagStackData(val tag: String, val exitAni: FragmentAnimation? = null)

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

    private val menuTabFragmentTagStacks: HashMap<TabType, Stack<FragmentTagStackData>>
    private val menuStacks: MutableList<TabType> = mutableListOf(mainTabType)

    private lateinit var currentTab: TabType
    private lateinit var currentFragment: Fragment

    init {
        val tagStacks = hashMapOf<TabType, Stack<FragmentTagStackData>>()
        menuTabFragmentStacks.forEach {
            tagStacks[it.key] = Stack<FragmentTagStackData>()
        }
        this.menuTabFragmentTagStacks = tagStacks
    }

    // 탭 선택
    fun selectTab(tabType: TabType) {
        currentTab = tabType

        val tagStack: Stack<FragmentTagStackData> = menuTabFragmentTagStacks[tabType] ?: return
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
        // 탭 리스트 순서 재정렬
        // tabType 에 해당하는 탭을 탭 리스트 맨 뒤으로
        menuStacks.updateExistDataToEnd(data = tabType)
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
        menuTabFragmentTagStacks[tag]?.push(FragmentTagStackData(tag = fragmentTag))
    }

    // 탭 변경 후 변경 된 탭 fragment 반환
    private fun showHideTabFragment(tabType: TabType): Fragment? {
        if (!::currentFragment.isInitialized) return null

        val tagStack: Stack<FragmentTagStackData> = menuTabFragmentTagStacks[tabType] ?: return null
        val showTabFragment: Fragment =
                fragmentManager.findFragmentByTag(tagStack.peek().tag) ?: return null

        fragmentManager
                .beginTransaction()
                .hide(currentFragment)
                .show(showTabFragment)
                .commit()
        return showTabFragment
    }

    // 백버튼 process logic
    fun resolveBackPressed(finish: () -> Unit, changeTabType: (TabType) -> Unit) {
        val tagStack: Stack<FragmentTagStackData> = menuTabFragmentTagStacks[currentTab] ?: return
        // 현재 탭 프래그먼트 스택의 사이즈가 1개인 경우 탭 변경 or 앱 종료
        if (tagStack.size == 1) {
            // 현재 탭이 메인탭 일 경우 -> 앱 종료 / 그 외 탭은 이전 탭으로 변경
            if (currentTab == mainTabType) finish()
            else {
                menuStacks.removeLast()
                val nextMenuTabType = menuStacks.lastOrNull() ?: return
                changeTabType(nextMenuTabType)
            }
        } else // 현재 탭 프래그먼트 스택에 여러 화면이 쌓여있는 경우 해당 탭 프래그먼트 스택에서 pop
            popFragment(tabType = currentTab)
    }

    // 프래그먼트 스택에서 맨 마지막 fragment 제거
    private fun popFragment(tabType: TabType) {
        val tagStack: Stack<FragmentTagStackData> = menuTabFragmentTagStacks[tabType] ?: return
        // 프래그먼트 exit 에니메이션
        val exitAnimation: FragmentAnimation? = tagStack.peek().exitAni
        // 스택에서 마지막 프래그먼트 태그 제거
        tagStack.pop()

        // 제거 후 보여질 fragment tag
        val fragment: Fragment =
                fragmentManager.findFragmentByTag(tagStack.peek().tag) ?: return

        removeCurrentFragment(showFragment = fragment, animations = exitAnimation)

        currentFragment = fragment
    }

    // 현재 탭 프래그먼트 스택에서 root fragment 만 놔두고 전부 pop
    fun popStackExceptFirst() {
        val tagStack: Stack<FragmentTagStackData> = menuTabFragmentTagStacks[currentTab] ?: return
        if (tagStack.size == 1) return

        fragmentManager
                .beginTransaction()
                .apply {
                    // 현재 탭 tag stack 에서 root fragment 나올때까지 stack 탐색하며 fragment manager 에서 제거
                    // root fragment 나오면 show & currentFragment 변경
                    do {
                        val peekFragment: Fragment =
                                fragmentManager.findFragmentByTag(tagStack.peek().tag) ?: return

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

    // 현재 보여지고있는 fragment 를 fragment manager 에서 숨기고
    // 새로운 fragment 를 보여줌
    fun showFragment(
            fragment: Fragment,
            enterAnimations: FragmentAnimation?,
            exitAnimations: FragmentAnimation?,
    ) {
        addShowFragment(
                fragmentManager = fragmentManager,
                showFragment = fragment,
                enterAnimations = enterAnimations,
                exitAnimations = exitAnimations
        )
        currentFragment = fragment
    }

    // 현재 보여지고있는 fragment 를 fragment manager 에서 숨기고
    // 새로운 fragment 를 보여줌
    private fun addShowFragment(
            fragmentManager: FragmentManager,
            showFragment: Fragment,
            enterAnimations: FragmentAnimation? = null,
            exitAnimations: FragmentAnimation? = null,
    ) {
        val fragmentTag = showFragment.createFragmentTag()
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        enterAnimations?.enter ?: defaultAddFragmentAnimations.enter,
                        enterAnimations?.exit ?: defaultAddFragmentAnimations.exit
                )
                .add(containerLayoutId, showFragment, fragmentTag)
                .show(showFragment)
                .hide(currentFragment)
                .commit()
        menuTabFragmentTagStacks[currentTab]?.push(
                FragmentTagStackData(tag = fragmentTag, exitAni = exitAnimations)
        )
    }

    // 현재 보여지고있는 fragment 를 fragment manager 에서 제거하고
    // 새로운 fragment 를 보여줌
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