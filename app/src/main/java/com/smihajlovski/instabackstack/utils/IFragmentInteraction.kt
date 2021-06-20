package com.smihajlovski.instabackstack.utils

import android.os.Bundle

interface IFragmentInteraction {
    fun onInteractionCallback(
            actionBundle: Bundle,
            fragmentBundle: Bundle?
    )
}