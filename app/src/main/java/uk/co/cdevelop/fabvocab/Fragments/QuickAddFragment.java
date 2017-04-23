package uk.co.cdevelop.fabvocab.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.SQL.Models.DefinitionEntry;
import uk.co.cdevelop.fabvocab.Subscription.AddWordsResultsViewSelectionSubscriber;
import uk.co.cdevelop.fabvocab.Subscription.RequestWordCompleteSubscriber;
import uk.co.cdevelop.fabvocab.Support.Constants;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;
import uk.co.cdevelop.fabvocab.WebRequest.RequestWord;

/**
 * Created by Chris on 13/02/2017.
 */

public class QuickAddFragment extends Fragment implements IFragmentWithCleanUp, IAddResultsViewOwner, AddWordsResultsViewSelectionSubscriber, RequestWordCompleteSubscriber {

    public enum State {
        IDLE,
        RESULTS_PENDING,
        SHOWING_RESULTS
    }

    private AddWordsResultsView addWordsResultsView;
    private RequestWord requester;
    private String word;
    private int wordId;
    private State fragmentState = State.IDLE;

    private Button btnDone;


    // Necessary to allow for screen orientation
    public QuickAddFragment(){}

    public QuickAddFragment(String word) {
        this.word = word;
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        Log.i("stateTransitions", "QuickAddFragment: onCreateView");
        return inflator.inflate(R.layout.quickadd_main, container, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        Log.i("stateTransition", "QuickAddFragment: onCreateView");

        if(savedInstanceState != null) {
            word = savedInstanceState.getString("word");
        }

        // Check if the word already exists in My Dictionary
        wordId = FabVocabSQLHelper.getInstance(getContext()).getWordId(word.toLowerCase());
        final boolean isNewWord = (wordId == -1);

        boolean isInAddLater = false;
        if(isNewWord) {
            if(FabVocabSQLHelper.getInstance(getContext()).existsInAddWordsLater(word.toLowerCase())) {
                isInAddLater = true;
            }
        }


        final LinearLayout llAddNow = (LinearLayout) view.findViewById(R.id.ll_quickadd_addnow);
        final LinearLayout llContainer = (LinearLayout) view.findViewById(R.id.ll_quickadd_container);
        final LinearLayout llAddButtons = (LinearLayout) view.findViewById(R.id.ll_quickadd_addbuttons);
        final LinearLayout llWordReview = (LinearLayout) view.findViewById(R.id.ll_quickadd_wordreview);
        final ListView lvDefinitions = (ListView) view.findViewById(R.id.lv_quickadd_definitions);
        addWordsResultsView = (AddWordsResultsView) view.findViewById(R.id.awrv_quickadd_results);

        final TextView tvWord = (TextView) view.findViewById(R.id.tv_quickadd_word);
        final TextView tvInAddLater = (TextView) view.findViewById(R.id.tv_quickadd_inaddlater);
        btnDone = (Button) view.findViewById(R.id.btn_quickadd_done);
        final Button btnClose = (Button) view.findViewById(R.id.btn_quickadd_close);
        final Button btnAddNow = (Button) view.findViewById(R.id.btn_quickadd_addnow);
        final Button btnAddLater = (Button) view.findViewById(R.id.btn_quickadd_addlater);

        final ImageView ivNewWordIcon = (ImageView) view.findViewById(R.id.iv_quickadd_newwordicon);


        tvWord.setText(word.toLowerCase());

        addWordsResultsView.subscribeSelectionChange(this);


        if(isNewWord) {
            ivNewWordIcon.setVisibility(View.VISIBLE);
            llAddButtons.setVisibility(View.VISIBLE);
            if (isInAddLater) {
                btnAddLater.setVisibility(View.GONE);
                tvInAddLater.setVisibility(View.VISIBLE);
            }
        } else {
            llWordReview.setVisibility(View.VISIBLE);


            ArrayList<DefinitionEntry> definitionList = FabVocabSQLHelper.getInstance(getContext()).getAllDefinitions(wordId);

            ArrayAdapter<DefinitionEntry> definitions = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, definitionList);
            lvDefinitions.setAdapter(definitions);
            btnDone.setVisibility(View.VISIBLE);
        }

        btnAddLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FabVocabSQLHelper.getInstance(getContext()).existsInAddWordsLater(word.toLowerCase())) {
                    Toast.makeText(getContext(), "Already in your 'Add Later' List!", Toast.LENGTH_SHORT).show();
                } else {
                    FabVocabSQLHelper.getInstance(getContext()).addWordForLater(word.toLowerCase());
                    Toast.makeText(getContext(), "Added to 'Add Later' List!", Toast.LENGTH_SHORT).show();
                }
                returnToCallingActivity();
            }
        });

        btnAddNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAddNow.setVisibility(View.VISIBLE);
                btnAddNow.setVisibility(View.GONE);

                btnDone.setEnabled(false);
                btnDone.setVisibility(View.VISIBLE);

                try {
                    requester = new RequestWord(getContext());
                    requester.allCompleteSubscribe(QuickAddFragment.this);
                    requester.requestAll(word.toLowerCase(), addWordsResultsView);
                    fragmentState = State.RESULTS_PENDING;

                    addWordsResultsView.clearAll();
                    addWordsResultsView.setAllInProgress();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the calling app e.g. Chrome/PDF viewer etc.

                if(isNewWord) {
                    int wordId = FabVocabSQLHelper.getInstance(getContext()).addWord(word.toLowerCase());

                    // Add Definitions
                    for (DefinitionEntry definitionEntry : addWordsResultsView.getDefinitionsToAdd()) {
                        FabVocabSQLHelper.getInstance(getContext()).addDefinition(wordId, definitionEntry.getDefinition());
                    }

                    Toast.makeText(getContext(), "Added word to My Dictionary", Toast.LENGTH_SHORT).show();
                }

                returnToCallingActivity();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToCallingActivity();
            }
        });

        if(savedInstanceState != null) {
            State restoredFragmentState = State.values()[ savedInstanceState.getInt("fragmentState")];
            if(restoredFragmentState != null) {
                fragmentState = restoredFragmentState;
                if(fragmentState == State.SHOWING_RESULTS) {

                    llAddNow.setVisibility(View.VISIBLE);
                    btnAddNow.setVisibility(View.GONE);

                    btnDone.setEnabled(savedInstanceState.getBoolean("btnDoneEnabled", false));
                    btnDone.setVisibility(View.VISIBLE);

                } else if(fragmentState == State.RESULTS_PENDING) {
                    btnAddNow.callOnClick();
                }
            }
        }
    }



    @Override
    public AddWordsResultsView getResultView() {
        return addWordsResultsView;
    }

    @Override
    public void cleanUp() {

    }

    private void returnToCallingActivity() {
        //((Activity) getContext()).setResult(Activity.RESULT_OK, null);
        Intent intent = ((Activity) getContext()).getIntent();
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "testing");
        ((Activity) getContext()).setResult(Activity.RESULT_OK, intent);
        ((Activity) getContext()).finish();
    }


    @Override
    public void selectionChanged(int selectionSize) {
        btnDone.setEnabled(selectionSize > 0);
    }

    @Override
    public void requestComplete() {
        fragmentState = State.SHOWING_RESULTS;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("word", word);
        bundle.putInt("fragmentState", fragmentState.ordinal());
        bundle.putBoolean("btnDoneEnabled", btnDone.isEnabled());
    }

    @Override
    public void onPause(){
        super.onPause();

        if(requester != null) {
            requester.cancelAll();
        }

        addWordsResultsView.cleanUp();

    }

    @Override
    public void onResume() {
        super.onResume();
        addWordsResultsView.setDefinitionsThatAlreadyExist(FabVocabSQLHelper.getInstance(getContext()).getAllDefinitions(wordId));
    }
}
