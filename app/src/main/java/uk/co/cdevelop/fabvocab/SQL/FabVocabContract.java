package uk.co.cdevelop.fabvocab.SQL;

import android.provider.BaseColumns;

/**
 * Created by Chris on 16/01/2017.
 */

public class FabVocabContract {
    private FabVocabContract(){}

    public static class DefinitionTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "definitions";
        public static final String COLUMN_WORD_ID = "word_id";
        public static final String COLUMN_NAME_DEFINITION = "definition";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + DefinitionTableEntry._ID + " INTEGER PRIMARY KEY "
                                + ", " + COLUMN_WORD_ID + " INTEGER"
                                + ", " + COLUMN_NAME_DEFINITION + " TEXT"
                                + ")";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class WordTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_ADDED = "added";
        public static final String COLUMN_NAME_AUDIOURL = "audio_url";


        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + WordTableEntry._ID + " INTEGER PRIMARY KEY, "
                                                                                    + COLUMN_NAME_WORD + " TEXT, "
                                                                                    + COLUMN_NAME_ADDED + " TIMESTAMP, "
                                                                                    + COLUMN_NAME_AUDIOURL + " TEXT )";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class WordPracticeTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "word_practice";
        public static final String COLUMN_NAME_WORD_ID = "word_id";
        public static final String COLUMN_NAME_RECALL_RATING = "recall_rating";
        public static final String COLUMN_NAME_FLUENCY_RATING = "fluency_rating";
        public static final String COLUMN_NAME_TIME = "time";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + WordPracticeTableEntry._ID + " INTEGER PRIMARY KEY"
                + ", " + COLUMN_NAME_WORD_ID + " INTEGER"
                + ", " + COLUMN_NAME_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ", " + COLUMN_NAME_RECALL_RATING + " INTEGER"
                + ", " + COLUMN_NAME_FLUENCY_RATING + " INTEGER"
                + ")";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class AddWordLaterTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "addwordlater";
        public static final String COLUMN_NAME_WORD = "word";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + AddWordLaterTableEntry._ID + " INTEGER PRIMARY KEY"
                + ", " + COLUMN_NAME_WORD + " TEXT"
                + ")";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}
