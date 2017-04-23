package uk.co.cdevelop.fabvocab.Fragments.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import uk.co.cdevelop.fabvocab.Fragments.AddWordsFragment;
import uk.co.cdevelop.fabvocab.SQL.Models.DefinitionEntry;
import uk.co.cdevelop.fabvocab.Support.Helpers;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 08/02/2017.
 */

public class ManualAddDefinitionDialogFragment extends DialogFragment {

    private final AddWordsFragment addWordsFragment;
    private AlertDialog dialog;
    private boolean isNewWord;
    private int wordId;
    private String wordForDefinition;
    private ArrayList<DefinitionEntry> existingDefinitions = new ArrayList<>();

    public ManualAddDefinitionDialogFragment(AddWordsFragment addWordsFragment, String wordForDefinition) {
        this.addWordsFragment = addWordsFragment;
        this.wordForDefinition = wordForDefinition;
        this.wordId = -1;
        this.isNewWord = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Find the word in the DB

        // Setup layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.manualadddefinition, null);
        final TextView tvWord = (TextView) view.findViewById(R.id.tv_word);
        final EditText etDefinition = (EditText) view.findViewById(R.id.et_definition);
        final Button positiveButton;


        // Search for existing word
        wordId = FabVocabSQLHelper.getInstance(getContext()).getWordId(wordForDefinition);
        if(wordId > 0) {
            isNewWord = false;
        }

        if(isNewWord) {
            view.findViewById(R.id.iv_newwordicon).setVisibility(View.VISIBLE);
        } else {
            existingDefinitions = FabVocabSQLHelper.getInstance(getContext()).getAllDefinitions(wordId);
        }



        tvWord.setText(wordForDefinition);

        builder.setMessage("")
                .setTitle("Add New " + (isNewWord ? "Word & " : "") + "Definition")
                .setView(view)
                .setPositiveButton((isNewWord ? "Add Word" : "Add Definition"), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: alert of invalid definition without closing the dialog?

                        String toastStr = "";

                        if(isNewWord) {
                            wordId = FabVocabSQLHelper.getInstance(getContext()).addWord(wordForDefinition);
                            toastStr += "Added New Word: " + wordForDefinition;
                            addWordsFragment.updateWordView();
                        } else {
                            toastStr += "Definition Added";
                        }

                        // Add Definition
                        FabVocabSQLHelper.getInstance(getContext()).addDefinition(wordId, etDefinition.getText().toString());

                        // Toast outcome
                        Toast.makeText(getContext(), toastStr, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialog = builder.create();

        etDefinition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean newDefinition = false;
                if(etDefinition.getText().toString().trim().length() > 0) {
                    newDefinition = true;
                    for (DefinitionEntry definitionEntry : existingDefinitions) {
                        if (Helpers.isSimilar(etDefinition.getText().toString(), definitionEntry.getDefinition())) {
                            newDefinition = false;
                            break;
                        }
                    }
                }

                if(newDefinition) {
                    etDefinition.setTextColor(Color.rgb(0, 0, 0));
                    etDefinition.setPaintFlags(etDefinition.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    etDefinition.setTextColor(Color.rgb(255, 0, 0));
                    etDefinition.setPaintFlags(etDefinition.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        return dialog;
    }

}
