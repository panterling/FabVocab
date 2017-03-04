package uk.co.cdevelop.fabvocab.Fragments.Practice;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 04/02/2017.
 */

public class WordPracticeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.practice_word, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final Button btnDone = (Button) view.findViewById(R.id.btn_done);
        final TextView tvWord = (TextView) view.findViewById(R.id.tv_word);
        final TextView tvRecall = (TextView) view.findViewById(R.id.tv_recall);
        final TextView tvFluency = (TextView) view.findViewById(R.id.tv_fluency);
        final ImageView ivBlurredDefinitions = (ImageView) view.findViewById(R.id.iv_blurreddefinitions);
        final ImageView ivBlurredExamples = (ImageView) view.findViewById(R.id.iv_blurredexamples);
        final ListView lvDefinitions = (ListView) view.findViewById(R.id.lv_definitions);
        final ListView lvExamples = (ListView) view.findViewById(R.id.lv_examples);
        final SeekBar sbRecall = (SeekBar) view.findViewById(R.id.rating_recall);
        final SeekBar sbFluency = (SeekBar) view.findViewById(R.id.rating_fluency);

        ArrayList<String> definitionsList = new ArrayList<String>();
        ArrayList<String> examplesList = new ArrayList<String>();


        final int word_id = getArguments().getInt("word_id");

        final SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();
        Cursor cursor;


        // Get Word
        cursor = db.rawQuery("SELECT word FROM words WHERE _id = ?", new String[]{Integer.toString(word_id)});
        cursor.moveToNext();
        tvWord.setText(cursor.getString(cursor.getColumnIndex("word")));


        // Get Definitions
        cursor = db.rawQuery("SELECT definition FROM definitions WHERE word_id = ?", new String[]{Integer.toString(word_id)});
        while(cursor.moveToNext()) {
            definitionsList.add(cursor.getString(cursor.getColumnIndex("definition")));
        }
        lvDefinitions.setAdapter(new ArrayAdapter<String>(getContext(),  android.R.layout.simple_list_item_1, definitionsList));


        // Get Examples
        // TODO: examples functionality
        examplesList.add("A dummy example 1 ...");
        examplesList.add("A dummy example 2 ...");
        lvExamples.setAdapter(new ArrayAdapter<String>(getContext(),  android.R.layout.simple_list_item_1, examplesList));




        // START: Callbacks and flow-control
        ivBlurredDefinitions.setVisibility(View.VISIBLE);
        ivBlurredExamples.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
        lvDefinitions.setVisibility(View.GONE);
        tvRecall.setVisibility(View.GONE);
        tvFluency.setVisibility(View.GONE);
        lvExamples.setVisibility(View.GONE);
        sbRecall.setVisibility(View.GONE);
        sbFluency.setVisibility(View.GONE);

        ivBlurredDefinitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivBlurredDefinitions.setVisibility(View.GONE);
                lvDefinitions.setVisibility(View.VISIBLE);
                tvRecall.setVisibility(View.VISIBLE);
                sbRecall.setVisibility(View.VISIBLE);
            }
        });

        sbRecall.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ivBlurredExamples.setVisibility(View.VISIBLE);
            }
        });

        ivBlurredExamples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivBlurredExamples.setVisibility(View.GONE);
                lvExamples.setVisibility(View.VISIBLE);
                tvFluency.setVisibility(View.VISIBLE);
                sbFluency.setVisibility(View.VISIBLE);
            }
        });

        sbFluency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                btnDone.setVisibility(View.VISIBLE);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(FabVocabContract.WordPractice.COLUMN_NAME_RECALL_RATING, sbRecall.getProgress());
                values.put(FabVocabContract.WordPractice.COLUMN_NAME_FLUENCY_RATING, sbFluency.getProgress());
                values.put(FabVocabContract.WordPractice.COLUMN_NAME_WORD_ID, word_id);
                db.insert(FabVocabContract.WordPractice.TABLE_NAME, null, values);

                ((Activity) getContext()).finish();
            }
        });

        // END: Callbacks and flow-control

    }
}
