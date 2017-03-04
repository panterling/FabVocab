package uk.co.cdevelop.fabvocab.Fragments;

/**
 * Created by Chris on 16/01/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.cdevelop.fabvocab.Activities.MainActivity;
import uk.co.cdevelop.fabvocab.Helpers;
import uk.co.cdevelop.fabvocab.WebRequest.CollinsAPIRequest;
import uk.co.cdevelop.fabvocab.Fragments.Dialog.ManualAddDefinitionDialogFragment;
import uk.co.cdevelop.fabvocab.SQL.FabVocabContract;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;
import uk.co.cdevelop.fabvocab.WebRequest.MerriamWebsterAPIRequest;
import uk.co.cdevelop.fabvocab.WebRequest.OxfordAPIRequest;
import uk.co.cdevelop.fabvocab.WebRequest.RequestWord;

public class AddWordsFragment extends Fragment implements IFragmentWithCleanUp, IAddResultsViewOwner {

    private AddWordsResultsView addWordsResultsView;
    private int wordId;

    ArrayList<String> allWords = new ArrayList<String>();
    ArrayList<String> didYouMeanList = new ArrayList<String>();

    Button btnWordExists;
    EditText etWordInput;
    Button btnAdd;
    TextView tvDidYouMean;
    ListView lvDidYouMean;


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

        final SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getWritableDatabase();
        allWords = FabVocabSQLHelper.getAllWords(db);

        final Button btnWebSearch = (Button) view.findViewById(R.id.btnSearch);
        final Button btnManualAdd = (Button) view.findViewById(R.id.btn_manualaddword);
        final TextView tvWord = (TextView) view.findViewById(R.id.tv_word);

        btnAdd = (Button) view.findViewById(R.id.btn_addword);
        btnWordExists = (Button) view.findViewById(R.id.iv_addwords_exists);
        etWordInput = (EditText) view.findViewById(R.id.editSearch);
        tvDidYouMean = (TextView) view.findViewById(R.id.tv_didyoumean);
        lvDidYouMean = (ListView) view.findViewById(R.id.lv_didyoumean);

        final ArrayAdapter<String> didYouMeanAdapter= new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, didYouMeanList);
        lvDidYouMean.setAdapter(didYouMeanAdapter);

        wordId = -1;

        addWordsResultsView = (AddWordsResultsView) view.findViewById(R.id.adwv_addwords_resultsview);
        addWordsResultsView.setParent(btnAdd);

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

                addWordsResultsView.setVisibility(View.VISIBLE);
                addWordsResultsView.animate().alpha(1.0f);

                addWordsResultsView.setDefinitionsThatAlreadyExist(FabVocabSQLHelper.getDefinitions(wordId, db));

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

                        RequestWord requester = new RequestWord(getContext());
                        requester.requestAll(word, addWordsResultsView);

                        // Prep UI
                        tvWord.setText(word);
                        tvWord.setVisibility(View.VISIBLE);
                        tvWord.animate().alpha(1.0f);

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

                String word = (String) tvWord.getText().toString().toLowerCase();
                String str = "";


                if(wordId == -1) {
                    wordId = FabVocabSQLHelper.addWord(word, db);
                    str = "Added New Word: " + word + "\n";
                    updateWordView();
                }

                for (String definition : addWordsResultsView.getDefinitionsToAdd()) {
                    ContentValues values = new ContentValues();
                    values.put(FabVocabContract.DefinitionEntry.COLUMN_WORD_ID, wordId);
                    values.put(FabVocabContract.DefinitionEntry.COLUMN_NAME_DEFINITION, definition);

                    db.insert(FabVocabContract.DefinitionEntry.TABLE_NAME, null, values);
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

                tvWord.animate().alpha(0.0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvWord.setVisibility(View.GONE);
                    }
                });

                addWordsResultsView.animate().alpha(0.0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        addWordsResultsView.setVisibility(View.GONE);
                    }
                });

                btnAdd.setVisibility(View.GONE);

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
        SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();
        final String word = etWordInput.getText().toString().toLowerCase();
        wordId = FabVocabSQLHelper.getWord(word, db);

        if(wordId != -1) {
            btnWordExists.setVisibility(View.VISIBLE);
            etWordInput.setTextAppearance(R.style.DefinitionText);
            btnAdd.setText("Add Definitions");
        } else {
            btnWordExists.setVisibility(View.INVISIBLE);
            etWordInput.setTextAppearance(R.style.StandardText);
            btnAdd.setText("Add Word & Definitions");

            for(final String wordToCheck : allWords) {
                if(Helpers.isSimilar(wordToCheck, word, 0.3)) {
                    tvDidYouMean.setVisibility(View.VISIBLE);
                    tvDidYouMean.animate().alpha(1.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            tvDidYouMean.setVisibility(View.VISIBLE);
                        }
                    });

                    tvDidYouMean.setText("Did you mean: " + word + "?");

                    didYouMeanList.add(word);
                    didYouMeanList.add(word);

                    tvDidYouMean.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etWordInput.setText(word);
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
        addWordsResultsView.detachChildren();
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();

        // Refresh result Fragments
        SQLiteDatabase db = FabVocabSQLHelper.getInstance(getContext()).getReadableDatabase();
        addWordsResultsView.setDefinitionsThatAlreadyExist(FabVocabSQLHelper.getDefinitions(wordId, db));
        addWordsResultsView.refreshResultsFragments();

    }

    public AddWordsResultsView getResultView() {
        return addWordsResultsView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //addWordsResultsView.cleanUp(); // Already removed by MAin Activity
    }
}