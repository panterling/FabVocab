package uk.co.cdevelop.fabvocab.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import uk.co.cdevelop.fabvocab.DataModels.OxfordAPIWordDefinition;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 21/01/2017.
 */
public class AddWordsResultFragment extends Fragment implements IFragmentWithCleanUp{

    @Override
    public void cleanUp() {

    }

    private enum State {
        BLANK,
        PROGRESS,
        SHOWRESULT,
        NORESULT
    }

    private State state;

    private String APITitle = "undefined";
    private ArrayList<String> definitions;
    private String results;

    public AddWordsResultFragment() {
        super();
        this.APITitle = "undefined";
        this.definitions = new ArrayList<String>();
        this.state = State.BLANK;
    }
    public AddWordsResultFragment(String title) {
        this.APITitle = title;
        this.definitions = new ArrayList<String>();
        this.state = State.BLANK;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.definitionresult, container, false);

        //TODO: Move to XML
        ProgressBar prgBar = (ProgressBar) view.findViewById(R.id.prgSearch);
        prgBar.setVisibility(View.GONE);

        ScrollView svResults = (ScrollView) view.findViewById(R.id.sv_main);
        svResults.setVisibility(View.GONE);

        TextView msgTextView = (TextView) view.findViewById(R.id.tv_message);
        msgTextView.setVisibility(View.GONE);

        TextView noResultTextView = (TextView) view.findViewById(R.id.tv_noresultsfound);
        noResultTextView.setVisibility(View.GONE);


        switch(state) {
            case BLANK:
                msgTextView.setVisibility(View.VISIBLE);
                msgTextView.setText("Blank....idle.....");
                break;
            case PROGRESS:
                prgBar.setVisibility(View.VISIBLE);
                break;
            case NORESULT:
                noResultTextView.setVisibility(View.VISIBLE);
                break;
            case SHOWRESULT:
                svResults.setVisibility(View.VISIBLE);

                List<Fragment> fl = ((FragmentActivity) getContext()).getSupportFragmentManager().getFragments();
                IAddResultsViewOwner parentFragment = null;
                for (Fragment f : fl) {
                    if(f instanceof IAddResultsViewOwner) {
                        parentFragment = (IAddResultsViewOwner) f;
                        break;
                    }
                }

                final AddWordsResultsView parent = parentFragment.getResultView();



                LinearLayout resultsLayout = (LinearLayout) view.findViewById(R.id.resultslayout);
                LayoutParams definitionLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                definitionLayoutParams.setMargins(25, 0, 0, 0);

                for (final String definition : definitions) {
                    CheckBox newCheck = new CheckBox(this.getContext());

                    newCheck.setText(definition);
                    if(parent.definitionExists(definition)) {
                        newCheck.setEnabled(false);
                        newCheck.setChecked(true);
                    } else if(parent.isChecked(definition)) {
                        newCheck.setChecked(true);
                    }

                    newCheck.setLayoutParams(definitionLayoutParams);

                    newCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                parent.addDefinitionForSaving(definition);
                            } else {
                                parent.removeDefinitionForSaving(definition);
                            }

                        }
                    });

                    resultsLayout.addView(newCheck);
                }
                TextView temp = new TextView(getContext());
                temp.setText(results);
                resultsLayout.addView(temp);
                break;
        }

        return view;
    }

    public void setAPITitle(String title) {
        APITitle = title;
    }

    public String getAPITitle() {
        return APITitle;
    }

    public void setAsInProgress() {
        this.definitions.clear();
        this.state = State.PROGRESS;
    }

    public void setResults(ArrayList<String> results) {
        this.state = State.SHOWRESULT;
        this.definitions = results;
    }

    public void noResults() {
        this.state = State.NORESULT;
    }

}
