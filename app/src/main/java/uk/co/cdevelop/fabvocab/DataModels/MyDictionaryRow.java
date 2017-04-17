package uk.co.cdevelop.fabvocab.DataModels;

/**
 * Created by Chris on 19/01/2017.
 */
public class MyDictionaryRow implements Comparable<MyDictionaryRow> {
    private String word;
    private int word_id;

    public MyDictionaryRow(int word_id, String word) {
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

    public int compareTo(MyDictionaryRow other) {
        return this.getWord().compareTo(other.getWord());
    }

}
