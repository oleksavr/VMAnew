package com.example.root.vma.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.root.vma.database.VisitsDbSchema.VisitTable;

public class VisitBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "visitBase.db";

    public VisitBaseHelper (Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + VisitTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                VisitTable.Cols.UUID + ", " +
                VisitTable.Cols.TITLE + ", " +
                VisitTable.Cols.DESCRIPTION + ", " +
                VisitTable.Cols.DATE + ", " +
                VisitTable.Cols.SOLVED + ", " +
                VisitTable.Cols.OWNER +
                ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
