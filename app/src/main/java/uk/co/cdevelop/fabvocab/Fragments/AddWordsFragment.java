package uk.co.cdevelop.fabvocab.Fragments;

/**
 * Created by Chris on 16/01/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.cdevelop.fabvocab.Activities.MainActivity;
import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;
import uk.co.cdevelop.fabvocab.SQL.Models.DefinitionEntry;
import uk.co.cdevelop.fabvocab.SQL.Models.WordEntry;
import uk.co.cdevelop.fabvocab.Support.Constants;
import uk.co.cdevelop.fabvocab.Support.Helpers;
import uk.co.cdevelop.fabvocab.Fragments.Dialog.ManualAddDefinitionDialogFragment;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;
import uk.co.cdevelop.fabvocab.Views.PronounciationPlayerView;
import uk.co.cdevelop.fabvocab.WebRequest.RequestWord;

public class AddWordsFragment extends Fragment implements IFragmentWithCleanUp, IAddResultsViewOwner {

    private AddWordsResultsView addWordsResultsView;
    private int wordId;
    private boolean showingResult = false;

    private RequestWord requester;

    private ArrayList<WordEntry> allWords = new ArrayList<>();
    private ArrayList<String> didYouMeanList = new ArrayList<>();

    private Button btnWordExists;
    private EditText etWordInput;
    private Button btnAdd;
    private TextView tvDidYouMean;
    private ListView lvDidYouMean;
    private PronounciationPlayerView ppvAudio;


    public AddWordsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        return inflator.inflate(R.layout.addwords, container, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        // Set Title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Words");
        ((MainActivity) getActivity()).hideFloatingAddButton();

        allWords = FabVocabSQLHelper.getInstance(getContext()).getAllWords();

        final Button btnWebSearch = (Button) view.findViewById(R.id.btnSearch);
        final Button btnManualAdd = (Button) view.findViewById(R.id.btn_manualaddword);
        final TextView tvWord = (TextView) view.findViewById(R.id.tv_word);
        final RelativeLayout rlWord = (RelativeLayout) view.findViewById(R.id.rl_word);

        ppvAudio = (PronounciationPlayerView) view.findViewById(R.id.ppv_addaudio);

        btnAdd = (Button) view.findViewById(R.id.btn_addword);
        btnWordExists = (Button) view.findViewById(R.id.iv_addwords_exists);
        etWordInput = (EditText) view.findViewById(R.id.editSearch);
        tvDidYouMean = (TextView) view.findViewById(R.id.tv_didyoumean);
        lvDidYouMean = (ListView) view.findViewById(R.id.lv_didyoumean);

        final ArrayAdapter<String> didYouMeanAdapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, didYouMeanList);
        lvDidYouMean.setAdapter(didYouMeanAdapter);

        wordId = -1;

        addWordsResultsView = (AddWordsResultsView) view.findViewById(R.id.adwv_addwords_resultsview);
        addWordsResultsView.setAddButton(btnAdd); //TODO meaningless method name
        addWordsResultsView.setParent(this);

        // Likely an orientation change - Restore results and visibility if required
        if (savedInstanceState != null) {
            if(savedInstanceState.getBoolean("showingResults")) {
                showingResult = true;
                btnAdd.setVisibility(View.VISIBLE);
                addWordsResultsView.setVisibility(View.VISIBLE);
                addWordsResultsView.setAlpha(1.0f);
                rlWord.setVisibility(View.VISIBLE);
                rlWord.setAlpha(1.0f);

                HashMap<String, ArrayList<WordDefinition>> apiResults = (HashMap<String, ArrayList<WordDefinition>>) savedInstanceState.get("apiResults");

                ArrayList<WordDefinition> oxfordResults = apiResults.get("oxford");
                ArrayList<WordDefinition> merriamResults = apiResults.get("merriam");
                ArrayList<WordDefinition> collinsResults = apiResults.get("collins");

                if(oxfordResults != null) {
                    addWordsResultsView.giveResults(Constants.APIType.OXFORD, new APIResultSet(oxfordResults, ""));
                }

                if(merriamResults != null) {
                    addWordsResultsView.giveResults(Constants.APIType.MW, new APIResultSet(merriamResults, ""));
                }

                if(collinsResults != null) {
                    addWordsResultsView.giveResults(Constants.APIType.COLLINS, new APIResultSet(collinsResults, ""));
                }


                addWordsResultsView.setDefinitionsToAdd((ArrayList<DefinitionEntry>) savedInstanceState.get("selectedItems"));

            }
        }

        btnWordExists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordReviewFragment newFragment = new WordReviewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("word_id", wordId);
                newFragment.setArguments(bundle);

                ((FragmentActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.flcontent, newFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        btnWebSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setVisibility(View.VISIBLE);

                showingResult = true;
                addWordsResultsView.setVisibility(View.VISIBLE);
                addWordsResultsView.animate().alpha(1.0f);

                ppvAudio.reset();

                addWordsResultsView.setDefinitionsThatAlreadyExist(FabVocabSQLHelper.getInstance(getContext()).getAllDefinitions(wordId));

                String word = etWordInput.getText().toString();
                etWordInput.clearFocus();

                Matcher m = Pattern.compile("[a-zA-Z-']+").matcher(word);
                if(!m.matches())
                {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                            .setTitle("That's not a word!")
                            .setMessage("What kind of word is '" + word + "'?! \n\n ")
                            .setPositiveButton("OK, I'm sorry!", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    try {

                        if(requester != null) {
                            requester.cancelAll();
                        }
                        requester = new RequestWord(getContext());
                        requester.requestAll(word, addWordsResultsView);

                        // Prep UI
                        tvWord.setText(word);
                        rlWord.setVisibility(View.VISIBLE);
                        rlWord.animate().alpha(1.0f);

                        (view.findViewById(R.id.tv_word)).setVisibility(View.VISIBLE);
                        //(view.findViewById(R.id.pager_searchresults)).setVisibility(View.VISIBLE); //todo: make refernce the new customview
                        (view.findViewById(R.id.btn_addword)).setVisibility(View.VISIBLE);

                        addWordsResultsView.clearAll();
                        btnAdd.setEnabled(false);


                        // Set result fragments to 'searching'
                        addWordsResultsView.setAllInProgress();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etWordInput.getWindowToken(), 0);

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = tvWord.getText().toString().toLowerCase();
                String str = "";


                if(wordId == -1) {
                    wordId = FabVocabSQLHelper.getInstance(getContext()).addWord(word, addWordsResultsView.getAudioUrl());
                    str = "Added New Word: " + word + "\n";
                    updateWordView();
                }

                for (DefinitionEntry definitionEntry : addWordsResultsView.getDefinitionsToAdd()) {
                    FabVocabSQLHelper.getInstance(getContext()).addDefinition(wordId, definitionEntry.getDefinition());
                }

                str += "Added " + addWordsResultsView.getDefinitionsToAdd().size() + " definition(s)";

                // Update GUI to prevent repeated addition of the same definition
                addWordsResultsView.definitionAddedToDB();
                btnAdd.setEnabled(false);

                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
            }
        });

        btnManualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = etWordInput.getText().toString();

                // TODO: Create 'VALID WORD' helper function for use app-wide
                if(!word.trim().equals("")) {
                    ManualAddDefinitionDialogFragment eddf = new ManualAddDefinitionDialogFragment(AddWordsFragment.this, word);
                    eddf.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "editDefinition");


                }
            }
        });

        etWordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                //TODO: Either remove or fully implement the 'did you mean' list feature
                didYouMeanList.clear();

                /*rlWord.animate().alpha(0.0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        rlWord.setVisibility(View.GONE);
                    }
                });

                addWordsResultsView.animate().alpha(0.0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        addWordsResultsView.setVisibility(View.GONE);
                    }
                });

                btnAdd.setVisibility(View.GONE);*/

                tvDidYouMean.animate().alpha(0.0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvDidYouMean.setVisibility(View.INVISIBLE);
                    }
                });


                lvDidYouMean.setVisibility(View.INVISIBLE);
                lvDidYouMean.setScaleY(0);
                ViewGroup.LayoutParams params = lvDidYouMean.getLayoutParams();
                params.height = 0;
                lvDidYouMean.setLayoutParams(params);


                if(s.toString().trim().length() != 0) {
                    btnManualAdd.setEnabled(true);
                    btnWebSearch.setEnabled(true);
                    updateWordView();

                } else {
                    btnManualAdd.setEnabled(false);
                    btnWebSearch.setEnabled(false);
                }

                didYouMeanAdapter.notifyDataSetChanged();
            }
        });

        etWordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    btnWebSearch.callOnClick();
                    return true;
                }

                return false;
            }
        });


        // Word passed in for searching
        Bundle argsBundle = getArguments();
        if(argsBundle != null) {
            String argWord = argsBundle.getString("word");
            if (argWord != null) {
                etWordInput.setText(argWord);
            }
        }
    }

    public void updateWordView() {
        final String word = etWordInput.getText().toString().toLowerCase();
        wordId = FabVocabSQLHelper.getInstance(getContext()).getWordId(word);

        if(wordId != -1) {
            btnWordExists.setVisibility(View.VISIBLE);
            etWordInput.setTextAppearance(R.style.DefinitionText);
            btnAdd.setText("Add Definitions");
        } else {
            btnWordExists.setVisibility(View.INVISIBLE);
            etWordInput.setTextAppearance(R.style.StandardText);
            btnAdd.setText("Add Word & Definitions");

            for(final WordEntry wordToCheck : allWords) {
                if(Helpers.isSimilar(wordToCheck.getWord(), word, 0.3)) {
                    tvDidYouMean.setVisibility(View.VISIBLE);
                    tvDidYouMean.animate().alpha(1.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            tvDidYouMean.setVisibility(View.VISIBLE);
                        }
                    });

                    tvDidYouMean.setText("Did you mean: " + wordToCheck.getWord() + "?");

                    didYouMeanList.add(wordToCheck.getWord());
                    didYouMeanList.add(wordToCheck.getWord());

                    tvDidYouMean.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etWordInput.setText(wordToCheck.getWord());
                        }
                    });
                }
            }


        }
    }

    @Override
    public void cleanUp() {
        addWordsResultsView.cleanUp();
    }

    public boolean isChecked(String definition) {
        return addWordsResultsView.getDefinitionsToAdd().contains(definition);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(requester != null) {
            requester.cancelAll();
        }
        addWordsResultsView.cleanUp();
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();

        // Refresh result Fragments
        addWordsResultsView.setDefinitionsThatAlreadyExist(FabVocabSQLHelper.getInstance(getContext()).getAllDefinitions(wordId));
        addWordsResultsView.refreshResultsFragments();

    }

    public AddWordsResultsView getResultView() {
        return addWordsResultsView;
    }

    public void giveAudioUrl(String audioUrl) {
        ppvAudio.giveSource(audioUrl);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("showingResults", showingResult);

        if(showingResult) {
            // Pass results to new AddWordsResultView
            outState.putSerializable("apiResults", addWordsResultsView.getResults());

            // Reselect any 'selected' options
            outState.putSerializable("selectedItems", addWordsResultsView.getDefinitionsToAdd());
        }
    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // UNUSED
    }*/
}