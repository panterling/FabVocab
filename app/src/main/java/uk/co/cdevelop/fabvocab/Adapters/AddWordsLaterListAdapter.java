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

import uk.co.cdevelop.fabvocab.Fragments.AddWordsFragment;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;

/**
 * Created by Chris on 11/02/2017.
 */
public class AddWordsLaterListAdapter extends ArrayAdapter<String> implements IRefreshable {
    private final ArrayList<String> addWordsLaterList;
    private Context context;

    public AddWordsLaterListAdapter(Context context, ArrayList<String> list) {
        super(context, -1, list);

        this.addWordsLaterList = list;
        this.context = context;
        showAll();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.homepage_addwordslateritem, parent, false);


        final TextView tvWord = (TextView) rowView.findViewById(R.id.tv_homepage_addwordslateritem_word);
        final Button btnDelete = (Button) rowView.findViewById(R.id.btn_homepage_addwordslateritem_delete);
        final Button btnAdd = (Button) rowView.findViewById(R.id.btn_homepage_addwordslateritem_add);

        tvWord.setText(addWordsLaterList.get(position));
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FabVocabSQLHelper.getInstance(getContext()).deleteWordForLater(tvWord.getText().toString());

                showAll();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWordsFragment newFragment = new AddWordsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("word", addWordsLaterList.get(position));
                newFragment.setArguments(bundle);

                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flcontent, newFragment).addToBackStack(null).commit();
            }
        });


        return rowView;
    }

    private void showAll() {
        addWordsLaterList.clear();

        ArrayList<String> wordsList = FabVocabSQLHelper.getInstance(context).getAllAddWordsLater();
        if (wordsList != null) {
            addWordsLaterList.addAll(wordsList);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        showAll();
    }
}
