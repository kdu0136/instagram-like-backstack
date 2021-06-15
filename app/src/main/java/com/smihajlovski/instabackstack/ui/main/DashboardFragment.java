package com.smihajlovski.instabackstack.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.smihajlovski.instabackstack.R;
import com.smihajlovski.instabackstack.databinding.FragmentDashboardBinding;
import com.smihajlovski.instabackstack.tmp.Dummy;
import com.smihajlovski.instabackstack.tmp.FeedMainAdapter;
import com.smihajlovski.instabackstack.ui.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

import static com.smihajlovski.instabackstack.common.Constants.DASHBOARD_FRAGMENT;
import static com.smihajlovski.instabackstack.common.Constants.EXTRA_IS_ROOT_FRAGMENT;
import static com.smihajlovski.instabackstack.utils.FragmentUtils.sendActionToActivity;

public class DashboardFragment extends BaseFragment {

    static final String ACTION_NOTIFICATION = DASHBOARD_FRAGMENT + "action.notification";
    private FragmentDashboardBinding binder;
    private final FeedMainAdapter adapter = new FeedMainAdapter();

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(boolean isRoot) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot);
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        init();
        return binder.getRoot();
    }

    private void init() {
        binder.button.setOnClickListener(v -> sendActionToActivity(ACTION_NOTIFICATION, currentTab, true, fragmentInteractionCallback));
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binder.recyclerView.setAdapter(adapter);

        adapter.submitList(Dummy.INSTANCE.getMainFeed(1, 200));
    }

}
