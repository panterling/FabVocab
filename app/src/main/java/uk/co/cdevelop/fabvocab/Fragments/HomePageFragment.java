package uk.co.cdevelop.fabvocab.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import uk.co.cdevelop.fabvocab.Adapters.AddWordsLaterListAdapter;
import uk.co.cdevelop.fabvocab.Adapters.IRefreshable;
import uk.co.cdevelop.fabvocab.Adapters.RecentlyAddedWordsListAdapter;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;

/**
 * Created by Chris on 04/02/2017.
 */

public class HomePageFragment extends Fragment implements IFragmentWithCleanUp {

    private ArrayList<IRefreshable> refreshables = new ArrayList<>();


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


        ((TextView) view.findViewById(R.id.tv_progress_totalwords)).setText("Total Words: " + Integer.toString(FabVocabSQLHelper.getInstance(getContext()).getWordsCount()));

        ((TextView) view.findViewById(R.id.tv_progress_masteredwords)).setText("Mastered Words: " + Integer.toString(FabVocabSQLHelper.getInstance(getContext()).getMasteredWordsCount()));

        ((TextView) view.findViewById(R.id.tv_progress_totalpractice)).setText("Total Practice Sessions: " + Integer.toString(FabVocabSQLHelper.getInstance(getContext()).getPracticedSessionsCount()));

        ((TextView) view.findViewById(R.id.tv_progress_totalunpracticedwords)).setText("Total Unpracticed Words: " + Integer.toString(FabVocabSQLHelper.getInstance(getContext()).getUnpracticedWordsCount()));
        // END: PROGRESS STATISTICS


        // START: RECENTLY ADDED
        RecentlyAddedWordsListAdapter recentlyAddedWordsListAdapter = new RecentlyAddedWordsListAdapter(getContext(), new ArrayList<WordEntry>());
        lvRecentlyAdded.setAdapter(recentlyAddedWordsListAdapter);//new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, recentlyAddedWordsList));
        refreshables.add(recentlyAddedWordsListAdapter);
        // END: RECENTLY ADDED


        // START: ADD WORDS LATER LIST

        if(FabVocabSQLHelper.getInstance(getContext()).getAddWordsLaterCount() > 0) {
            tvAddWordsLaterBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        AddWordsLaterListAdapter addWordsLaterListAdapter = new AddWordsLaterListAdapter(getContext(), new ArrayList<String>());
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
