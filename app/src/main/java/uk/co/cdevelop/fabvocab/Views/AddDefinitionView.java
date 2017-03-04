package uk.co.cdevelop.fabvocab.Views;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.DataModels.OxfordAPIWordDefinition;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 15/01/2017.
 */

public class AddDefinitionView extends LinearLayout {

    View v;
    ArrayList<String> definitionsToAdd;
    Button addButton;

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

    public void init(final Context context) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //v = mInflater.inflate(R.layout.definition_view, this, true);
        definitionsToAdd = new ArrayList<String>();

        addButton = (Button) findViewById(R.id.btn_addword);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = (String) ((TextView) getRootView().findViewById(R.id.tv_word)).getText();
                String str = "";

                SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();

                // Get Word Id
                long word_id = -1;

                Cursor wordFound = db.rawQuery("SELECT _id FROM words WHERE word = ?", new String[]{word});
                if(wordFound.getCount() == 0) {
                    // Insert
                    ContentValues values = new ContentValues();
                    values.put("word", word);
                    word_id = db.insert("words", null, values);
                } else {
                    wordFound.moveToNext();
                    word_id = wordFound.getInt(wordFound.getColumnIndex("_id"));
                }

                for (String definition : definitionsToAdd) {
                    str += definition + "\n";
                    ContentValues values = new ContentValues();
                    values.put(FabVocabContract.DefinitionEntry.COLUMN_WORD_ID, word_id);
                    values.put(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION, definition);

                    db.insert(FabVocabContract.DefinitionEntry.TABLE_NAME, null, values);
                }

                new AlertDialog.Builder(context, R.style.MyAlertDialog)
                        .setTitle("Definitions Added:")
                        .setMessage(str)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    public void setBody(String body) {
        //((TextView) v.findViewById(R.id.tv_body)).setText(body);
    }

    public void setWord(String word) {
        ((TextView) v.findViewById(R.id.tv_word)).setText(word);
    }


    public void setDefinition(OxfordAPIWordDefinition wd) {
        /*LinearLayout bodyLayout = (LinearLayout) v.findViewById(R.id.sv_body);

        //Remove any dangling definitions from previous searches
        definitionsToAdd.clear();
        bodyLayout.removeAllViews(); //TODO: Change to using a listview rather than adding textviews to a scrollview?


        setWord(wd.getWordId());



        LayoutParams definitionLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        definitionLayoutParams.setMargins(25, 0, 0, 0);

        String body = "";

        int r = 0;
        for(OxfordAPIWordDefinition.WordDefinitionResult result : wd.getWordDefinitionResults()) {
            body += "Result [" + (r++) + "]: \n";

            for(OxfordAPIWordDefinition.WordDefinitionResultCategory category : result.getCategories()) {
                TextView newSense = new TextView(this.getContext());
                newSense.setText(category.getCategoryId());
                bodyLayout.addView(newSense);

                for (final String definition : category.getDefinitions()) {
                    CheckBox newCheck = new CheckBox(this.getContext());

                    newCheck.setText(definition);

                    newCheck.setLayoutParams(definitionLayoutParams);

                    newCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                definitionsToAdd.add(definition);
                            } else {
                                definitionsToAdd.remove(definition);
                            }

                            addButton.setEnabled(definitionsToAdd.size() > 0);
                        }
                    });

                    bodyLayout.addView(newCheck);
                }
            }
        }

        addButton.setEnabled(false);*/
    }
}
