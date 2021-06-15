package com.smihajlovski.instabackstack.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.smihajlovski.instabackstack.R;
import com.smihajlovski.instabackstack.databinding.FragmentHomeBinding;
import com.smihajlovski.instabackstack.utils.FragmentUtils;
import com.smihajlovski.instabackstack.ui.base.BaseFragment2;

import static com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT;
import static com.smihajlovski.instabackstack.common.Constants.HOME_FRAGMENT;

public class HomeFragment2 extends BaseFragment2 {

    static final String ACTION_DASHBOARD = HOME_FRAGMENT + "action.dashboard";
    private FragmentHomeBinding binder;

    public HomeFragment2() {
    }

    public static HomeFragment2 newInstance(boolean isRoot) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot);
        HomeFragment2 fragment = new HomeFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        init();
        return binder.getRoot();
    }

    private void init() {
        binder.button.setOnClickListener(v -> FragmentUtils.INSTANCE.sendActionToActivity(ACTION_DASHBOARD, true, fragmentInteractionCallback));
    }
}
