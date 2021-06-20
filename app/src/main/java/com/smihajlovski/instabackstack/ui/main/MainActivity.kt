package com.smihajlovski.instabackstack.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.databinding.ActivityMainBinding
import com.smihajlovski.instabackstack.ui.base.BaseActivity
import com.smihajlovski.instabackstack.utils.FragmentStackManager
import com.smihajlovski.instabackstack.utils.FragmentUtils
import com.smihajlovski.instabackstack.utils.IFragmentInteraction
import java.util.*

enum class NavigationMenuType { HOME, DASH_BOARD, NOTIFICATION }
enum class FragmentType { HOME, DASH_BOARD, NOTIFICATION, POST }

class MainActivity :
        BaseActivity<ActivityMainBinding>(resId = R.layout.activity_main),
        IFragmentInteraction {

    private val fragmentStackManager: FragmentStackManager<NavigationMenuType>

    init {
        val tabs: MutableList<NavigationMenuType> = ArrayList()
        tabs.add(NavigationMenuType.HOME)
        tabs.add(NavigationMenuType.DASH_BOARD)
        tabs.add(NavigationMenuType.NOTIFICATION)

        val tabFragments = HashMap<NavigationMenuType, Fragment>()
        tabFragments[NavigationMenuType.HOME] = HomeFragment.newFragment()
        tabFragments[NavigationMenuType.DASH_BOARD] = DashboardFragment.newFragment()
        tabFragments[NavigationMenuType.NOTIFICATION] = NotificationFragment.newFragment()

        fragmentStackManager = FragmentStackManager(
                R.id.frame_layout,
                supportFragmentManager,
                tabFragments,
                NavigationMenuType.HOME,
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

    override fun onInteractionCallback(
            actionBundle: Bundle,
            fragmentBundle: Bundle?
    ) {
        val action: FragmentType =
                (actionBundle.getSerializable(FragmentUtils.ACTION) as? FragmentType) ?: return
        val enterAnimations: FragmentStackManager.FragmentAnimation? =
                actionBundle.getSerializable(FragmentUtils.ENTER_ANIMATION) as? FragmentStackManager.FragmentAnimation
        val exitAnimations: FragmentStackManager.FragmentAnimation? =
                actionBundle.getSerializable(FragmentUtils.EXIT_ANIMATION) as? FragmentStackManager.FragmentAnimation
        val fragment: Fragment = when (action) {
            FragmentType.HOME -> HomeFragment.newFragment()
            FragmentType.DASH_BOARD -> DashboardFragment.newFragment()
            FragmentType.NOTIFICATION -> NotificationFragment.newFragment()
            FragmentType.POST -> PostFragment.newFragment(bundle = fragmentBundle)
        }
        fragmentStackManager.showFragment(
                fragment = fragment,
                enterAnimations = enterAnimations,
                exitAnimations = exitAnimations,
        )
    }

    override fun onBackPressed() {
        fragmentStackManager.resolveBackPressed(
                finish = this::finish,
                changeTabType = {
                    val selectItemId = when (it) {
                        NavigationMenuType.HOME -> R.id.tab_home
                        NavigationMenuType.DASH_BOARD -> R.id.tab_dashboard
                        NavigationMenuType.NOTIFICATION -> R.id.tab_notifications
                    }
                    binding.bottomNavigationView.selectedItemId = selectItemId
                }
        )
    }
}