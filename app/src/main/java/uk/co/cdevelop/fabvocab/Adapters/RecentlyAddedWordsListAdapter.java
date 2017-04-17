package uk.co.cdevelop.fabvocab.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.Fragments.WordReviewFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;

/**
 * Created by Chris on 13/02/2017.
 */

public class RecentlyAddedWordsListAdapter extends ArrayAdapter<WordEntry> implements IRefreshable {
    private final ArrayList<WordEntry> wordsList;
    private final Context context;

    public RecentlyAddedWordsListAdapter(Context context, ArrayList<WordEntry> list) {
        super(context, -1, list);

        this.wordsList = list;
        this.context = context;

        showAll();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.homepage_recentlyaddedwordsitem, parent, false);

        final TextView tvWord = (TextView) rowView.findViewById(R.id.tv_homepage_recentlyaddedwordsitem_word);
        final Button btnGoto = (Button) rowView.findViewById(R.id.btn_homepage_recentlyaddedwordsitem_goto);

        tvWord.setText(wordsList.get(position).getWord());

        btnGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordReviewFragment newFragment = new WordReviewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("word_id", wordsList.get(position).getId());
                newFragment.setArguments(bundle);

                ((FragmentActivity)getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.flcontent, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rowView;
    }

    private void showAll() {
        wordsList.clear();

        ArrayList<WordEntry> dbWordsList = FabVocabSQLHelper.getInstance(getContext()).getRecentlyAddedWords();
        if(dbWordsList != null && dbWordsList.size() > 0) {
            ArrayList<String> recentlyAddedWordsList = new ArrayList<>();
                wordsList.addAll(dbWordsList);

        } else {
            // TODO: No recently added words UI
        }

        notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        showAll();
    }
}
