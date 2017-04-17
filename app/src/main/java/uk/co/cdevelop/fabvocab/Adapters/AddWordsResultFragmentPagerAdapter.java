package uk.co.cdevelop.fabvocab.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;
import uk.co.cdevelop.fabvocab.Support.Constants.APIType;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsResultFragment;

/**
 * Created by Chris on 21/01/2017.
 */

public class AddWordsResultFragmentPagerAdapter extends FragmentPagerAdapter {

    private AddWordsResultFragment[] fragments;
    private Context context;

    public AddWordsResultFragmentPagerAdapter(Context context, AddWordsResultFragment[] fragments) {
        super(((FragmentActivity) context).getSupportFragmentManager());
        this.context = context;

        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }


    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getAPITitle();
    }

    public void giveResult(APIType owner, ArrayList<WordDefinition> responseDefinitions) {

        if(responseDefinitions.size() > 0) {
            fragments[owner.ordinal()].setResults(responseDefinitions);
        } else {
            fragments[owner.ordinal()].noResults();
        }

        this.notifyDataSetChanged();
    }

    //TODO: Find an alternate way to handle this - forcing view creation for ALL fragments each time notifyDataChange is called is inefficient!
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void allInProgress() {
        for(AddWordsResultFragment f : fragments) {
            f.setAsInProgress();
        }
        this.notifyDataSetChanged();
    }

    public void removeChildren() {
        for (AddWordsResultFragment f : fragments) {
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().remove(f).commit();
        }
    }
}
