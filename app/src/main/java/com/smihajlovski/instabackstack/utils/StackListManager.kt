package com.smihajlovski.instabackstack.utils

import java.io.Serializable
import java.util.*

/**
 * Keeps track of clicked tabs and their respective stacks
 * Swaps the tabs to first position as they're clicked
 * Ensures proper navigation when back presses occur
 */
fun <T : Serializable> List<T>.updateStackIndex(tabId: T) {
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
fun <T : Serializable> List<T>.updateStackToIndexFirst(tabId: T) {
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
fun <T : Serializable> MutableList<T>.updateTabStackIndex(tabId: T) {
    if (!contains(tabId))
        add(tabId)
    while (indexOf(tabId) != 0) {
        val i = indexOf(tabId)
        Collections.swap(this, i, i - 1)
    }
}