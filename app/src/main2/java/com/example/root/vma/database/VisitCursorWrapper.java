package com.example.root.vma.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.root.vma.model.Visit;

import java.util.Date;
import java.util.UUID;

public class VisitCursorWrapper extends CursorWrapper {

    public VisitCursorWrapper (Cursor cursor){
        super(cursor);
    }

    public Visit getVisit(){
        String uuidString = getString(getColumnIndex(VisitsDbSchema.VisitTable.Cols.UUID));
        String title = getString(getColumnIndex(VisitsDbSchema.VisitTable.Cols.TITLE));
        String description = getString(getColumnIndex(VisitsDbSchema.VisitTable.Cols.DESCRIPTION));
        long date = getLong(getColumnIndex(VisitsDbSchema.VisitTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(VisitsDbSchema.VisitTable.Cols.SOLVED));
        String owner = getString(getColumnIndex(VisitsDbSchema.VisitTable.Cols.OWNER));

        Visit visit = new Visit(UUID.fromString(uuidString));
        visit.setTitle(title);
        visit.setDate(new Date(date));
        visit.setSolved(isSolved !=0);
        visit.setOwner(owner);

        return visit;
    }
}
