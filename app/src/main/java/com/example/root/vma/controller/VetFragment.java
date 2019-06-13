package com.example.root.vma.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.Toast;

import com.example.root.vma.R;
import com.example.root.vma.model.Visit;


import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;
import static com.example.root.vma.controller.VetListFragment.*;

public class VetFragment extends Fragment {

    public static final String ARG_VISIT_ID = "visit_id";
    public static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;


    private Visit mVisit;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mOwnerButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;


    /**
     *
     * Required interface for hosting activities
     */

    public interface Callbacks {
        void onVisitUpdated(Visit visit);
    }


    public static VetFragment newInstance(UUID visitID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_VISIT_ID, visitID);

        VetFragment fragment = new VetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       UUID visitID = (UUID) getArguments().getSerializable(ARG_VISIT_ID);
        mVisit = VisitLab.get(getActivity()).getVisit(visitID);
        mPhotoFile = VisitLab.get(getActivity()).getPhotoFile(mVisit);

    }

    @Override
    public void onPause() {
        super.onPause();

        VisitLab.get(getActivity())
                .updateVisit(mVisit);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
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
                updateVisit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDetailsField = (EditText) v.findViewById(R.id.vet_details);

            mDetailsField.setText(mVisit.getDetails());
       // Toast toast = Toast.makeText(getContext(),mVisit.getDetails(),Toast.LENGTH_LONG);
      //  toast.show();

        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mVisit.setDetails(charSequence.toString());
                    updateVisit();
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
                updateVisit();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.visit_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                  Intent i = new Intent(Intent.ACTION_SEND);
                  i.setType("text/plain");
                  i.putExtra(Intent.EXTRA_TEXT,getVisitReport());
                  i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.visit_report_subject));
                  i = Intent.createChooser(i, getString(R.string.send_report));
                  startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mOwnerButton = (Button) v.findViewById(R.id.animal_owner);
        mOwnerButton.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivityForResult(pickContact,REQUEST_CONTACT);
                                            }
                                        }
        );

        if (mVisit.getOwner() != null){
            mOwnerButton.setText(mVisit.getOwner());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY)== null){
            mOwnerButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.pet_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.root.vma.fileprovider",mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage,REQUEST_PHOTO);

            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.pet_photo);
        updatePhotoView();

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
            updateVisit();
            updateDate();
        } else  if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String [] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri,queryFields,null,null,null);
            try{
                if(c.getCount() ==0 ){
                    return;
            }

            c.moveToFirst();
            String owner = c.getString(0);
            mVisit.setOwner(owner);
            updateVisit();
            mOwnerButton.setText(owner);
        }finally {
                c.close();
            }

        }else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.root.vma.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateVisit();
            updatePhotoView();
        }
    }


    private void updateVisit(){
        VisitLab.get(getActivity()).updateVisit(mVisit);
        mCallbacks.onVisitUpdated(mVisit);
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

    private void  updatePhotoView (){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
