package com.example.root.vma.controller;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.root.vma.R;
import com.example.root.vma.model.Visit;

import java.util.List;
import java.util.UUID;

public class VisitPagerActivity extends AppCompatActivity {

    public static final String EXTRA_VISIT_ID = "com.example.root.vma.visit_id";

    private ViewPager mViewPager;
    private List<Visit> mVisits;

    public static Intent newIntent(Context packageContext, UUID visitId){
        Intent intent = new Intent(packageContext, VisitPagerActivity.class);
        intent.putExtra(EXTRA_VISIT_ID,visitId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_pager);

        UUID visitId = (UUID) getIntent().getSerializableExtra(EXTRA_VISIT_ID);


        mViewPager = (ViewPager) findViewById(R.id.visit_view_pager);

        mVisits = VisitLab.get(this).getVisits();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
               Visit visit = mVisits.get(position);
               return VetFragment.newInstance(visit.getId());
            }

            @Override
            public int getCount() {
               return mVisits.size();
            }
        });

        for (int i = 0; i < mVisits.size(); i++){
            if(mVisits.get(i).getId().equals(visitId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
