package uk.co.cdevelop.fabvocab.Support;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by chris on 22/04/17.
 */

public class SaveState implements Parcelable {

    private Parcelable superState;
    private Bundle bundle;

    public SaveState(Parcelable superState, Bundle bundle) {
        this.superState = superState;
        this.bundle = bundle;
    }

    public SaveState(Parcel in) {
    }

    public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
        @Override
        public SaveState createFromParcel(Parcel in) {
            return new SaveState(in);
        }

        @Override
        public SaveState[] newArray(int size) {
            return new SaveState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Parcelable getSuperState() {
        return superState;
    }

}
