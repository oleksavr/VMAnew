package com.example.root.vma.controller;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.root.vma.R;
import com.example.root.vma.model.Visit;

public class VetListActivity extends SingleFragmentActivity implements VetListFragment.Callbacks, VetFragment.Callbacks{
    @Override
    protected Fragment createFragment() {
        return new VetListFragment();
    }

    @Override
    protected int getLayoutResId() {
      //  return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onVisitSelected(Visit visit) {
        if(findViewById(R.id.detail_fragment_container) == null ){
            Intent intent = VisitPagerActivity.newIntent(this,visit.getId());
            startActivity(intent);
        }else{
            Fragment newDetail = VetFragment.newInstance(visit.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }

    }

    @Override
    public void onVisitUpdated(Visit visit) {

        VetListFragment listFragment = (VetListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();

    }
}
