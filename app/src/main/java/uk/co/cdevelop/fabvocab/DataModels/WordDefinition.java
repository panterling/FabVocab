package uk.co.cdevelop.fabvocab.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Chris on 05/03/2017.
 */

public class WordDefinition implements Serializable {
    String word = null;
    String wordType = null;
    String definition = null;

    public WordDefinition(String word, String definition, String wordType) {
        this.word = word;
        this.wordType = wordType;
        this.definition = definition;
    }

    public WordDefinition(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    protected WordDefinition(Parcel in) {
        word = in.readString();
        wordType = in.readString();
        definition = in.readString();
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return this.definition;
    }
}
