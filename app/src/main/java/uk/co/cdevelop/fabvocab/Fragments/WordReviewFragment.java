package uk.co.cdevelop.fabvocab.Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import uk.co.cdevelop.fabvocab.Activities.MainActivity;
import uk.co.cdevelop.fabvocab.Activities.PracticeActivity;
import uk.co.cdevelop.fabvocab.DataModels.OxfordAPIWordDefinition;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.Views.AddDefinitionView;
import uk.co.cdevelop.fabvocab.Views.EditTextWithImeEvents;
import uk.co.cdevelop.fabvocab.Views.EditTextWithImeEventsBackListener;

/**
 * Created by Chris on 19/01/2017.
 */

public class WordReviewFragment extends Fragment implements IFragmentWithCleanUp {

    private View view;
    private int word_id;

    public WordReviewFragment(){}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wordreview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Set Title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Word Review");
        ((MainActivity) getActivity()).hideFloatingAddButton();

        this.view = view;
        final TextView word = (TextView) view.findViewById(R.id.tv_word);
        final Button btnDoWordPractice = (Button) view.findViewById(R.id.btn_practiceword);

        Bundle b = getArguments();
        final int word_id = b.getInt("word_id");
        this.word_id = word_id;

        // Get Word info
        SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();
        Cursor wordCursor = db.rawQuery("SELECT word, _id FROM " + FabVocabContract.WordEntry.TABLE_NAME + " WHERE _id=?", new String[]{Integer.toString(word_id)});

        wordCursor.moveToNext();
        word.setText(wordCursor.getString(wordCursor.getColumnIndex("word")));


        // Get Definitions
        refreshDefinitions();

        // Get Examples
        //TODO: Examples implementation.....

        // Get/Generate Stats
        refreshStats();


        btnDoWordPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PracticeActivity.class);
                intent.putExtra("mode", "doword");
                intent.putExtra("word_id", word_id);
                startActivity(intent);
            }
        });

    }

    private void refreshDefinitions() {

        final LinearLayout definitionsLayout = (LinearLayout) view.findViewById(R.id.lv_definitions);
        definitionsLayout.removeAllViews();

        final SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();
        Cursor definitionCursor = db.rawQuery("SELECT _id, definition FROM " + FabVocabContract.DefinitionEntry.TABLE_NAME + " WHERE word_id=?", new String[]{Integer.toString(word_id)});

        Boolean noDelete = definitionCursor.getCount() <= 1;
        while (definitionCursor.moveToNext()) {
            LayoutInflater inflator = getActivity().getLayoutInflater();
            final View newDefinitionRow = inflator.inflate(R.layout.wordreview_definitionrow, null, false);


            /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(50, 0, 50, 25);
            newDefinitionRow.findViewById(R.id.tv_definition).setLayoutParams(params);*/

            final LinearLayout lvDefinitionRow = (LinearLayout) newDefinitionRow.findViewById(R.id.lv_definitionrow);
            final EditTextWithImeEvents etDefinition = (EditTextWithImeEvents) newDefinitionRow.findViewById(R.id.et_definition);
            final TextView tvDefinitionId = (TextView) newDefinitionRow.findViewById(R.id.tv_definition_id);
            final Button btnDelete = (Button) newDefinitionRow.findViewById(R.id.btn_delete);

            if (noDelete) {
                btnDelete.setVisibility(View.INVISIBLE);
            }

            etDefinition.setText(definitionCursor.getString(definitionCursor.getColumnIndex("definition")));
            tvDefinitionId.setText(definitionCursor.getString(definitionCursor.getColumnIndex("_id")));


            // Defauly edittext state
            etDefinition.setInputType(InputType.TYPE_NULL);
            etDefinition.setHorizontallyScrolling(false);
            etDefinition.setMaxLines(100); //TODO: this is an arbitrary number to set to match the XML... refine this thinking
            etDefinition.setImeOptions(EditorInfo.IME_ACTION_DONE);

            etDefinition.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    etDefinition.setInputType(InputType.TYPE_CLASS_TEXT);
                    etDefinition.requestFocus();
                    etDefinition.setSelection(etDefinition.getText().length());

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDefinition, InputMethodManager.SHOW_IMPLICIT);

                    return true;
                }
            });
            etDefinition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        etDefinition.setInputType(InputType.TYPE_NULL);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etDefinition.getWindowToken(), 0);

                        // Attempt to update the definition

                        String toastStr = "undefined";
                        //TODO: A more generic helper function for a VALID definition
                        if(!etDefinition.getText().toString().trim().equals("")) {
                            SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION, etDefinition.getText().toString());
                            db.update(FabVocabContract.DefinitionEntry.TABLE_NAME, values, FabVocabContract.DefinitionEntry._ID + " = ?", new String[]{tvDefinitionId.getText().toString()});
                            toastStr = "Definition updated!";
                        } else {
                            toastStr = "No! Invalid definition change!";
                        }
                        Toast.makeText(getContext(), toastStr, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            etDefinition.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE) {
                        etDefinition.clearFocus();
                        return true;
                    }
                    return false;
                }
            });

            etDefinition.setOnEditTextBackListener(new EditTextWithImeEventsBackListener() {
                @Override
                public void onImeBack() {
                    etDefinition.clearFocus();
                }
            });


            newDefinitionRow.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor = db.rawQuery("SELECT count(definition) as numDefinitions FROM " + FabVocabContract.DefinitionEntry.TABLE_NAME + " WHERE word_id=?", new String[]{Integer.toString(word_id)});
                    cursor.moveToNext();
                    int numDefinitions = cursor.getInt(cursor.getColumnIndex("numDefinitions"));

                    if(numDefinitions > 1) {

                        final String definition_id = (String) ((TextView) newDefinitionRow.findViewById(R.id.tv_definition_id)).getText();

                        new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                                .setTitle("Delete Definition")
                                .setMessage("Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.delete(FabVocabContract.DefinitionEntry.TABLE_NAME, "_id = ?", new String[]{definition_id});
                                        refreshDefinitions();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                                .setTitle("No can do...")
                                .setMessage("You can't delete the only definition!")
                                .setPositiveButton("Gotcha!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                }
            });



            definitionsLayout.addView(newDefinitionRow);
        }
    }

    private void refreshStats() {
        if(view == null) {
            return;
        }

        //final TextView tvLastPracticeRecall = (TextView) view.findViewById(R.id.tv_lastpractice_recall);
        //final TextView tvAvgPracticeRecall = (TextView) view.findViewById(R.id.tv_avgpractice_recall);
        //final TextView tvBestPracticeRecall = (TextView) view.findViewById(R.id.tv_bestpractice_recall);
        //final TextView tvLastPracticeFluency = (TextView) view.findViewById(R.id.tv_lastpractice_fluency);
        //final TextView tvAvgPracticeFluency = (TextView) view.findViewById(R.id.tv_avgpractice_fluency);
        //final TextView tvBestPracticeFluency = (TextView) view.findViewById(R.id.tv_bestpractice_fluency);

        final ProgressBar pbLastPracticeRecall = (ProgressBar) view.findViewById(R.id.pb_lastpractice_recall);
        final ProgressBar pbLastPracticeFluency = (ProgressBar) view.findViewById(R.id.pb_lastpractice_fluency);
        final ProgressBar pbAvgPracticeRecall = (ProgressBar) view.findViewById(R.id.pb_avgpractice_recall);
        final ProgressBar pbAvgPracticeFluency = (ProgressBar) view.findViewById(R.id.pb_avgpractice_fluency);
        final ProgressBar pbBestPracticeRecall = (ProgressBar) view.findViewById(R.id.pb_bestpractice_recall);
        final ProgressBar pbBestPracticeFluency = (ProgressBar) view.findViewById(R.id.pb_bestpractice_fluency);

        SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from word_practice where word_id = ?", new String[]{Integer.toString(word_id)});
        if(cursor.getCount() != 0) {
            cursor = db.rawQuery("select avg(CAST(recall_rating AS FLOAT)) as avgpractice_recall, avg(CAST(fluency_rating AS FLOAT)) as avgpractice_fluency from word_practice where word_id = ?", new String[]{Integer.toString(word_id)});
            cursor.moveToNext();
            //tvAvgPracticeRecall.setText(Integer.toString(Math.round(cursor.getFloat(cursor.getColumnIndex("avgpractice_recall")))));
            //tvAvgPracticeFluency.setText(Integer.toString(Math.round(cursor.getFloat(cursor.getColumnIndex("avgpractice_fluency")))));
            pbAvgPracticeRecall.setProgress(cursor.getInt(cursor.getColumnIndex("avgpractice_recall")));
            pbAvgPracticeFluency.setProgress(cursor.getInt(cursor.getColumnIndex("avgpractice_fluency")));

            cursor = db.rawQuery("select recall_rating as lastpractice_recall, fluency_rating as lastpractice_fluency from word_practice where word_id = ? order by time desc limit 1", new String[]{Integer.toString(word_id)});
            cursor.moveToNext();
            //tvLastPracticeRecall.setText(Integer.toString(Math.round(cursor.getFloat(cursor.getColumnIndex("lastpractice_recall")))));
            //tvLastPracticeFluency.setText(Integer.toString(Math.round(cursor.getFloat(cursor.getColumnIndex("lastpractice_fluency")))));
            pbLastPracticeRecall.setProgress(cursor.getInt(cursor.getColumnIndex("lastpractice_recall")));
            pbLastPracticeFluency.setProgress(cursor.getInt(cursor.getColumnIndex("lastpractice_fluency")));

            cursor = db.rawQuery("select recall_rating as bestpractice_recall, fluency_rating as bestpractice_fluency from word_practice where word_id = ? order by recall_rating desc, fluency_rating desc limit 1", new String[]{Integer.toString(word_id)});
            cursor.moveToNext();
            //tvBestPracticeRecall.setText(Integer.toString(Math.round(cursor.getFloat(cursor.getColumnIndex("bestpractice_recall")))));
            //tvBestPracticeFluency.setText(Integer.toString(Math.round(cursor.getFloat(cursor.getColumnIndex("bestpractice_fluency")))));
            pbBestPracticeRecall.setProgress(cursor.getInt(cursor.getColumnIndex("bestpractice_recall")));
            pbBestPracticeFluency.setProgress(cursor.getInt(cursor.getColumnIndex("bestpractice_fluency")));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStats();
    }

    @Override
    public void cleanUp() {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
