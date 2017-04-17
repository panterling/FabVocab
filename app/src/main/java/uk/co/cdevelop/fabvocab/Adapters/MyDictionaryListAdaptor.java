package uk.co.cdevelop.fabvocab.Adapters;


import uk.co.cdevelop.fabvocab.DataModels.*;
import uk.co.cdevelop.fabvocab.Fragments.WordReviewFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Chris on 19/01/2017.
 */

public class MyDictionaryListAdaptor extends ArrayAdapter<MyDictionaryRow> {

    private Context context;
    private ArrayList<MyDictionaryRow> words;

    public MyDictionaryListAdaptor(Context context, ArrayList<MyDictionaryRow> words) {
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
        final int wordId = words.get(position).getWordId();
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

                                FabVocabSQLHelper.getInstance(getContext()).deleteWord(wordId);

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

        words.clear();

        ArrayList<WordEntry> dbWordsList = FabVocabSQLHelper.getInstance(getContext()).getFilteredWords(c);
        if(dbWordsList != null) {
            ArrayList<MyDictionaryRow> newWords = new ArrayList<>();
            for(WordEntry wordEntry : dbWordsList) {
                newWords.add(new MyDictionaryRow(wordEntry.getId(), wordEntry.getWord()));

            }

            words.addAll(newWords);
        }

        notifyDataSetChanged();
    }

    public void showAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<WordEntry> dbWordList = FabVocabSQLHelper.getInstance(getContext()).getAllWords(FabVocabSQLHelper.ORDERBY_ASC);
                if(dbWordList != null) {
                    final ArrayList<MyDictionaryRow> words = new ArrayList<>();

                    for(WordEntry wordEntry : dbWordList) {
                        words.add(new MyDictionaryRow(wordEntry.getId(), wordEntry.getWord().toLowerCase()));
                    }

                    // Sort on lowercase first letter - failsafe incase a word has got into the DB with an uppercase first letter
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

                ArrayList<WordEntry> dbWordList= FabVocabSQLHelper.getInstance(getContext()).getRecentlyAddedWords();
                if(dbWordList != null) {
                    final ArrayList<MyDictionaryRow> words = new ArrayList<>();
                    for (WordEntry wordEntry : dbWordList) {

                        words.add(new MyDictionaryRow(wordEntry.getId(), wordEntry.getWord()));

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
                }

            }}).start();

    }
}
