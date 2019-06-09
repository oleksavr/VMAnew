package com.example.root.vma.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.vma.R;
import com.example.root.vma.model.Visit;

import java.util.List;

public class VetListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mVisitRecyclerView;
    private VisitAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;


    /**
     *
     * Required interface for hosting activities
     */

    public interface Callbacks{
        void onVisitSelected(Visit visit);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_vet_list, container,false);
       mVisitRecyclerView = (RecyclerView) view
               .findViewById(R.id.visit_recycler_view);
       mVisitRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

       if(savedInstanceState != null){
           mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
       }

       updateUI();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_visit_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_visit:
                Visit visit = new Visit();
                VisitLab.get(getActivity()).addVisit(visit);

                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        VisitLab visitLab = VisitLab.get(getActivity());
        int visitCount = visitLab.getVisits().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,visitCount,visitCount);


        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI(){
        VisitLab visitLab = VisitLab.get(getActivity());
        List<Visit> visits = visitLab.getVisits();

        if(mAdapter == null) {
            mAdapter = new VisitAdapter(visits);
            mVisitRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setVisits(visits);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }



    private class VisitHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Visit mVisit;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public VisitHolder (LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_visit,parent,false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.visit_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.visit_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.visit_solved);
        }

        public void bind(Visit visit){
            mVisit = visit;
            mTitleTextView.setText(mVisit.getTitle());
            mDateTextView.setText(mVisit.getDate().toString());
            mSolvedImageView.setVisibility(visit.isSolved() ? View.VISIBLE : View.GONE);

        }


        @Override
        public void onClick(View v) {
           Intent intent = VisitPagerActivity.newIntent(getActivity(),mVisit.getId());
           startActivity(intent);
        }
    }




    private class VisitAdapter extends RecyclerView.Adapter<VisitHolder>{
        private List<Visit> mVisits;
        public VisitAdapter(List<Visit> visits){
            mVisits = visits;
        }

        @NonNull
        @Override
        public VisitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new VisitHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull VisitHolder holder, int position) {
            Visit visit = mVisits.get(position);
            holder.bind(visit);
        }

        @Override
        public int getItemCount() {
            return mVisits.size();
        }

        public void setVisits (List<Visit> visits){
            mVisits = visits;
        }


    }


}
