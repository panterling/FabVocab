package uk.co.cdevelop.fabvocab.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.Activities.MainActivity;
import uk.co.cdevelop.fabvocab.Activities.PracticeActivity;
import uk.co.cdevelop.fabvocab.DataModels.WordHolder;
import uk.co.cdevelop.fabvocab.ListAdapters.AddWordsLaterListAdapter;
import uk.co.cdevelop.fabvocab.ListAdapters.IRefreshable;
import uk.co.cdevelop.fabvocab.ListAdapters.RecentlyAddedWordsListAdapter;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 04/02/2017.
 */

public class HomePageFragment extends Fragment implements IFragmentWithCleanUp {

    private ArrayList<IRefreshable> refreshables = new ArrayList<IRefreshable>();


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        return inflator.inflate(R.layout.homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        refreshables.clear();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        ((MainActivity) getActivity()).showFloatingAddButton();


        final ListView lvRecentlyAdded = (ListView) view.findViewById(R.id.lv_recentlyadded);
        final ListView lvAddWordsLater = (ListView) view.findViewById(R.id.lv_addwordslater);

        final TextView tvAddWordsLaterBar = (TextView) view.findViewById(R.id.tv_homepage_addwordslaterbar);

        // START: PROGRESS STATISTICS
        FabVocabSQLHelper sqlHelper = FabVocabSQLHelper.getInstance(getContext());
        SQLiteDatabase db = sqlHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("SELECT count(*) as totalwords FROM words", null);
        cursor.moveToNext();
        int stat_totalWords = cursor.getInt(cursor.getColumnIndex("totalwords"));
        ((TextView) view.findViewById(R.id.tv_progress_totalwords)).setText("Total Words: " + Integer.toString(stat_totalWords));

        cursor = db.rawQuery("SELECT count(*) as masteredwords FROM word_practice WHERE recall_rating = 10 and fluency_rating = 10", null);
        cursor.moveToNext();
        int stat_masteredWords = cursor.getInt(cursor.getColumnIndex("masteredwords"));
        ((TextView) view.findViewById(R.id.tv_progress_masteredwords)).setText("Mastered Words: " + Integer.toString(stat_masteredWords));

        cursor = db.rawQuery("SELECT count(*) as totalpracticesessions FROM word_practice", null);
        cursor.moveToNext();
        int stat_totalpractice = cursor.getInt(cursor.getColumnIndex("totalpracticesessions"));
        ((TextView) view.findViewById(R.id.tv_progress_totalpractice)).setText("Total Practice Sessions: " + Integer.toString(stat_totalpractice));

        cursor = db.rawQuery("select (total.total - sessions.uniquewords) as unpracticedwords from (select count(*) as total from words) as total, (select count(distinct word_id) as uniquewords from word_practice ) as sessions", null);
        cursor.moveToNext();
        int stat_unpracticedwords = cursor.getInt(cursor.getColumnIndex("unpracticedwords"));
        ((TextView) view.findViewById(R.id.tv_progress_totalunpracticedwords)).setText("Total Unpracticed Words: " + Integer.toString(stat_unpracticedwords));
        // END: PROGRESS STATISTICS


        // START: RECENTLY ADDED
        ArrayList<WordHolder> recentlyAddedWordsList = new ArrayList<WordHolder>();
        RecentlyAddedWordsListAdapter recentlyAddedWordsListAdapter = new RecentlyAddedWordsListAdapter(getContext(), recentlyAddedWordsList);
        lvRecentlyAdded.setAdapter(recentlyAddedWordsListAdapter);//new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, recentlyAddedWordsList));
        refreshables.add(recentlyAddedWordsListAdapter);
        // END: RECENTLY ADDED


        // START: ADD WORDS LATER LIST
        cursor = db.rawQuery("SELECT " + FabVocabContract.AddWordLater.COLUMN_NAME_WORD + " FROM " + FabVocabContract.AddWordLater.TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            tvAddWordsLaterBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        ArrayList<String> addWordsList = new ArrayList<String>();
        AddWordsLaterListAdapter addWordsLaterListAdapter = new AddWordsLaterListAdapter(getContext(), addWordsList);
        lvAddWordsLater.setAdapter(addWordsLaterListAdapter);
        refreshables.add(addWordsLaterListAdapter);
        // END: ADD WORDS LATER LIST



        final Button btnDoDailyPractice = (Button) view.findViewById(R.id.btn_dailypractice);
        btnDoDailyPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PracticeActivity.class);
                intent.putExtra("mode", "dodaily");
                startActivity(intent);
            }
        });

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void onResume() {
        super.onResume();

        for(IRefreshable refreshable : refreshables) {
            refreshable.refresh();
        }
    }
}
