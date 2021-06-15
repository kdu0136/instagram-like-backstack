package com.smihajlovski.instabackstack.tmp

import android.util.Log
import com.smihajlovski.instabackstack.BuildConfig

object PrintLog {
    private const val defaultTag: String = "XLAB_LOG"

    /**
     * 디버그 로그 출력
     *
     * @param title 로그 타이틀
     * @param log 로그 내용
     */
    fun d(title: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.d("$defaultTag/Test", "$title => $log")
    }

    /**
     * 디버그 로그 출력
     *
     * @param title 로그 타이틀
     * @param log 로그 내용
     * @param tag 로그 태그
     */
    fun d(title: String, log: String, tag: String) {
        if (BuildConfig.DEBUG)
            Log.d("$defaultTag/$tag", "$title => $log")
    }

    /**
     * 에러 로그 출력
     *
     * @param title 로그 타이틀
     * @param log 로그 내용
     */
    fun e(title: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.e("$defaultTag/Test", "$title => $log")
    }

    /**
     * 에러 로그 출력
     *
     * @param title 로그 타이틀
     * @param log 로그 내용
     * @param tag 로그 태그
     */
    fun e(title: String, log: String, tag: String) {
        if (BuildConfig.DEBUG)
            Log.e("$defaultTag/$tag", "$title => $log")
    }

    /**
     * 인포 로그 출력
     *
     * @param title 로그 타이틀
     * @param log 로그 내용
     */
    fun i(title: String, log: String) {
        if (BuildConfig.DEBUG)
            Log.i("$defaultTag/Test", "$title => $log")
    }

    /**
     * 인포 로그 출력
     *
     * @param title 로그 타이틀
     * @param log 로그 내용
     * @param tag 로그 태그
     */
    fun i(title: String, log: String, tag: String) {
        if (BuildConfig.DEBUG)
            Log.i("$defaultTag/$tag", "$title => $log")
    }
}
