package uk.co.cdevelop.fabvocab.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.cdevelop.fabvocab.Fragments.PracticeActivity.WordPracticeFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;

/**
 * Created by Chris on 04/02/2017.
 */

public class PracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_home);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(mode);

        ViewGroup layout = (ViewGroup) findViewById(R.id.practice_homelayout);
        layout.addView(textView);

        if(mode.equals("doword")) {
            int word_id = intent.getIntExtra("word_id", -1);
            if (word_id != -1) {

                TextView tvWord = new TextView(this);
                tvWord.setTextSize(40);
                tvWord.setText(mode);

                WordEntry wordFromDb = FabVocabSQLHelper.getInstance(this).getWord(word_id);
                if(wordFromDb != null) {
                    tvWord.setText("Word to practice: " + wordFromDb.getWord());
                } else {
                    tvWord.setText("Invalid word_id - not found in DB");
                }

                layout.addView(tvWord);

                WordPracticeFragment wpFragment = new WordPracticeFragment();
                Bundle fragArgs = new Bundle();
                fragArgs.putInt("word_id", word_id);
                wpFragment.setArguments(fragArgs);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontent, wpFragment).commit();
            }
        }
    }

}
