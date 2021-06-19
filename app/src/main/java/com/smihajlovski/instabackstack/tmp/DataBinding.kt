package com.smihajlovski.instabackstack.tmp

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <VB : ViewDataBinding> createDataBinding(
        activity: Activity,
        @LayoutRes resId: Int
): VB =
        DataBindingUtil.setContentView(activity, resId)

fun <VB : ViewDataBinding> createDataBinding(
        @LayoutRes resId: Int,
        context: Context,
        parent: ViewGroup? = null,
        attachToParent: Boolean = false,
): VB =
        DataBindingUtil.inflate(LayoutInflater.from(context), resId, parent, attachToParent)