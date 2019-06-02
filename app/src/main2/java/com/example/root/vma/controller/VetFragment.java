package com.example.root.vma.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.root.vma.R;
import com.example.root.vma.model.Visit;


import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class VetFragment extends Fragment {

    public static final String ARG_VISIT_ID = "visit_id";
    public static final String DIALOG_DATE = "DialogDate";

    public static final int REQUEST_DATE = 0;


    private Visit mVisit;
    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;


    public static VetFragment newInstance(UUID visitID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_VISIT_ID, visitID);

        VetFragment fragment = new VetFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       UUID visitID = (UUID) getArguments().getSerializable(ARG_VISIT_ID);
        mVisit = VisitLab.get(getActivity()).getVisit(visitID);

    }

    @Override
    public void onPause() {
        super.onPause();

        VisitLab.get(getActivity())
                .updateVisit(mVisit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vet, container,false);

        mTitleField = (EditText) v.findViewById(R.id.vet_title);
        mTitleField.setText(mVisit.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVisit.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDetailsField = (EditText) v.findViewById(R.id.vet_details);
        mDetailsField.setText(mVisit.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mVisit.setDetails(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mDateButton = (Button) v.findViewById(R.id.vet_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View view) {
               FragmentManager fragmentManager = getFragmentManager();
               DatePickerFragment dialog = DatePickerFragment
                       .newInstance(mVisit.getDate());
               dialog.setTargetFragment(VetFragment.this,REQUEST_DATE);
               dialog.show(fragmentManager,DIALOG_DATE);

           }
       });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.vet_solved);
        mSolvedCheckBox.setChecked(mVisit.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mVisit.setSolved(isChecked);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mVisit.setDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        mDateButton.setText(mVisit.getDate().toString());
    }

    private String getVisitReport(){
        String solvedString = null;
        if(mVisit.isSolved()){
            solvedString = getString(R.string.visit_report_solved);
        }else {
            solvedString  = getString(R.string.visit_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mVisit.getDate()).toString();
        String owner = mVisit.getOwner();
        if(owner == null){
            owner = getString(R.string.visit_report_no_owner);
        }else {
            owner  = getString(R.string.visit_report_owner,owner);
        }
        String report = getString(R.string.visit_report,mVisit.getTitle(),dateString,solvedString,owner);
        return report;
    }
}
