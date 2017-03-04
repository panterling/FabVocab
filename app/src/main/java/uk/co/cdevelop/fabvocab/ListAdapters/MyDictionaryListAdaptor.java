package uk.co.cdevelop.fabvocab.ListAdapters;

import uk.co.cdevelop.fabvocab.DataModels.*;
import uk.co.cdevelop.fabvocab.Fragments.WordReviewFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Chris on 19/01/2017.
 */

public class MyDictionaryListAdaptor extends ArrayAdapter {

    Context context;
    ArrayList<MyDictionaryDefinition> words;

    public MyDictionaryListAdaptor(Context context, ArrayList<MyDictionaryDefinition> words) {
        super(context, -1, words);
        this.words = words;
        this.context = context;
        this.showAll();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflator.inflate(R.layout.dictionaryrow, parent, false);

        TextView wordTextView = (TextView) rowView.findViewById(R.id.dictionaryrow_textview);
        Button deleteButton = (Button) rowView.findViewById(R.id.btn_dictionaryrow_delete);


        final String word = words.get(position).getWord();
        final int word_id = words.get(position).getWordId();
        wordTextView.setText(word);


        wordTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                WordReviewFragment newFragment = new WordReviewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("word_id", words.get(position).getWordId());
                newFragment.setArguments(bundle);

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.flcontent, newFragment).addToBackStack(null)
                        .commit();
            }
        });



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context,R.style.MyAlertDialog)
                        .setTitle("Delete Word...")
                        .setMessage("Are you sure you want to delete: " + word)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                words.remove(position);

                                SQLiteDatabase db = FabVocabSQLHelper.getInstance(context).getWritableDatabase();
                                db.delete(FabVocabContract.DefinitionEntry.TABLE_NAME, "word_id=?", new String[]{Integer.toString(word_id)});
                                db.delete(FabVocabContract.WordEntry.TABLE_NAME, "_id=?", new String[]{Integer.toString(word_id)});

                                MyDictionaryListAdaptor.this.notifyDataSetChanged();

                                Toast.makeText(getContext(), "Deleted word: " + word, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return rowView;

    }

    public void filterByLetter(char c) {
        SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();

        Log.i("alphabet", "SELECT _id, word FROM words WHERE word LIKE '" + c + "%' or word LIKE '" + Character.toString((char)(((int) c) + 32)) + "%'  ORDER BY word ASC");
        Cursor cursor = db.rawQuery("SELECT _id, word FROM words WHERE word LIKE '" + c + "%' or word LIKE '" + Character.toString((char)(((int) c) + 32)) + "%'  ORDER BY word ASC", null);

        ArrayList<MyDictionaryDefinition> newWords = new ArrayList<MyDictionaryDefinition>();
        while(cursor.moveToNext()) {
            //String definition = cursor.getString(cursor.getColumnIndex(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION));
            int word_id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordEntry._ID));
            String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordEntry.COLUMN_NAME_WORD));

            newWords.add(new MyDictionaryDefinition(word_id, word));

        }

        words.clear();
        words.addAll(newWords);
        notifyDataSetChanged();
    }

    public void showAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();

                Cursor cursor = db.rawQuery("SELECT _id, word FROM words ORDER BY word ASC", null);

                final ArrayList<MyDictionaryDefinition> words = new ArrayList<MyDictionaryDefinition>();
                while(cursor.moveToNext()) {
                    int word_id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordEntry._ID));
                    String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordEntry.COLUMN_NAME_WORD));

                    words.add(new MyDictionaryDefinition(word_id, word));

                }

                // Filler!
                words.add(new MyDictionaryDefinition(0,"Afghanistan"));
                words.add(new MyDictionaryDefinition(0,"Albania"));
                words.add(new MyDictionaryDefinition(0,"Bahrain"));
                words.add(new MyDictionaryDefinition(0,"Bangladesh"));
                words.add(new MyDictionaryDefinition(0,"Cambodia"));
                words.add(new MyDictionaryDefinition(0,"Cameroon"));
                words.add(new MyDictionaryDefinition(0,"Denmark"));
                words.add(new MyDictionaryDefinition(0,"Djibouti"));
                words.add(new MyDictionaryDefinition(0,"East Timor"));
                words.add(new MyDictionaryDefinition(0,"Ecuador"));
                words.add(new MyDictionaryDefinition(0,"Fiji"));
                words.add(new MyDictionaryDefinition(0,"Finland"));
                words.add(new MyDictionaryDefinition(0,"Gabon"));
                words.add(new MyDictionaryDefinition(0,"Georgia"));
                words.add(new MyDictionaryDefinition(0,"Haiti"));
                words.add(new MyDictionaryDefinition(0,"Holy See"));
                words.add(new MyDictionaryDefinition(0,"Iceland"));
                words.add(new MyDictionaryDefinition(0,"India"));
                words.add(new MyDictionaryDefinition(0,"Jamaica"));
                words.add(new MyDictionaryDefinition(0,"Japan"));
                words.add(new MyDictionaryDefinition(0,"Kazakhstan"));
                words.add(new MyDictionaryDefinition(0,"Kenya"));
                words.add(new MyDictionaryDefinition(0,"Laos"));
                words.add(new MyDictionaryDefinition(0,"Latvia"));
                words.add(new MyDictionaryDefinition(0,"Macau"));
                words.add(new MyDictionaryDefinition(0,"Macedonia"));
                words.add(new MyDictionaryDefinition(0,"Namibia"));
                words.add(new MyDictionaryDefinition(0,"Nauru"));
                words.add(new MyDictionaryDefinition(0,"Oman"));
                words.add(new MyDictionaryDefinition(0,"Pakistan"));
                words.add(new MyDictionaryDefinition(0,"Palau"));
                words.add(new MyDictionaryDefinition(0,"Qatar"));
                words.add(new MyDictionaryDefinition(0,"Romania"));
                words.add(new MyDictionaryDefinition(0,"Russia"));
                words.add(new MyDictionaryDefinition(0,"Saint Kitts and Nevis"));
                words.add(new MyDictionaryDefinition(0,"Saint Lucia"));
                words.add(new MyDictionaryDefinition(0,"Taiwan"));
                words.add(new MyDictionaryDefinition(0,"Tajikistan"));
                words.add(new MyDictionaryDefinition(0,"Uganda"));
                words.add(new MyDictionaryDefinition(0,"Ukraine"));
                words.add(new MyDictionaryDefinition(0,"Vanuatu"));
                words.add(new MyDictionaryDefinition(0,"Venezuela"));
                words.add(new MyDictionaryDefinition(0,"Yemen"));
                words.add(new MyDictionaryDefinition(0,"Zambia"));
                words.add(new MyDictionaryDefinition(0,"Zimbabwe"));
                words.add(new MyDictionaryDefinition(0,"0"));
                words.add(new MyDictionaryDefinition(0,"2"));
                words.add(new MyDictionaryDefinition(0,"9"));

                Collections.sort(words);


                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyDictionaryListAdaptor.this.words.clear();
                        MyDictionaryListAdaptor.this.words.addAll(words);
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public int getPositionForChar(char c) {
        // Slow and inefficient - but a starter for 10
        for(int i = 0; i < words.size(); i++) {
            if(words.get(i).getWord().charAt(0) == c) {
                Log.i("Position", "Pos of (" + c + "): " + i);
                return i;
            }
        }

        return -1;
    }

    public void showRecentlyAdded() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();

                Cursor cursor = db.rawQuery("SELECT _id, word, added FROM words WHERE added > datetime('now', '-7 days') ORDER BY word ASC", null);

                final ArrayList<MyDictionaryDefinition> words = new ArrayList<MyDictionaryDefinition>();
                while (cursor.moveToNext()) {
                    int word_id = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordEntry._ID));
                    String word = cursor.getString(cursor.getColumnIndex(FabVocabContract.WordEntry.COLUMN_NAME_WORD));

                    words.add(new MyDictionaryDefinition(word_id, word));

                }


                Collections.sort(words); // TODO: unnecessary if SQL is ordering already?

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyDictionaryListAdaptor.this.words.clear();
                        MyDictionaryListAdaptor.this.words.addAll(words);
                        notifyDataSetChanged();
                    }
                });

            }}).start();

    }
}
