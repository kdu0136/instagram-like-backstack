package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants
import com.smihajlovski.instabackstack.databinding.ActivityMainBinding
import com.smihajlovski.instabackstack.ui.base.BaseActivity
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction
import com.smihajlovski.instabackstack.utils.FragmentStackManager
import com.smihajlovski.instabackstack.utils.FragmentUtils
import com.smihajlovski.instabackstack.utils.FragmentUtils.FragmentDirection
import java.util.*

class MainActivity :
    BaseActivity<ActivityMainBinding>(resId = R.layout.activity_main), IFragmentInteraction {

    private val fragmentStackManager: FragmentStackManager<FragmentDirection>

    init {
        val tabs: MutableList<FragmentDirection> = ArrayList()
        tabs.add(FragmentDirection.HOME)
        tabs.add(FragmentDirection.DASH_BOARD)
        tabs.add(FragmentDirection.NOTIFICATION)

        val tabFragments = HashMap<FragmentDirection, Fragment>()
        tabFragments[FragmentDirection.HOME] = HomeFragment.newFragment(true)
        tabFragments[FragmentDirection.DASH_BOARD] = DashboardFragment.newFragment(true)
        tabFragments[FragmentDirection.NOTIFICATION] = NotificationFragment.newFragment(true)

        fragmentStackManager = FragmentStackManager(
            supportFragmentManager,
            tabFragments,
            tabs
        )
    }

    override fun onSetupUI() {
        with(binding.bottomNavigationView) {
            inflateMenu(R.menu.bottom_nav_tabs)
            setOnNavigationItemSelectedListener { menu ->
                val tabType: FragmentDirection? = when (menu.itemId) {
                    R.id.tab_home -> FragmentDirection.HOME
                    R.id.tab_dashboard -> FragmentDirection.DASH_BOARD
                    R.id.tab_notifications -> FragmentDirection.NOTIFICATION
                    else -> null
                }
                if (tabType != null)
                    fragmentStackManager.selectTab(tabType = tabType)

                tabType != null
            }
            selectedItemId = R.id.tab_home
            setOnNavigationItemReselectedListener { menu ->
                when (menu.itemId) {
                    R.id.tab_home,
                    R.id.tab_dashboard,
                    R.id.tab_notifications -> fragmentStackManager.popStackExceptFirst()
                }
            }
        }
    }

    override fun observeViewModel() {
    }

    override fun onInteractionCallback(bundle: Bundle) {
        val action: FragmentDirection =
            (bundle.getSerializable(Constants.ACTION) as? FragmentDirection) ?: return
        val fragment: Fragment? = when (action) {
            FragmentDirection.HOME -> null
            FragmentDirection.DASH_BOARD -> DashboardFragment.newFragment(isRoot = false)
            FragmentDirection.NOTIFICATION -> NotificationFragment.newFragment(isRoot = false)
        }
        if (fragment != null)
            fragmentStackManager.showFragment(bundle = bundle, fragment = fragment)
    }

    override fun onBackPressed() {
        fragmentStackManager.resolveBackPressed(
            finish = this::finish,
            selectItemId = binding.bottomNavigationView::setSelectedItemId
        )
    }
}