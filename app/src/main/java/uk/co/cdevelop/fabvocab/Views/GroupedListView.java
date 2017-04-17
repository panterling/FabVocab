package uk.co.cdevelop.fabvocab.Views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.co.cdevelop.fabvocab.Adapters.AddWordsResultListAdapter;
import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;
import uk.co.cdevelop.fabvocab.R;

/**
 * Created by Chris on 10/03/2017.
 */

public class GroupedListView extends LinearLayout {

    private class GroupedSection {
        public String label;
        public int sectionStartPos;
        public int sectionEndPos;
        public TextView view;

        public GroupedSection(String label, int ss, int se, TextView view) {
            this.label = label;
            this.sectionStartPos = ss;
            this.sectionEndPos = se;
            this.view = view;

            this.view.setText(label);
        }
    }

    private ArrayList<GroupedSection> sections;
    private RelativeLayout rlGroups;
    private ListView lvItems;
    private ArrayAdapter adapter;
    private boolean hasDataBeenGiven;

    public GroupedListView(Context context) {
        super(context);
        init();
    }

    public GroupedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GroupedListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.groupedlistview, this);

        rlGroups = (RelativeLayout) findViewById(R.id.rl_groups);
        lvItems = (ListView) findViewById(R.id.lv_items);
        hasDataBeenGiven = false;

    }


    private TextView makeGroupedSectionTextView(RelativeLayout rl) {
        TextView tv = new TextView(getContext());

        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        rl.addView(tv);

        return tv;
    }

    public void giveItems(ArrayList<WordDefinition> items, AddWordsResultsView parent){

        if(items.size() == 0) {
            Log.e("GroupedListView", "Empty list of items passed to GroupedListView - State remains 'waiting for data'");
            return;
        }

        hasDataBeenGiven = true;

        // Clear up any pre-existing data
        // Remove Groups from layout
        rlGroups.removeAllViews();
        if(sections != null) {
            sections.clear();
        }
        if(adapter != null) {
            adapter.clear();
        }


        // Generate Groups and Labels
        Collections.sort(items, new Comparator<WordDefinition>() {

            @Override
            public int compare(WordDefinition a, WordDefinition b) {
                return a.getWordType().compareTo(b.getWordType());
            }
        });


        sections = new ArrayList<>();
        String currentType = items.get(0).getWordType();
        int ss = 0;
        int se = 0;
        for (int i = 1; i < items.size(); i++) {
            String type = items.get(i).getWordType();
            if(type.equals(currentType)) {
                se++;
            } else {
                sections.add(new GroupedSection(currentType, ss, se, makeGroupedSectionTextView(rlGroups)));
                currentType = type;
                ss = i;
                se = i;
            }
        }
        sections.add(new GroupedSection(currentType, ss, se, makeGroupedSectionTextView(rlGroups)));

        // Attach items to ListView
        adapter = new AddWordsResultListAdapter(getContext(), items, parent);
        lvItems.setAdapter(adapter);


        // Create Scrolling UI
        lvItems.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {



                // Update and display Group Labels
                int topPos = lvItems.getFirstVisiblePosition();
                int bottomPos = lvItems.getLastVisiblePosition();

                for(GroupedSection s : sections) {
                    s.view.setVisibility(View.VISIBLE);
                    if(s.sectionEndPos > topPos/* && s.sectionEndPos <= bottomPos*/ && s.sectionStartPos <= topPos) {
                        try {
                            s.view.setTop(0);
                            s.view.setBottom(lvItems.getChildAt(0).getHeight());
                            Log.i("grouped", "Pos @ " + Integer.toString(lvItems.getChildAt(s.sectionEndPos - topPos).getTop()));
                        } catch (Exception e){}

                    } else if (s.sectionEndPos == topPos) {
                        s.view.setTop(lvItems.getChildAt(s.sectionEndPos - topPos).getTop());
                        s.view.setBottom(lvItems.getChildAt(s.sectionEndPos - topPos).getBottom());
                    } else if(s.sectionStartPos > topPos && s.sectionStartPos <= bottomPos) {
                        s.view.setTop(lvItems.getChildAt(s.sectionStartPos - topPos).getTop());
                        s.view.setBottom(lvItems.getChildAt(s.sectionStartPos - topPos).getBottom());
                    } else {
                        s.view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


    }

}
