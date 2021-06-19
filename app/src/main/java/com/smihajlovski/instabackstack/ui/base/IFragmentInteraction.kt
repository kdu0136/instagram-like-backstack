package com.smihajlovski.instabackstack.ui.base

import android.os.Bundle

interface IFragmentInteraction {
    fun onInteractionCallback(
            actionBundle: Bundle,
            fragmentBundle: Bundle?
    )
}