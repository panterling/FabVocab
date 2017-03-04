package uk.co.cdevelop.fabvocab.DataModels;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Chris on 19/01/2017.
 */
public class MyDictionaryDefinition  implements Comparable<MyDictionaryDefinition> {
    String word;
    int word_id;
    ArrayList<String> definitions; // TODO: preload or load-on-click?

    public MyDictionaryDefinition(int word_id, String word) {
        this.word_id = word_id;
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWordId() {
        return word_id;
    }

    public void getWordId(int word_id) {
        this.word_id = word_id;
    }

    public int compareTo(MyDictionaryDefinition other) {
        return this.getWord().compareTo(other.getWord());
    }

}
