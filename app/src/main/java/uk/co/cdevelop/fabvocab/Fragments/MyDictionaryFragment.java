package uk.co.cdevelop.fabvocab.Fragments;

/**
 * Created by Chris on 16/01/2017.
 */


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.cdevelop.fabvocab.Activities.MainActivity;
import uk.co.cdevelop.fabvocab.DataModels.MyDictionaryDefinition;
import uk.co.cdevelop.fabvocab.ListAdapters.MyDictionaryListAdaptor;
import uk.co.cdevelop.fabvocab.R;

public class MyDictionaryFragment extends Fragment implements IFragmentWithCleanUp {

    TextView currentlySelected;

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        return inflator.inflate(R.layout.mydictionary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Set Title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Dictionary");

        ((MainActivity) getActivity()).showFloatingAddButton();


        final ListView lvDictionary = (ListView) view.findViewById(R.id.lv_dictionary);

        final LinearLayout alphabetLayout = (LinearLayout) view.findViewById(R.id.alphabetLayout);
        final TextView tvCurrentFilter = (TextView) view.findViewById(R.id.tv_current_filter);
        final TextView tvScollingLetterOverlay = (TextView) view.findViewById(R.id.tv_scrolling_letter_overlay);

        final Spinner spFilters = (Spinner) view.findViewById(R.id.sp_mydictionary_filters);


        final MyDictionaryListAdaptor listAdapter = new MyDictionaryListAdaptor(getContext(), new ArrayList<MyDictionaryDefinition>());
        lvDictionary.setAdapter(listAdapter);

        final ArrayAdapter<String> filtersAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList("Show All", "New", "Etc")));
        spFilters.setAdapter(filtersAdapter);

        spFilters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: do not switch on strings!!!!!!! Bad chris!
                String selected = filtersAdapter.getItem(position);
                switch(selected) {
                    case "Show All":
                        listAdapter.showAll();
                        break;
                    case "New":
                        listAdapter.showRecentlyAdded();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lvDictionary.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount > 0 && visibleItemCount > 0) {
                    MyDictionaryDefinition def = (MyDictionaryDefinition) listAdapter.getItem(firstVisibleItem);
                    tvCurrentFilter.setText(Character.toString(def.getWord().charAt(0)));
                }
            }
        });


        alphabetLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentlySelected.setBackgroundColor(0);
                    tvScollingLetterOverlay.setVisibility(View.GONE);
                } else {
                    Log.i("Alphabet", MotionEvent.actionToString(event.getAction()));
                    for (int i = 0; i < alphabetLayout.getChildCount(); i++) {
                        Rect r = new Rect();
                        alphabetLayout.getChildAt(i).getGlobalVisibleRect(r);

                        //TODO: change these constants to be display metrics
                        if (r.intersect(0, (int) event.getRawY(), 10000, (int) event.getRawY())/*.contains((int) event.getRawX(), (int) event.getRawY())*/) {
                            currentlySelected.setBackgroundColor(0);

                            currentlySelected = (TextView) alphabetLayout.getChildAt(i);
                            currentlySelected.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                            tvScollingLetterOverlay.setVisibility(View.VISIBLE);
                            tvScollingLetterOverlay.setText(Character.toString(currentlySelected.getText().charAt(0)));

                            int topOffset = 200; //px
                            int offset = 0;
                            if(currentlySelected.getText().charAt(0) != '#') {
                                int vv = (int) currentlySelected.getText().charAt(0);
                                vv -= 65; // offset down to ASCII 0
                                vv *= 50; // * item height

                                offset += 50 + vv;
                            }

                            offset -= tvScollingLetterOverlay.getHeight() / 2; // offset to center

                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvScollingLetterOverlay.getLayoutParams();
                            params.setMargins(params.leftMargin, topOffset + offset, params.rightMargin, params.bottomMargin);
                            tvScollingLetterOverlay.setLayoutParams(params);


                            int newPosition = listAdapter.getPositionForChar(currentlySelected.getText().charAt(0));
                            if(newPosition >= 0) {
                                lvDictionary.smoothScrollToPositionFromTop(newPosition, 0);
                            }
                        }
                    }
                }
                return true;
            }
        });


        TextView nextLetter;

        nextLetter = new TextView(getContext());
        nextLetter.setText("#");
        nextLetter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        nextLetter.setHeight(50);
        nextLetter.setGravity(Gravity.CENTER);

        alphabetLayout.addView(nextLetter);

        for (int i = 0; i < 26; i++) {
            nextLetter = new TextView(getContext());
            nextLetter.setText(Character.toString((char) (i + 65)));
            nextLetter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            nextLetter.setHeight(50);
            nextLetter.setGravity(Gravity.CENTER);

            alphabetLayout.addView(nextLetter);
        }

        // Set 'A' as selected
        currentlySelected = (TextView) alphabetLayout.getChildAt(0);


    }

    @Override
    public void cleanUp() {

    }
}
