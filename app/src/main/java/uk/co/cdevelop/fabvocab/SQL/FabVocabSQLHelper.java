package uk.co.cdevelop.fabvocab.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chris on 16/01/2017.
 */

public class FabVocabSQLHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "FabVocab.db";
    private static FabVocabSQLHelper instance = null;

    private FabVocabSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    public static FabVocabSQLHelper getInstance(Context context) {
        if(instance == null) {
            instance = new FabVocabSQLHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FabVocabContract.DefinitionEntry.SQL_CREATE_TABLE);
        db.execSQL(FabVocabContract.WordEntry.SQL_CREATE_TABLE);
        db.execSQL(FabVocabContract.WordPractice.SQL_CREATE_TABLE);
        db.execSQL(FabVocabContract.AddWordLater.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FabVocabContract.DefinitionEntry.SQL_DELETE_TABLE);
        db.execSQL(FabVocabContract.WordEntry.SQL_DELETE_TABLE);
        db.execSQL(FabVocabContract.WordPractice.SQL_DELETE_TABLE);
        db.execSQL(FabVocabContract.AddWordLater.SQL_DELETE_TABLE);
        onCreate(db);
    }

    public void purge() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 0, 0);
    }

    public static String getSQLTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }


     //******************** Common SQL Operations ******************//
    public static int getWord(String word, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordEntry._ID + " fROM " + FabVocabContract.WordEntry.TABLE_NAME + " WHERE " + FabVocabContract.WordEntry.COLUMN_NAME_WORD + " = ?", new String[]{word});
        if(cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordEntry._ID));
        } else {
            return -1;
        }
    }

    public static ArrayList<String> getDefinitions(int wordId, SQLiteDatabase db) {
        ArrayList<String> definitions = new ArrayList<String>();

        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION
                                  + " FROM " + FabVocabContract.DefinitionEntry.TABLE_NAME
                                  + " WHERE " + FabVocabContract.DefinitionEntry.COLUMN_WORD_ID + " = ? ", new String[]{Integer.toString(wordId)});

        while(cursor.moveToNext()) {
            definitions.add(cursor.getString(cursor.getColumnIndex(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION)));
        }
        return definitions;
    }

    public static int addWord(String word, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        db.delete(FabVocabContract.AddWordLater.TABLE_NAME, FabVocabContract.AddWordLater.COLUMN_NAME_WORD + " = ?", new String[]{word});

        values.put("word", word);
        values.put("added", FabVocabSQLHelper.getSQLTimestamp());
        return (int) db.insert("words", null, values);
    }

    public static ArrayList<String> getAllWords(SQLiteDatabase db) {
        ArrayList<String> allWords = new ArrayList<String>();

        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordEntry.COLUMN_NAME_WORD
                + " FROM " + FabVocabContract.WordEntry.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            allWords.add(cursor.getString(cursor.getColumnIndex(FabVocabContract.WordEntry.COLUMN_NAME_WORD)));
        }
        return allWords;
    }
}
