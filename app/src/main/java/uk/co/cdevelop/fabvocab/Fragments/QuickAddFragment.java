package uk.co.cdevelop.fabvocab.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.Activities.QuickAddActivity;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;
import uk.co.cdevelop.fabvocab.WebRequest.RequestWord;

/**
 * Created by Chris on 13/02/2017.
 */

public class QuickAddFragment extends Fragment implements IFragmentWithCleanUp, IAddResultsViewOwner{

    private AddWordsResultsView addWordsResultsView;
    private RequestWord requester;
    private String word;

    public QuickAddFragment(String word) {
        this.word = word;
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        return inflator.inflate(R.layout.quickadd_main, container, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        // Check if the word already exists in My Dictionary
        final SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();
        int wordId = FabVocabSQLHelper.getWord(word.toString(), db);
        final boolean isNewWord = (wordId == -1);

        boolean isInAddLater = false;
        if(isNewWord) {
            Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.AddWordLater.COLUMN_NAME_WORD + " FROM " + FabVocabContract.AddWordLater.TABLE_NAME + " WHERE " + FabVocabContract.AddWordLater.COLUMN_NAME_WORD + " = ?", new String[]{word.toLowerCase()});
            if(cursor.getCount() > 0) {
                isInAddLater = true;
            }
        }


        final LinearLayout llAddNow = (LinearLayout) view.findViewById(R.id.ll_quickadd_addnow);
        final LinearLayout llContainer = (LinearLayout) view.findViewById(R.id.ll_quickadd_container);
        final LinearLayout llAddButtons = (LinearLayout) view.findViewById(R.id.ll_quickadd_addbuttons);
        final LinearLayout llWordReview = (LinearLayout) view.findViewById(R.id.ll_quickadd_wordreview);
        final ListView lvDefinitions = (ListView) view.findViewById(R.id.lv_quickadd_definitions);
        addWordsResultsView = (AddWordsResultsView) view.findViewById(R.id.awrv_quickadd_results);

        final TextView tvWord = (TextView) view.findViewById(R.id.tv_quickadd_word);
        final TextView tvInAddLater = (TextView) view.findViewById(R.id.tv_quickadd_inaddlater);
        final Button btnDone = (Button) view.findViewById(R.id.btn_quickadd_done);
        final Button btnClose = (Button) view.findViewById(R.id.btn_quickadd_close);
        final Button btnAddNow = (Button) view.findViewById(R.id.btn_quickadd_addnow);
        final Button btnAddLater = (Button) view.findViewById(R.id.btn_quickadd_addlater);
        final ImageView ivNewWordIcon = (ImageView) view.findViewById(R.id.iv_quickadd_newwordicon);


        tvWord.setText(word.toLowerCase());


        if(isNewWord) {
            ivNewWordIcon.setVisibility(View.VISIBLE);
            llAddButtons.setVisibility(View.VISIBLE);
            if (isInAddLater) {
                btnAddLater.setVisibility(View.GONE);
                tvInAddLater.setVisibility(View.VISIBLE);
            }
        } else {
            llWordReview.setVisibility(View.VISIBLE);


            ArrayList<String> definitionList = FabVocabSQLHelper.getDefinitions(wordId, db);

            ArrayAdapter<String> definitions = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, definitionList);
            lvDefinitions.setAdapter(definitions);
            btnDone.setVisibility(View.VISIBLE);
        }

        btnAddLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues value = new ContentValues();
                value.put(FabVocabContract.AddWordLater.COLUMN_NAME_WORD, word.toLowerCase());

                Cursor cursor = db.rawQuery("SELECT 1 FROM " + FabVocabContract.AddWordLater.TABLE_NAME + " WHERE " + FabVocabContract.AddWordLater.COLUMN_NAME_WORD + " = ?", new String[]{word.toLowerCase()});
                if(cursor.getCount() > 0) {
                    Toast.makeText(getContext(), "Already in your 'Add Later' List!", Toast.LENGTH_SHORT).show();
                } else {
                    db.insert(FabVocabContract.AddWordLater.TABLE_NAME, null, value);
                    Toast.makeText(getContext(), "Added to 'Add Later' List!", Toast.LENGTH_SHORT).show();
                }
                returnToCallingActivity();
            }
        });

        btnAddNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAddNow.setVisibility(View.VISIBLE);
                btnAddNow.setVisibility(View.GONE);

                btnDone.setEnabled(false);

                addWordsResultsView.setParent(btnDone);

                try {
                    requester = new RequestWord(getContext());
                    requester.requestAll(word.toLowerCase(), addWordsResultsView);

                    addWordsResultsView.clearAll();
                    addWordsResultsView.setAllInProgress();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                btnDone.setVisibility(View.VISIBLE);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the calling app e.g. Chrome/PDF viewer etc.

                if(isNewWord) {
                    // Insert
                    ContentValues values = new ContentValues();


                    // Remove from 'Add Later' if there
                    SQLiteDatabase localDb = db;
                    if (!db.isOpen()) {
                        localDb = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();
                    }
                    localDb.delete(FabVocabContract.AddWordLater.TABLE_NAME, FabVocabContract.AddWordLater.COLUMN_NAME_WORD + " = ?", new String[]{word.toLowerCase()});

                    // Add Word
                    values.put("word", word.toLowerCase());
                    values.put("added", FabVocabSQLHelper.getSQLTimestamp());
                    int word_id = (int) localDb.insert("words", null, values);

                    // Add Definitions
                    for (String definition : addWordsResultsView.getDefinitionsToAdd()) {
                        values = new ContentValues();
                        values.put(FabVocabContract.DefinitionEntry.COLUMN_WORD_ID, word_id);
                        values.put(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION, definition);

                        localDb.insert(FabVocabContract.DefinitionEntry.TABLE_NAME, null, values);
                    }

                    Toast.makeText(getContext(), "Added word to My Dictionary", Toast.LENGTH_SHORT).show();
                }

                returnToCallingActivity();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToCallingActivity();
            }
        });
    }



    @Override
    public AddWordsResultsView getResultView() {
        return addWordsResultsView;
    }

    @Override
    public void cleanUp() {

    }

    public void returnToCallingActivity() {
        //((Activity) getContext()).setResult(Activity.RESULT_OK, null);
        Intent intent = ((Activity) getContext()).getIntent();
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "testing");
        ((Activity) getContext()).setResult(Activity.RESULT_OK, intent);
        ((Activity) getContext()).finish();
    }
}
