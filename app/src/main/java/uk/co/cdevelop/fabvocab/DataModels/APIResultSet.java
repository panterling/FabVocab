package uk.co.cdevelop.fabvocab.DataModels;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chris on 05/03/2017.
 */

public class APIResultSet {

    private String audioUrl;
    private ArrayList<WordDefinition> definitions;

    public APIResultSet() {
        this.definitions = new ArrayList<>();
        this.audioUrl = "";
    }

    public APIResultSet(ArrayList<WordDefinition> definitions, String audioUrl) {
        this.definitions = definitions;
        this.audioUrl = audioUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public ArrayList<WordDefinition> getDefinitions() {
        return definitions;
    }
}
