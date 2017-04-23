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

import uk.co.cdevelop.fabvocab.Subscription.AddWordsResultsViewSelectionSubscriber;
import uk.co.cdevelop.fabvocab.Subscription.AddWordsResultsViewAudioUrlSubscriber;
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


    private AddWordsResultFragmentPagerAdapter adapter;
    private ArrayList<DefinitionEntry> definitionsToAdd;
    private ArrayList<DefinitionEntry> definitionsAlreadyExist;
    private AddWordsResultFragment[] fragments;
    private String audioUrl = "";

    private ArrayList<AddWordsResultsViewSelectionSubscriber> selectionChangeSubscribers = new ArrayList<>();
    private ArrayList<AddWordsResultsViewAudioUrlSubscriber> audioUrlSubscribers = new ArrayList<>();
    public void subscribeSelectionChange(AddWordsResultsViewSelectionSubscriber subscriber) {
        selectionChangeSubscribers.add(subscriber);
    }
    public void subscribeAudioUrl(AddWordsResultsViewAudioUrlSubscriber subscriber) {
        audioUrlSubscribers.add(subscriber);
    }
    // Unsubscribe functions here AS-AND-WHEN-NEEDED

    private State resultState = State.IDLE;
    private boolean resultsReceived[];

    public AddWordsResultsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.i("stateTransition", "AddWordsResultsView: Constructor");

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


    public void giveResults(Constants.APIType owner, APIResultSet response) {
        if(adapter != null) {
            adapter.giveResult(owner, response.getDefinitions());
            setResultReceived(owner.ordinal());

            // Audio URL
            if(isValidAudioUrl(response.getAudioUrl())) {
                if(audioUrl.equals("")) {
                    audioUrl = response.getAudioUrl();

                    publishAudioUrl();
                }

            }
        } else {
            Log.e("Err", "Attempt to pass a CustomStringRequest response to an uninitialised adapter.");
        }
    }

    public void addDefinitionForSaving(String definition){
        definitionsToAdd.add(new DefinitionEntry(-1, definition));
        publishSelectionChange();
    }
    public void removeDefinitionForSaving(String definition){
        definitionsToAdd.remove(new DefinitionEntry(-1, definition));
        publishSelectionChange();
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

    public void publishSelectionChange() {
        for(AddWordsResultsViewSelectionSubscriber subscriber : selectionChangeSubscribers) {
            subscriber.selectionChanged(definitionsToAdd.size());
        }
    }

    public void publishAudioUrl() {
        for(AddWordsResultsViewAudioUrlSubscriber subscriber : audioUrlSubscribers) {
            subscriber.audioUrlChange(audioUrl);
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedInstanceState) {
        SaveState state = (SaveState) savedInstanceState;
        super.onRestoreInstanceState(state.getSuperState());

        Log.i("stateTransition", "AddWordsResultsView: Restore");

        if (savedInstanceState != null) // implicit null check
        {
            Bundle bundle = state.getBundle();
            resultState = State.values()[bundle.getInt("resultState")];

            if(resultState == State.SHOWING_RESULTS) {
                setVisibility(View.VISIBLE);
                setAlpha(1.0f);

                ArrayList<WordDefinition> oxfordResults = (ArrayList<WordDefinition>) bundle.getSerializable("oxford");
                ArrayList<WordDefinition> merriamResults = (ArrayList<WordDefinition>) bundle.getSerializable("merriam");
                ArrayList<WordDefinition> collinsResults = (ArrayList<WordDefinition>) bundle.getSerializable("collins");
                fragments[0].setResults(oxfordResults);
                fragments[1].setResults(merriamResults);
                fragments[2].setResults(collinsResults);

                adapter.notifyDataSetChanged();

                this.definitionsToAdd = (ArrayList<DefinitionEntry>) bundle.getSerializable("definitionsToAdd");

            } else if(resultState == State.RESULTS_PENDING) {
                setVisibility(View.VISIBLE);
                setAlpha(1.0f);

                fragments[0].setAsInProgress();
                fragments[1].setAsInProgress();
                fragments[2].setAsInProgress();

                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();


        Bundle bundle = new Bundle();

        // TODO: Determine based on current state
        bundle.putInt("resultState", resultState.ordinal());
        if(resultState == State.SHOWING_RESULTS) {
            bundle.putSerializable("oxford", fragments[0].getResults());
            bundle.putSerializable("merriam", fragments[1].getResults());
            bundle.putSerializable("collins", fragments[2].getResults());

            bundle.putSerializable("definitionsToAdd", definitionsToAdd);
        }

        SaveState state = new SaveState(superState, bundle);

        return state;
    }
}