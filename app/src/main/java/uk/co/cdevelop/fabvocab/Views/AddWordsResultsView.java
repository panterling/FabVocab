package uk.co.cdevelop.fabvocab.Views;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.AddWordsResultFragmentPagerAdapter;
import uk.co.cdevelop.fabvocab.DataModels.Constants;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsFragment;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsResultFragment;
import uk.co.cdevelop.fabvocab.R;

/**
 * Created by Chris on 11/02/2017.
 */

public class AddWordsResultsView extends LinearLayout {

    private ViewPager pager;
    private AddWordsResultFragmentPagerAdapter adapter;
    private ArrayList<String> definitionsToAdd;
    private ArrayList<String> definitionsAlreadyExist;
    private Button btnDone;
    private AddWordsResultFragment[] fragments;

    public AddWordsResultsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout container = (LinearLayout) inflater.inflate(R.layout.addwordsresult, this);

        definitionsToAdd = new ArrayList<String>();
        definitionsAlreadyExist = new ArrayList<String>();

        // Setup Pager for multiple dictionary results
        final AddWordsResultFragment oxfordAPIFragment = new AddWordsResultFragment("Oxford Dictionary");
        AddWordsResultFragment merriamWebsterAPIFragment = new AddWordsResultFragment("Merriam Webster Dictionary");
        AddWordsResultFragment collinsAPIFragment = new AddWordsResultFragment("Collins Dictionary");

        fragments = new AddWordsResultFragment[]{oxfordAPIFragment, merriamWebsterAPIFragment, collinsAPIFragment};
        adapter = new AddWordsResultFragmentPagerAdapter(getContext(), fragments);
        pager = (ViewPager) container.findViewById(R.id.pager_searchresults);
        pager.setAdapter(adapter);

    }

    //TODO: Ditch this dangerous function
    public void setParent(Button btnDone) {
        this.btnDone = btnDone;
    }

    public void giveResults(Constants.APIType owner, ArrayList<String> responseDefinitions) {
        if(adapter != null) {
            adapter.giveResult(owner, responseDefinitions);
        } else {
            Log.e("Err", "Attempt to pass a CustomStringRequest response to an uninitialised adapter.");
        }
    }

    public void addDefinitionForSaving(String definition){
        definitionsToAdd.add(definition);
        btnDone.setEnabled(definitionsToAdd.size() > 0);
    }
    public void removeDefinitionForSaving(String definition){
        definitionsToAdd.remove(definition);
        btnDone.setEnabled(definitionsToAdd.size() > 0);
    }

    public boolean isChecked(String definition) {
        return definitionsToAdd.contains(definition);
    }

    public void setDefinitionsThatAlreadyExist(ArrayList<String> list) {
        definitionsAlreadyExist = list;
    }

    public void setAllInProgress() {
        adapter.allInProgress();
    }

    public ArrayList<String> getDefinitionsToAdd() {
        return definitionsToAdd;
    }

    public void clearAll() {
        definitionsToAdd.clear();
    }

    public void cleanUp() {
        adapter.removeChildren();
    }

    public void refreshResultsFragments() {
        adapter.notifyDataSetChanged();
    }

    public boolean definitionExists(String definition) {
        return definitionsAlreadyExist.contains(definition);
    }

    public void detachChildren() {
        FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
        for(Fragment f : fragments) {
            fm.beginTransaction().detach(f).commit();
        }
    }

    public void definitionAddedToDB() {
        definitionsAlreadyExist.addAll(definitionsToAdd);
        definitionsToAdd.clear();

        // Refresh result fragments
        adapter.notifyDataSetChanged();
    }
}
