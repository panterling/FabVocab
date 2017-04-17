package uk.co.cdevelop.fabvocab.Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.Activities.MainActivity;
import uk.co.cdevelop.fabvocab.Activities.PracticeActivity;
import uk.co.cdevelop.fabvocab.DataModels.WordStatistics;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.SQL.Models.DefinitionEntry;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;
import uk.co.cdevelop.fabvocab.Views.EditTextWithImeEvents;
import uk.co.cdevelop.fabvocab.Views.EditTextWithImeEventsBackListener;
import uk.co.cdevelop.fabvocab.Views.PronounciationPlayerView;

/**
 * Created by Chris on 19/01/2017.
 */

public class WordReviewFragment extends Fragment implements IFragmentWithCleanUp {

    private View view;
    private int wordId;

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
        final TextView tvWord = (TextView) view.findViewById(R.id.tv_word);
        final Button btnDoWordPractice = (Button) view.findViewById(R.id.btn_practiceword);

        final PronounciationPlayerView ppvAudio = (PronounciationPlayerView) view.findViewById(R.id.ppv_audio);



        Bundle b = getArguments();
        this.wordId = b.getInt("word_id");

        // Get Word info
        WordEntry wordEntry = FabVocabSQLHelper.getInstance(getContext()).getWord(wordId);
        tvWord.setText(wordEntry.getWord());
        ppvAudio.giveSource(wordEntry.getAudioUrl());


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
                intent.putExtra("word_id", wordId);
                startActivity(intent);
            }
        });

    }

    private void refreshDefinitions() {

        final LinearLayout definitionsLayout = (LinearLayout) view.findViewById(R.id.lv_definitions);
        definitionsLayout.removeAllViews();


        ArrayList<DefinitionEntry> dbDefinitionList = FabVocabSQLHelper.getInstance(getContext()).getAllDefinitions(wordId);
        if(dbDefinitionList != null) {
            boolean noDelete = dbDefinitionList.size() == 0;

            int count = 0;
            for (DefinitionEntry definitionEntry : dbDefinitionList) {
                count++;
                LayoutInflater inflator = getActivity().getLayoutInflater();
                final View newDefinitionRow = inflator.inflate(R.layout.wordreview_definitionrow, null, false);

                final LinearLayout lvDefinitionRow = (LinearLayout) newDefinitionRow.findViewById(R.id.lv_definitionrow);
                final EditTextWithImeEvents etDefinition = (EditTextWithImeEvents) newDefinitionRow.findViewById(R.id.et_definition);
                final TextView tvDefinitionId = (TextView) newDefinitionRow.findViewById(R.id.tv_definition_id);
                final TextView tvDefinitionCount = (TextView) newDefinitionRow.findViewById(R.id.tv_definition_counter);
                final Button btnDelete = (Button) newDefinitionRow.findViewById(R.id.btn_delete);

                if (noDelete) {
                    btnDelete.setVisibility(View.INVISIBLE);
                }

                etDefinition.setText(definitionEntry.getDefinition());
                tvDefinitionId.setText(Integer.toString(definitionEntry.getId()));
                tvDefinitionCount.setText(Integer.toString(count) + ".");


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
                        etDefinition.setTypeface(null, Typeface.ITALIC);

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
                            etDefinition.setTypeface(null, Typeface.NORMAL);
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etDefinition.getWindowToken(), 0);

                            // Attempt to update the definition

                            String toastStr = "undefined";
                            //TODO: A more generic helper function for a VALID definition
                            if(!etDefinition.getText().toString().trim().equals("")) {
                                FabVocabSQLHelper.getInstance(getContext()).updateDefinition(Integer.parseInt(tvDefinitionId.getText().toString()), etDefinition.getText().toString());

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

                        if(FabVocabSQLHelper.getInstance(getContext()).getDefinitionsCount(wordId) > 1) {

                            final String definition_id = (String) ((TextView) newDefinitionRow.findViewById(R.id.tv_definition_id)).getText();

                            new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                                    .setTitle("Delete Definition")
                                    .setMessage("Are you sure?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FabVocabSQLHelper.getInstance(getContext()).deleteDefinition(Integer.parseInt(definition_id));
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

        if(FabVocabSQLHelper.getInstance(getContext()).getPracticeSessionCountForWord(wordId) != 0) {

            WordStatistics wordStats = FabVocabSQLHelper.getInstance(getContext()).getWordStatistics(wordId);

            pbAvgPracticeRecall.setProgress(wordStats.getAvgRecall());
            pbAvgPracticeFluency.setProgress(wordStats.getAvgFluency());
            pbLastPracticeRecall.setProgress(wordStats.getLastRecall());
            pbLastPracticeFluency.setProgress(wordStats.getLastFluency());
            pbBestPracticeRecall.setProgress(wordStats.getBestRecall());
            pbBestPracticeFluency.setProgress(wordStats.getBestFluency());
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
