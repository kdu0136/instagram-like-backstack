package com.smihajlovski.instabackstack.ui.base

import android.os.Bundle
import android.view.View

interface IFragmentInteraction {
    fun onInteractionCallback(bundle: Bundle, view: View? = null)
}