package uk.co.cdevelop.fabvocab.ListAdapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.DataModels.WordHolder;
import uk.co.cdevelop.fabvocab.Fragments.WordReviewFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 13/02/2017.
 */

public class RecentlyAddedWordsListAdapter extends ArrayAdapter implements IRefreshable {
    private final ArrayList<WordHolder> wordsList;
    private final Context context;

    public RecentlyAddedWordsListAdapter(Context context, ArrayList<WordHolder> list) {
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
                bundle.putInt("word_id", wordsList.get(position).getWordId());
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

    public void showAll() {
        wordsList.clear();

        SQLiteDatabase db = FabVocabSQLHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FabVocabContract.WordEntry.COLUMN_NAME_WORD + ", " + FabVocabContract.WordEntry._ID + " FROM " + FabVocabContract.WordEntry.TABLE_NAME + " WHERE " + FabVocabContract.WordEntry.COLUMN_NAME_ADDED + " > datetime('now', '-7 days') ORDER BY " + FabVocabContract.WordEntry.COLUMN_NAME_ADDED + " DESC", null);

        if(cursor.getCount() > 0) {
            ArrayList<String> recentlyAddedWordsList = new ArrayList<String>();
            while (cursor.moveToNext()) {
                wordsList.add(new WordHolder(cursor.getString(cursor.getColumnIndex(FabVocabContract.WordEntry.COLUMN_NAME_WORD)),
                                             cursor.getInt(cursor.getColumnIndex(FabVocabContract.WordEntry._ID))));
            }

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
