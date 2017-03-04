package uk.co.cdevelop.fabvocab.Fragments.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.Fragments.AddWordsFragment;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsResultFragment;
import uk.co.cdevelop.fabvocab.Helpers;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 08/02/2017.
 */

public class ManualAddDefinitionDialogFragment extends DialogFragment {

    private final AddWordsFragment addWordsFragment;
    private boolean isNewWord;
    private int wordId;
    private String wordForDefinition;
    private ArrayList<String> existingDefinitions = new ArrayList<String>();

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
        final SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordEntry._ID + " fROM " + FabVocabContract.WordEntry.TABLE_NAME + " WHERE word = ?", new String[]{wordForDefinition});
        //TODO: Use this function!
        //wordId = getWord();
        if(cursor.getCount() > 0) {
            cursor.moveToNext();
            wordId = cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordEntry._ID));
            isNewWord = false;
        }

        if(isNewWord) {
            view.findViewById(R.id.iv_newwordicon).setVisibility(View.VISIBLE);
        } else {
            existingDefinitions = FabVocabSQLHelper.getDefinitions(wordId, db);
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
                            wordId = FabVocabSQLHelper.addWord(wordForDefinition, db);
                            toastStr += "Added New Word: " + wordForDefinition;
                            addWordsFragment.updateWordView();
                        } else {
                            toastStr += "Definition Added";
                        }

                        // Add Definition
                        ContentValues values = new ContentValues();
                        values.put(FabVocabContract.DefinitionEntry.COLUMN_WORD_ID, wordId);
                        values.put(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION, etDefinition.getText().toString());
                        db.insert(FabVocabContract.DefinitionEntry.TABLE_NAME, null, values);

                        // Toast outcome
                        Toast.makeText(getContext(), toastStr, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final AlertDialog dialog = builder.create();

        etDefinition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean newDefinition = true;
                for (String def : existingDefinitions) {
                    if(Helpers.isSimilar(etDefinition.getText().toString(), def)) {
                        newDefinition = false;
                        break;
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
