package uk.co.cdevelop.fabvocab.DataModels;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Chris on 15/01/2017.
 */

public class OxfordAPIWordDefinition {

    private String wordId;

    public static class WordDefinitionResultCategory{
        private ArrayList<String> definitions;
        private String categoryId;
        public WordDefinitionResultCategory(String categoryId) {
            this.definitions = new ArrayList<String>();
            this.categoryId = categoryId;
        }


        public void addDefinition(String definition_row) {
            definitions.add(definition_row);
        }

        public String getCategoryId() {
            return categoryId;
        }

        public ArrayList<String> getDefinitions() {
            return definitions;
        }
    };

    public static class WordDefinitionResult {
        private ArrayList<WordDefinitionResultCategory> categories;

        public WordDefinitionResult(){
            categories = new ArrayList<WordDefinitionResultCategory>();
        }

        public void addCategory(WordDefinitionResultCategory category) {
            categories.add(category);
        }

        public ArrayList<WordDefinitionResultCategory> getCategories() {
            return categories;
        }
    };




    private ArrayList<WordDefinitionResult> results;

    public OxfordAPIWordDefinition(){
        results = new ArrayList<WordDefinitionResult>();
    }

    public void addResult(WordDefinitionResult wdr) {
        results.add(wdr);
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWordId() {
        return wordId;
    }

    public ArrayList<WordDefinitionResult> getWordDefinitionResults() {
        return results;
    }
}
