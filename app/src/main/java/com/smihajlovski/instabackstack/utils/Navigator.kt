package com.smihajlovski.instabackstack.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

interface Navigator {
    @get:IdRes
    val containerId: Int
    val currentFragment: Fragment?
    fun push(fragment: Fragment, tag: String): Boolean
    fun pop(): Boolean
    fun clear(upToTag: String? = null, includeMatch: Boolean = false)
}