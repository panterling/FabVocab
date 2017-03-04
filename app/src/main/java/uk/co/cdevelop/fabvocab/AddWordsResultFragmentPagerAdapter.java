package uk.co.cdevelop.fabvocab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import uk.co.cdevelop.fabvocab.DataModels.Constants.APIType;
import uk.co.cdevelop.fabvocab.Fragments.AddWordsResultFragment;

/**
 * Created by Chris on 21/01/2017.
 */

public class AddWordsResultFragmentPagerAdapter extends FragmentPagerAdapter {

    private AddWordsResultFragment[] fragments;
    Context context;

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

    public void giveResult(APIType owner, ArrayList<String> responseDefinitions) {

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
        for(int i = 0; i < fragments.length; i++) {
            //AddWordsResultFragment tmp = (AddWordsResultFragment) fm.findFragmentByTag("");
            fragments[i].setAsInProgress();
        }
        this.notifyDataSetChanged();
    }

    public void removeChildren() {
        for (int i = 0; i < fragments.length; i++) {
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().remove(fragments[i]).commit();
        }
    }
}
