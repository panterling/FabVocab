package uk.co.cdevelop.fabvocab.Views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.cdevelop.fabvocab.Support.SaveState;
import uk.co.cdevelop.fabvocab.Adapters.AddWordsResultFragmentPagerAdapter;
import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsFragment;
import uk.co.cdevelop.fabvocab.SQL.Models.DefinitionEntry;
import uk.co.cdevelop.fabvocab.Support.Constants;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsResultFragment;
import uk.co.cdevelop.fabvocab.R;

/**
 * Created by Chris on 11/02/2017.
 */

public class AddWordsResultsView extends LinearLayout {

    public enum State {
        IDLE,
        RESULTS_PENDING,
        SHOWING_RESULTS
    }


    public void setDefinitionsToAdd(ArrayList<DefinitionEntry> definitionsToAdd) {
        this.definitionsToAdd = definitionsToAdd;
    }

    private AddWordsFragment parent;
    private AddWordsResultFragmentPagerAdapter adapter;
    private ArrayList<DefinitionEntry> definitionsToAdd;
    private ArrayList<DefinitionEntry> definitionsAlreadyExist;
    private Button btnDone;
    private AddWordsResultFragment[] fragments;
    private String audioUrl = "";

    private State resultState = State.IDLE;
    private boolean resultsReceived[];

    public AddWordsResultsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LinearLayout container = (LinearLayout) inflate(getContext(), R.layout.addwordsresult, this);

        this.definitionsToAdd = new ArrayList<>();
        this.definitionsAlreadyExist = new ArrayList<>();

        // Setup Pager for multiple dictionary results
        final AddWordsResultFragment oxfordAPIFragment = new AddWordsResultFragment("Oxford Dictionary");
        AddWordsResultFragment merriamWebsterAPIFragment = new AddWordsResultFragment("Merriam Webster Dictionary");
        AddWordsResultFragment collinsAPIFragment = new AddWordsResultFragment("Collins Dictionary");

        this.fragments = new AddWordsResultFragment[]{oxfordAPIFragment, merriamWebsterAPIFragment, collinsAPIFragment};
        this.adapter = new AddWordsResultFragmentPagerAdapter(getContext(), fragments);
        ViewPager pager = (ViewPager) container.findViewById(R.id.pager_searchresults);
        pager.setAdapter(adapter);

        // Reset ResultsReceived flag array
        resultsReceived = new boolean[fragments.length];
        for(int i = 0; i < resultsReceived.length; i++) {
            resultsReceived[i] = false;
        }
    }


    public void setResultReceived(int owner) {
        resultsReceived[owner] = true;

        for (boolean state : resultsReceived) {
            if(state == false) {
                return;
            }
        }

        resultState = State.SHOWING_RESULTS;
    }

    public State getState() {
        return resultState;
    }

    public void setParent(AddWordsFragment parent) {
        this.parent = parent;
    }

    //TODO: Ditch this dangerous function
    public void setAddButton(Button btnDone) {
        this.btnDone = btnDone;
    }

    public void giveResults(Constants.APIType owner, APIResultSet response) {
        if(adapter != null) {
            adapter.giveResult(owner, response.getDefinitions());
            setResultReceived(owner.ordinal());

            // Audio URL
            if(isValidAudioUrl(response.getAudioUrl())) {
                if(audioUrl.equals("")) {
                    audioUrl = response.getAudioUrl();

                    if(parent != null) {
                        parent.giveAudioUrl(audioUrl);
                    }
                }

            }
        } else {
            Log.e("Err", "Attempt to pass a CustomStringRequest response to an uninitialised adapter.");
        }
    }

    public void addDefinitionForSaving(String definition){
        definitionsToAdd.add(new DefinitionEntry(-1, definition));
        btnDone.setEnabled(definitionsToAdd.size() > 0);
    }
    public void removeDefinitionForSaving(String definition){
        definitionsToAdd.remove(definition);
        btnDone.setEnabled(definitionsToAdd.size() > 0);
    }

    public boolean isChecked(String definition) {
        return definitionsToAdd.contains(new DefinitionEntry(-1, definition));
    }

    public void setDefinitionsThatAlreadyExist(ArrayList<DefinitionEntry> list) {
        definitionsAlreadyExist = list;
    }

    public void setAllInProgress() {
        resultState = State.RESULTS_PENDING;

        // Reset ResultsReceived flag array
        resultsReceived = new boolean[fragments.length];
        for(int i = 0; i < resultsReceived.length; i++) {
            resultsReceived[i] = false;
        }

        adapter.allInProgress();
    }

    public ArrayList<DefinitionEntry> getDefinitionsToAdd() {
        return definitionsToAdd;
    }

    public void clearAll() {
        definitionsToAdd.clear();
        audioUrl = "";

        for(int i = 0; i < resultsReceived.length; i++) {
            resultsReceived[i] = false;
        }
    }

    public void cleanUp() {
        adapter.removeChildren();
    }

    public void refreshResultsFragments() {
        adapter.notifyDataSetChanged();
    }

    public boolean definitionExists(String definition) {
        return definitionsAlreadyExist.contains(new DefinitionEntry(-1, definition));
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

    public String getAudioUrl() {
        return audioUrl;
    }

    public boolean isValidAudioUrl(String url) {
        //TODO: implement a http request to test if an audio URL (a) exists and (b) isn't above xx mb in size....
        return true;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedInstanceState) {
        SaveState state = (SaveState) savedInstanceState;
        super.onRestoreInstanceState(state.getSuperState());

        if (savedInstanceState != null) // implicit null check
        {
            Bundle bundle = state.getBundle();
            if(bundle.getBoolean("showResults")) {
                setVisibility(View.VISIBLE);
                setAlpha(1.0f);
            }
        }

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle bundle = new Bundle();

        // TODO: Determine based on current state
        bundle.putBoolean("showResults", true);

        SaveState state = new SaveState(superState, bundle);

        return state;
    }

    public HashMap<String, ArrayList<WordDefinition>> getResults() {
        HashMap<String, ArrayList<WordDefinition>> results = new HashMap<>();

        results.put("oxford", fragments[0].getResults());
        results.put("merriam", fragments[1].getResults());
        results.put("collins", fragments[2].getResults());

        return results;
    }
}