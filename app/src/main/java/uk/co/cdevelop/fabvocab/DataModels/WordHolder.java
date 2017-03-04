package uk.co.cdevelop.fabvocab.DataModels;

/**
 * Created by Chris on 13/02/2017.
 */

public class WordHolder {
    private String word;
    private int wordId;

    public WordHolder(String word, int wordId) {
        this.word = word;
        this.wordId = wordId;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public int getWordId() {
        return wordId;
    }
}
