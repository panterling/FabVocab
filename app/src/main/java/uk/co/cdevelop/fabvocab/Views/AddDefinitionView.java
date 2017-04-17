package uk.co.cdevelop.fabvocab.Views;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 15/01/2017.
 */

public class AddDefinitionView extends LinearLayout {

    private View v;
    private ArrayList<String> definitionsToAdd;

    public AddDefinitionView(Context context) {
        super(context);
        init(context);
    }

    public AddDefinitionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddDefinitionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //v = mInflater.inflate(R.layout.definition_view, this, true);
        definitionsToAdd = new ArrayList<>();

        Button addButton = (Button) findViewById(R.id.btn_addword);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = (String) ((TextView) getRootView().findViewById(R.id.tv_word)).getText();
                String str = "";

                // Get Word Id
                int wordId = FabVocabSQLHelper.getInstance(getContext()).getWordId(word);
                if(wordId < 0) {
                    // Insert
                    ContentValues values = new ContentValues();
                    values.put("word", word);
                    wordId = FabVocabSQLHelper.getInstance(getContext()).addWord(word);
                }

                for (String definition : definitionsToAdd) {
                    str += definition + "\n";
                    FabVocabSQLHelper.getInstance(getContext()).addDefinition(wordId, definition);
                }

                new AlertDialog.Builder(context, R.style.MyAlertDialog)
                        .setTitle("Definitions Added:")
                        .setMessage(str)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    public void setWord(String word) {
        ((TextView) v.findViewById(R.id.tv_word)).setText(word);
    }



}
