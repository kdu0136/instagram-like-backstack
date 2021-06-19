package com.smihajlovski.instabackstack.utils

import java.io.Serializable
import java.util.*

/**
 * list element 에서 data 가 없을 경우 추가해주고,
 * 있을 경우 해당 data 를 맨 뒤로 이동
 */
fun <T : Serializable> MutableList<T>.updateExistDataToEnd(data: T) {
    if (!contains(data)) add(data)
    else {
        val index = indexOf(data)
        if (index <= size - 1) { // 해당 data 가 마지막 index 에 위치해 있지 않은 경우 맨 뒤로 이동
            remove(data)
            add(data)
        }
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