package com.smihajlovski.instabackstack.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.smihajlovski.instabackstack.tmp.PrintLog
import com.smihajlovski.instabackstack.tmp.createDataBinding

abstract class BaseActivity<VB : ViewDataBinding>(@LayoutRes resId: Int) :
        AppCompatActivity() {
    protected val activityTag = javaClass.simpleName

    protected val binding: VB by lazy { createDataBinding(activity = this, resId = resId) }

    /**
     * page UI setup
     */
    abstract fun onSetupUI()

    /**
     * page view model observe setting
     */
    abstract fun observeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
//        PrintLog.d(javaClass.simpleName, "onCreate", activityTag)
        super.onCreate(savedInstanceState)

        onSetupUI()

        observeViewModel()
    }

    override fun onStart() {
//        PrintLog.d(javaClass.simpleName, "onStart", activityTag)
        super.onStart()
    }

    override fun onResume() {
//        PrintLog.d(javaClass.simpleName, "onResume", activityTag)
        super.onResume()
    }

    override fun onPause() {
//        PrintLog.d(javaClass.simpleName, "onPause", activityTag)
        super.onPause()
    }

    override fun onRestart() {
//        PrintLog.d(javaClass.simpleName, "onRestart", activityTag)
        super.onRestart()
    }

    override fun onStop() {
//        PrintLog.d(javaClass.simpleName, "onStop", activityTag)
        super.onStop()
    }

    override fun onDestroy() {
//        PrintLog.d(javaClass.simpleName, "onDestroy", activityTag)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PrintLog.d("ActivityResult", "resultCode: $resultCode", activityTag)
        PrintLog.d("ActivityResult", "requestCode: $requestCode", activityTag)
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        PrintLog.d(javaClass.simpleName, "onSaveInstanceState", activityTag)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        PrintLog.d(javaClass.simpleName, "onRestoreInstanceState", activityTag)
        super.onRestoreInstanceState(savedInstanceState)
    }
}