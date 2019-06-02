package com.example.root.vma.database;

public class VisitsDbSchema {
    public static final class VisitTable{
        public static final String NAME = "visits";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "description";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String OWNER = "owner";
        }
    }
}
