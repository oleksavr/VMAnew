package com.example.root.vma.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.root.vma.database.VisitBaseHelper;
import com.example.root.vma.database.VisitCursorWrapper;
import com.example.root.vma.database.VisitsDbSchema.VisitTable;
import com.example.root.vma.model.Visit;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class VisitLab {

    private static VisitLab sVisitLab;


    private Context mContext;
    private SQLiteDatabase mDatabase;



    private VisitLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new VisitBaseHelper(mContext)
                .getWritableDatabase();


    }

    public static VisitLab get(Context context){

        if(sVisitLab == null){
            sVisitLab = new VisitLab(context);
        }
        return sVisitLab;
    }


    public void addVisit(Visit v){

        ContentValues values = getContentValues(v);
        mDatabase.insert(VisitTable.NAME, null,values);


    }

    public List<Visit> getVisits(){
       List<Visit> visits = new ArrayList<>();

       VisitCursorWrapper cursorWrapper = queryVisits(null,null);

       try{
           cursorWrapper.moveToFirst();
           while (!cursorWrapper.isAfterLast()){
               visits.add(cursorWrapper.getVisit());
               cursorWrapper.moveToNext();
           }
       } finally {
           cursorWrapper.close();
       }

       return visits;
    }

    public Visit getVisit(UUID id){
        VisitCursorWrapper cursorWrapper = queryVisits(
                VisitTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursorWrapper.getCount() == 0 ){
                return null;
            }

            cursorWrapper.moveToFirst();
            return cursorWrapper.getVisit();
        }finally {
            cursorWrapper.close();
        }
    }

    public File getPhotoFile(Visit visit){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,visit.getPhotoFilename());
    }

    public void  updateVisit(Visit visit){
        String uuidString = visit.getId().toString();
        ContentValues values = getContentValues(visit);

        mDatabase.update(VisitTable.NAME,values,
                VisitTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    private VisitCursorWrapper queryVisits(String whereCluse, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                VisitTable.NAME,
                null, //null select all columns
                whereCluse,
                whereArgs,
                null,
                null,
                null
        );

        return new VisitCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Visit visit){
        ContentValues values = new ContentValues();
        values.put(VisitTable.Cols.UUID, visit.getId().toString());
        values.put(VisitTable.Cols.TITLE,visit.getTitle());
        values.put(VisitTable.Cols.DESCRIPTION,visit.getDetails());
        values.put(VisitTable.Cols.DATE,visit.getDate().getTime());
        values.put(VisitTable.Cols.SOLVED,visit.isSolved() ? 1 : 0);
        values.put(VisitTable.Cols.OWNER, visit.getOwner());

        return values;

    }
}
