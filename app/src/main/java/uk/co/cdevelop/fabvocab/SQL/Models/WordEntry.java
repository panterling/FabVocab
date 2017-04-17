package uk.co.cdevelop.fabvocab.SQL.Models;

/**
 * Created by Chris on 12/03/2017.
 */

public class WordEntry {

    private int id;
    private String word;
    private long added;
    private String audioUrl;


    public WordEntry(int id, String word, long added, String audioUrl) {
        this.id = id;
        this.word = word;
        this.added = added;
        this.audioUrl = audioUrl;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public long getAdded() {
        return added;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
}
