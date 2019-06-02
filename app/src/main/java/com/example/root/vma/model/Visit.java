package com.example.root.vma.model;

import java.util.Date;
import java.util.UUID;

public class Visit {

    private UUID mId;
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private boolean mSolved;
    private String mOwner;

    public Visit(){
       this(UUID.randomUUID());
    }

    public Visit(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }



    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDetails(String details ) {
        mDetails = details;
    }

    public String getDetails(){
        return mDetails;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    public String getPhotoFilename (){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
