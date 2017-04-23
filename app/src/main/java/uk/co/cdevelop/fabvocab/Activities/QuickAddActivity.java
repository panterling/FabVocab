package uk.co.cdevelop.fabvocab.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import uk.co.cdevelop.fabvocab.Fragments.HomePageFragment;
import uk.co.cdevelop.fabvocab.Fragments.QuickAddFragment;
import uk.co.cdevelop.fabvocab.R;

public class QuickAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        final CharSequence wordText = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        setContentView(R.layout.activity_quickadd);

        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.flcontent_quickadd, new QuickAddFragment(wordText.toString())).commit();
        }

    }
}
