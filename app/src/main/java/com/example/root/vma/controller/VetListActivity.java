package com.example.root.vma.controller;

import android.support.v4.app.Fragment;

import com.example.root.vma.R;

public class VetListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new VetListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_twopane;
    }
}
