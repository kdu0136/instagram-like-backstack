package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants
import com.smihajlovski.instabackstack.databinding.ActivityMainBinding
import com.smihajlovski.instabackstack.ui.base.BaseActivity
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction
import com.smihajlovski.instabackstack.utils.FragmentStackManager
import java.util.*

enum class NavigationMenuType { HOME, DASH_BOARD, NOTIFICATION }

class MainActivity :
    BaseActivity<ActivityMainBinding>(resId = R.layout.activity_main), IFragmentInteraction {

    private val fragmentStackManager: FragmentStackManager<NavigationMenuType>

    init {
        val tabs: MutableList<NavigationMenuType> = ArrayList()
        tabs.add(NavigationMenuType.HOME)
        tabs.add(NavigationMenuType.DASH_BOARD)
        tabs.add(NavigationMenuType.NOTIFICATION)

        val tabFragments = HashMap<NavigationMenuType, Fragment>()
        tabFragments[NavigationMenuType.HOME] = HomeFragment.newFragment(true)
        tabFragments[NavigationMenuType.DASH_BOARD] = DashboardFragment.newFragment(true)
        tabFragments[NavigationMenuType.NOTIFICATION] = NotificationFragment.newFragment(true)

        fragmentStackManager = FragmentStackManager(
            R.id.frame_layout,
            supportFragmentManager,
            tabFragments,
            tabs
        )
    }

    override fun onSetupUI() {
        with(binding.bottomNavigationView) {
            inflateMenu(R.menu.bottom_nav_tabs)
            setOnNavigationItemSelectedListener { menu ->
                val tabType: NavigationMenuType? = when (menu.itemId) {
                    R.id.tab_home -> NavigationMenuType.HOME
                    R.id.tab_dashboard -> NavigationMenuType.DASH_BOARD
                    R.id.tab_notifications -> NavigationMenuType.NOTIFICATION
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

    override fun onInteractionCallback(bundle: Bundle, view: View?) {
        val action: NavigationMenuType =
            (bundle.getSerializable(Constants.ACTION) as? NavigationMenuType) ?: return
        val fragment: Fragment? = when (action) {
            NavigationMenuType.HOME -> null
            NavigationMenuType.DASH_BOARD -> DashboardFragment.newFragment(isRoot = false)
            NavigationMenuType.NOTIFICATION -> NotificationFragment.newFragment(isRoot = false)
//            FragmentType.POST -> PostFragment.newFragment(isRoot = false, image = bundle.getInt("image"))
        }
        if (fragment != null) {
            fragmentStackManager.showFragment(
                bundle = bundle,
                fragment = fragment,
                shareView = view
            )
        }
    }

    override fun onBackPressed() {
        fragmentStackManager.resolveBackPressed(
            finish = this::finish,
            currentTabType = {
                val selectItemId = when (it) {
                    NavigationMenuType.HOME -> R.id.tab_home
                    NavigationMenuType.DASH_BOARD -> R.id.tab_dashboard
                    NavigationMenuType.NOTIFICATION -> R.id.tab_notifications
                    else -> null
                } ?: return@resolveBackPressed
                binding.bottomNavigationView.selectedItemId = selectItemId
            }
        )
    }
}