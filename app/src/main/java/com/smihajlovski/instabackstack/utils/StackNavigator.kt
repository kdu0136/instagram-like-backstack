package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class StackNavigator constructor(
        internal val fragmentManger: FragmentManager,
        @param:IdRes @field:IdRes @get:IdRes override val containerId: Int,
) : Navigator {
    override val currentFragment: Fragment?
        get() = fragmentManger.findFragmentById(containerId)

    private val String.toEntry
        get() = "$containerId.$this"

    private val FragmentManager.BackStackEntry.inContainer: Boolean
        get() = name?.split(".")?.firstOrNull() == containerId.toString()

    private val FragmentManager.BackStackEntry.tag
        get() = name?.run { this.removePrefix(split(".").first() + ".") }

    private val backStackEntries: List<FragmentManager.BackStackEntry>
        get() = fragmentManger.run { (0 until backStackEntryCount).map(this::getBackStackEntryAt).filter { it.inContainer } }

    private val fragmentTags: List<String?>
        get() = backStackEntries.map { it.tag }

    init {
        fragmentManger.registerFragmentLifecycleCallbacks(object: FragmentManager.FragmentLifecycleCallbacks() {
//            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) = auditFragment(f)
        }, false)
    }

    override fun push(fragment: Fragment, tag: String): Boolean {
        val tags = fragmentTags
        val currentFragmentTag = tags.lastOrNull()
        if (currentFragmentTag != null && currentFragmentTag == tag) return false

        val fragmentAlreadyExist = tags.contains(tag)
        val fragmentShown = !fragmentAlreadyExist
        val fragmentToShow =
                (if (fragmentAlreadyExist) fragmentManger.findFragmentByTag(tag) else fragment) ?: throw NullPointerException("MSG DODGY FRAGMENT")

//        fragmentManger.
    }

    override fun pop(): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear(upToTag: String?, includeMatch: Boolean) {
        TODO("Not yet implemented")
    }
}