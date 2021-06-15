package com.smihajlovski.instabackstack.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class BaseFragment2 extends Fragment {

    protected IFragmentInteraction fragmentInteractionCallback;
    protected static String currentTab;

    @Override
    public void onAttach(Context context) {
        printFragmentLifecycle("onAttach");
        super.onAttach(context);
        try {
            fragmentInteractionCallback = (IFragmentInteraction) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.toString() + " must implement " + IFragmentInteraction.class.getName());
        }
    }

    @Override
    public void onDetach() {
        printFragmentLifecycle("onDetach");
        fragmentInteractionCallback = null;
        super.onDetach();
    }

    public static void setCurrentTab(String currentTab) {
        BaseFragment2.currentTab = currentTab;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        printFragmentLifecycle("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        printFragmentLifecycle("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        printFragmentLifecycle("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        printFragmentLifecycle("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        printFragmentLifecycle("onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        printFragmentLifecycle("onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        printFragmentLifecycle("onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        printFragmentLifecycle("onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        printFragmentLifecycle("onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        printFragmentLifecycle("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        printFragmentLifecycle("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        printFragmentLifecycle("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    void printFragmentLifecycle(String name) {
        Log.d("Test", getClass().getSimpleName() + " => " + name);
    }
}
