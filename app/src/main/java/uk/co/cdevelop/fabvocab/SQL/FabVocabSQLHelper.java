package uk.co.cdevelop.fabvocab.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.cdevelop.fabvocab.DataModels.WordStatistics;
import uk.co.cdevelop.fabvocab.SQL.Models.DefinitionEntry;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;

/**
 * Created by Chris on 16/01/2017.
 */

public class FabVocabSQLHelper extends SQLiteOpenHelper{

    public static int ORDERBY_NONE = 0;
    public static int ORDERBY_ASC = 1;
    public static int ORDERBY_DESC = 2;


    private static final String DATABASE_NAME = "FabVocab.db";
    private static FabVocabSQLHelper instance = null;
    private SQLiteDatabase db;
    private static Context context;

    private FabVocabSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.db = getWritableDatabase();

    }

    public static String getDatabaseFilename() {
        return DATABASE_NAME;
    }

    public static FabVocabSQLHelper getInstance(Context newContext) {
        if(instance == null || context == null || !instance.getContext().equals(newContext) || !instance.getReadableDatabase().isOpen()) {
            instance = new FabVocabSQLHelper(newContext);
            context = newContext;
        }

        return instance;
    }

    private static Context getContext() {
        return context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FabVocabContract.DefinitionTableEntry.SQL_CREATE_TABLE);
        db.execSQL(FabVocabContract.WordTableEntry.SQL_CREATE_TABLE);
        db.execSQL(FabVocabContract.WordPracticeTableEntry.SQL_CREATE_TABLE);
        db.execSQL(FabVocabContract.AddWordLaterTableEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FabVocabContract.DefinitionTableEntry.SQL_DELETE_TABLE);
        db.execSQL(FabVocabContract.WordTableEntry.SQL_DELETE_TABLE);
        db.execSQL(FabVocabContract.WordPracticeTableEntry.SQL_DELETE_TABLE);
        db.execSQL(FabVocabContract.AddWordLaterTableEntry.SQL_DELETE_TABLE);
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

    public boolean existsInAddWordsLater(String word) {
        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD + " FROM " + FabVocabContract.AddWordLaterTableEntry.TABLE_NAME + " WHERE " + FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD + " = ?", new String[]{word.toLowerCase()});
        return cursor.getCount() > 0;

    }

    public int getWordId(String word) {
         Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordTableEntry._ID + " fROM " + FabVocabContract.WordTableEntry.TABLE_NAME + " WHERE " + FabVocabContract.WordTableEntry.COLUMN_NAME_WORD + " = ?", new String[]{word});
         if(cursor.getCount() > 0) {
             cursor.moveToNext();
             return cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordTableEntry._ID));
         } else {
             return -1;
         }
     }

    public WordEntry getWord(int word_id) {
        WordEntry result = null;
        try {
            Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordTableEntry._ID + ", "
                    + FabVocabContract.WordTableEntry.COLUMN_NAME_WORD + ", "
                    + FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED + ", "
                    + FabVocabContract.WordTableEntry.COLUMN_NAME_AUDIOURL
                    + " FROM "
                    + FabVocabContract.WordTableEntry.TABLE_NAME
                    + " WHERE "
                    + FabVocabContract.WordTableEntry._ID + " = ?", new String[]{Integer.toString(word_id)});
            if (cursor.getCount() > 0) {
                cursor.moveToNext();

                int id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordTableEntry._ID));
                String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_WORD));
                long added = cursor.getLong(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED));
                String audioUrl = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_AUDIOURL));

                result = new WordEntry(id, word, added, audioUrl);
            }
        } catch (Exception e) {
            Log.e("SQL", e.getStackTrace()[0].getMethodName() + ":" + e.getMessage());
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public int addWord(String word) {
        return addWord(word, "");
    }

    public int addWord(String word, String audioUrl) {
        ContentValues values = new ContentValues();

        db.delete(FabVocabContract.AddWordLaterTableEntry.TABLE_NAME, FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD + " = ?", new String[]{word});

        values.put("word", word.trim().toLowerCase());
        values.put("added", FabVocabSQLHelper.getSQLTimestamp());
        values.put("audio_url", audioUrl);
        return (int) db.insert("words", null, values);
    }

    public void deleteWord(int id) {
        db.delete(FabVocabContract.DefinitionTableEntry.TABLE_NAME, "word_id=?", new String[]{Integer.toString(id)});
        db.delete(FabVocabContract.WordTableEntry.TABLE_NAME, "_id=?", new String[]{Integer.toString(id)});
    }

    public ArrayList<DefinitionEntry> getAllDefinitions(int wordId) {
        ArrayList<DefinitionEntry> definitions = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT "
                + FabVocabContract.DefinitionTableEntry._ID + ", "
                + FabVocabContract.DefinitionTableEntry.COLUMN_NAME_DEFINITION
                + " FROM " + FabVocabContract.DefinitionTableEntry.TABLE_NAME
                + " WHERE " + FabVocabContract.DefinitionTableEntry.COLUMN_WORD_ID + " = ? ", new String[]{Integer.toString(wordId)});

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.DefinitionTableEntry._ID));
            String definition = cursor.getString(cursor.getColumnIndex(FabVocabContract.DefinitionTableEntry.COLUMN_NAME_DEFINITION));
            definitions.add(new DefinitionEntry(id, definition));
        }
        return definitions;
    }

    public ArrayList<WordEntry> getRecentlyAddedWords() { return getRecentlyAddedWords(-1); }
    public ArrayList<WordEntry> getRecentlyAddedWords(int limit) {
        ArrayList<WordEntry> wordsList = new ArrayList<>();

        String limitClause = "";
        if(limit > 0) {
            limitClause = "LIMIT " + Integer.toString(limit);
        }

        Cursor cursor = db.rawQuery("SELECT "
                                + FabVocabContract.WordTableEntry._ID + ", "
                                + FabVocabContract.WordTableEntry.COLUMN_NAME_WORD + ", "
                                + FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED
                                + " FROM " + FabVocabContract.WordTableEntry.TABLE_NAME
                                + " WHERE added > datetime('now', '-7 days') ORDER BY added DESC " + limitClause, null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordTableEntry._ID));
            String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_WORD));
            long added = cursor.getLong(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED));

            wordsList.add(new WordEntry(id, word, added, ""));
        }

        return wordsList;
    }

    public ArrayList<WordEntry> getAllWords() {
        return getAllWords(0);
    }

    public ArrayList<WordEntry> getAllWords(int orderByWord) {

        ArrayList<WordEntry> allWords = new ArrayList<>();

        String orderByClause = "";
        if(orderByWord == ORDERBY_ASC) {
            orderByClause = " ORDER BY " + FabVocabContract.WordTableEntry.COLUMN_NAME_WORD + " ASC";
        } else if(orderByWord == ORDERBY_DESC) {
            orderByClause = " ORDER BY " + FabVocabContract.WordTableEntry.COLUMN_NAME_WORD + " DESC";
        }

        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordTableEntry._ID + ", "
                                              + FabVocabContract.WordTableEntry.COLUMN_NAME_WORD + ", "
                                              + FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED + ", "
                                              + FabVocabContract.WordTableEntry.COLUMN_NAME_AUDIOURL
                                              + " FROM "
                                              + FabVocabContract.WordTableEntry.TABLE_NAME
                                              + orderByClause, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordTableEntry._ID));
            String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_WORD));
            long added = cursor.getLong(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED));
            String audioUrl = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_AUDIOURL));

            allWords.add(new WordEntry(id, word, added, audioUrl));
        }
        return allWords;
    }

    public ArrayList<WordEntry> getFilteredWords(char c) {
        // TODO: Bound c to alphanumeric characters and acceptable symbols
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE word LIKE '" + c + "%' or word LIKE '" + Character.toString((char)(((int) c) + 32)) + "%'  ORDER BY word ASC", null);
        ArrayList<WordEntry> wordsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordTableEntry._ID));
            String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_WORD));
            long added = cursor.getLong(cursor.getColumnIndex(FabVocabContract.WordTableEntry.COLUMN_NAME_ADDED));
            wordsList.add(new WordEntry(id, word, added, ""));
        }

        return wordsList;

    }

    public ArrayList<String> getAllAddWordsLater() {
        ArrayList<String> addWordsLaterList = new ArrayList<>();

        SQLiteDatabase db = FabVocabSQLHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD + " FROM " + FabVocabContract.AddWordLaterTableEntry.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            addWordsLaterList.add(cursor.getString(cursor.getColumnIndex(FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD)));
        }

        return addWordsLaterList;
    }


    public int addDefinition(int wordId, String definition) {
        ContentValues values = new ContentValues();
        values.put(FabVocabContract.DefinitionTableEntry.COLUMN_WORD_ID, wordId);
        values.put(FabVocabContract.DefinitionTableEntry.COLUMN_NAME_DEFINITION, definition);
        return (int) db.insert(FabVocabContract.DefinitionTableEntry.TABLE_NAME, null, values);
    }

    public void deleteDefinition(int id) {
        db.delete(FabVocabContract.DefinitionTableEntry.TABLE_NAME, "_id = ?", new String[]{Integer.toString(id)});
    }

    public int updateDefinition(int id, String definition) {
        ContentValues values = new ContentValues();
        values.put(FabVocabContract.DefinitionTableEntry.COLUMN_NAME_DEFINITION, definition);
        return db.update(FabVocabContract.DefinitionTableEntry.TABLE_NAME, values, FabVocabContract.DefinitionTableEntry._ID + " = ?", new String[]{Integer.toString(id)});
    }


    public int addPractice(int wordId, int recall, int fluency) {
        ContentValues values = new ContentValues();
        values.put(FabVocabContract.WordPracticeTableEntry.COLUMN_NAME_RECALL_RATING, recall);
        values.put(FabVocabContract.WordPracticeTableEntry.COLUMN_NAME_FLUENCY_RATING, fluency);
        values.put(FabVocabContract.WordPracticeTableEntry.COLUMN_NAME_WORD_ID, wordId);
        return (int) db.insert(FabVocabContract.WordPracticeTableEntry.TABLE_NAME, null, values);
    }

    public int addWordForLater(String word){
        ContentValues value = new ContentValues();
        value.put(FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD, word.toLowerCase());
        return (int) db.insert(FabVocabContract.AddWordLaterTableEntry.TABLE_NAME, null, value);
    }

    public void deleteWordForLater(String word) {
        db.delete(FabVocabContract.AddWordLaterTableEntry.TABLE_NAME, FabVocabContract.AddWordLaterTableEntry.COLUMN_NAME_WORD + " = ?", new String[]{word});
    }

    // ***************** SQL Statistics Queries *****************//

    public int getWordsCount() {
        Cursor cursor = db.rawQuery("SELECT count(*) as totalwords FROM words", null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("totalwords"));
    }

    public int getMasteredWordsCount() {
        Cursor cursor = db.rawQuery("SELECT count(*) as masteredwords FROM word_practice WHERE recall_rating = 10 and fluency_rating = 10", null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("masteredwords"));
    }

    public int getPracticedSessionsCount() {
        Cursor cursor = db.rawQuery("SELECT count(*) as totalpracticesessions FROM word_practice", null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("totalpracticesessions"));
    }

    public int getUnpracticedWordsCount() {
        Cursor cursor = db.rawQuery("select (total.total - sessions.uniquewords) as unpracticedwords from (select count(*) as total from words) as total, (select count(distinct word_id) as uniquewords from word_practice ) as sessions", null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("unpracticedwords"));
    }

    public int getAddWordsLaterCount() {
        Cursor cursor = db.rawQuery("SELECT count(*) as addWordsLaterCount FROM " + FabVocabContract.AddWordLaterTableEntry.TABLE_NAME, null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("addWordsLaterCount"));
    }

    public int getDefinitionsCount(int wordId) {
        Cursor cursor = db.rawQuery("SELECT count(definition) as numDefinitions FROM " + FabVocabContract.DefinitionTableEntry.TABLE_NAME + " WHERE word_id=?", new String[]{Integer.toString(wordId)});
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("numDefinitions"));
    }

    public int getPracticeSessionCountForWord(int id) {
        Cursor cursor = db.rawQuery("select * from word_practice where word_id = ?", new String[]{Integer.toString(id)});
        return  cursor.getCount();
    }

    public WordStatistics getWordStatistics(int id) {

        Cursor cursor = db.rawQuery("select avg(CAST(recall_rating AS FLOAT)) as avgpractice_recall, avg(CAST(fluency_rating AS FLOAT)) as avgpractice_fluency from word_practice where word_id = ?", new String[]{Integer.toString(id)});
        cursor.moveToNext();
        int avgRecall = cursor.getInt(cursor.getColumnIndex("avgpractice_recall"));
        int avgFluency = cursor.getInt(cursor.getColumnIndex("avgpractice_fluency"));

        cursor = db.rawQuery("select recall_rating as lastpractice_recall, fluency_rating as lastpractice_fluency from word_practice where word_id = ? order by time desc limit 1", new String[]{Integer.toString(id)});
        cursor.moveToNext();
        int bestRecall = cursor.getInt(cursor.getColumnIndex("lastpractice_recall"));
        int bestFluency = cursor.getInt(cursor.getColumnIndex("lastpractice_fluency"));

        cursor = db.rawQuery("select recall_rating as bestpractice_recall, fluency_rating as bestpractice_fluency from word_practice where word_id = ? order by recall_rating desc, fluency_rating desc limit 1", new String[]{Integer.toString(id)});
        cursor.moveToNext();
        int lastRecall = cursor.getInt(cursor.getColumnIndex("bestpractice_recall"));
        int lastFluency = cursor.getInt(cursor.getColumnIndex("bestpractice_fluency"));

        return new WordStatistics(avgRecall, avgFluency, bestRecall, bestFluency, lastRecall, lastFluency);
    }

    @Override
    public void close() {
        super.close();
        this.instance = null;
    }
}
