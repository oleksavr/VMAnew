package com.example.root.vma.controller;

import android.support.v4.app.Fragment;

public class VetListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new VetListFragment();
    }
}
