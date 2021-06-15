package com.smihajlovski.instabackstack.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smihajlovski.instabackstack.R;
import com.smihajlovski.instabackstack.databinding.ActivityMainBinding;
import com.smihajlovski.instabackstack.ui.base.BaseFragment2;
import com.smihajlovski.instabackstack.ui.base.IFragmentInteraction;
import com.smihajlovski.instabackstack.utils.FragmentStackManager;
import com.smihajlovski.instabackstack.utils.FragmentUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;

import static com.smihajlovski.instabackstack.common.Constants.ACTION;
import static com.smihajlovski.instabackstack.common.Constants.DATA_KEY_1;
import static com.smihajlovski.instabackstack.common.Constants.DATA_KEY_2;
import static com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT;
import static com.smihajlovski.instabackstack.common.Constants.TAB_DASHBOARD;
import static com.smihajlovski.instabackstack.common.Constants.TAB_HOME;
import static com.smihajlovski.instabackstack.common.Constants.TAB_NOTIFICATIONS;
import static com.smihajlovski.instabackstack.utils.StackListManagerKt.updateStackToIndexFirst;

public class MainActivity2 extends AppCompatActivity implements IFragmentInteraction {

    private FragmentStackManager fragmentStackManager;

    private ActivityMainBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main);
        createStacks();
    }

    @Override
    public void onInteractionCallback(@NotNull Bundle bundle) {
        String action = bundle.getString(ACTION);

        if (action != null) {
            switch (action) {
                case HomeFragment2.ACTION_DASHBOARD:
                case NotificationsFragment2.ACTION_DASHBOARD:
                    fragmentStackManager.showFragment(bundle, DashboardFragment2.newInstance(false));
                    break;
                case DashboardFragment2.ACTION_NOTIFICATION:
                    fragmentStackManager.showFragment(bundle, NotificationsFragment2.newInstance(false));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        fragmentStackManager.resolveBackPressed(
                () -> {
                    finish();
                    return Unit.INSTANCE;
                },
                (id) -> {
                    binder.bottomNavigationView.setSelectedItemId(id);
                    return Unit.INSTANCE;
                }
        );
    }

    private void createStacks() {
        binder.bottomNavigationView.inflateMenu(R.menu.bottom_nav_tabs);
        binder.bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        List<String> tabs = new ArrayList<>();
        tabs.add(TAB_HOME);
        tabs.add(TAB_DASHBOARD);
        tabs.add(TAB_NOTIFICATIONS);

        HashMap<String, Fragment> tabFragments = new HashMap<>();
        tabFragments.put(TAB_HOME, HomeFragment2.newInstance(true));
        tabFragments.put(TAB_DASHBOARD, DashboardFragment2.newInstance(true));
        tabFragments.put(TAB_NOTIFICATIONS, NotificationsFragment2.newInstance(true));

        fragmentStackManager = new FragmentStackManager(
                getSupportFragmentManager(),
                tabFragments,
                tabs
        );

        binder.bottomNavigationView.setSelectedItemId(R.id.tab_home);
        binder.bottomNavigationView.setOnNavigationItemReselectedListener(onNavigationItemReselectedListener);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.tab_home:
                fragmentStackManager.selectTab(TAB_HOME);
                return true;
            case R.id.tab_dashboard:
                fragmentStackManager.selectTab(TAB_DASHBOARD);
                return true;
            case R.id.tab_notifications:
                fragmentStackManager.selectTab(TAB_NOTIFICATIONS);
                return true;
        }
        return false;
    };

    private final BottomNavigationView.OnNavigationItemReselectedListener onNavigationItemReselectedListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.tab_home:
            case R.id.tab_notifications:
            case R.id.tab_dashboard:
                fragmentStackManager.popStackExceptFirst();
                break;
        }
    };
}
