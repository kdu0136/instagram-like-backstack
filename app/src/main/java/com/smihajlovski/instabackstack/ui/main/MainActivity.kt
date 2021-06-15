package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.databinding.ActivityMainBinding
import com.smihajlovski.instabackstack.ui.base.BaseActivity
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction

class MainActivity:
    BaseActivity<ActivityMainBinding>(resId = R.layout.activity_main), IFragmentInteraction {
    override fun onSetupUI() {
    }

    override fun observeViewModel() {
    }

    override fun onInteractionCallback(bundle: Bundle) {
    }
}