package uk.co.cdevelop.fabvocab.WebRequest.APIs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chris on 22/01/2017.
 */

public class OxfordAPIParser implements IAPIParser {
    @Override
    public ArrayList<String> parse(String response) {


        ArrayList<String> resultDefinitions = new ArrayList<String>();

        try {
            JSONObject resObj = new JSONObject(response);

            JSONArray jo_result = resObj.getJSONArray("results");

            //OxfordAPIWordDefinition wd = new OxfordAPIWordDefinition();

            for (int i = 0; i < jo_result.length(); i++) {
                JSONObject result = jo_result.getJSONObject(i);

               // wd.setWordId(result.getString("id"));

                //OxfordAPIWordDefinition.WordDefinitionResult wdr = new OxfordAPIWordDefinition.WordDefinitionResult();

                JSONArray ja_lexical = result.getJSONArray("lexicalEntries");
                for (int j = 0; j < ja_lexical.length(); j++) {
                    JSONObject lexicalRow = ja_lexical.getJSONObject(j);
                    //OxfordAPIWordDefinition.WordDefinitionResultCategory nextCategory = new OxfordAPIWordDefinition.WordDefinitionResultCategory(lexicalRow.getString("lexicalCategory"));

                    JSONArray entries = lexicalRow.getJSONArray("entries");
                    for (int k = 0; k < entries.length(); k++) {
                        JSONObject entry = entries.getJSONObject(k);

                        JSONArray senses = entry.getJSONArray("senses");
                        for (int m = 0; m < senses.length(); m++) {
                            JSONObject senseObj = senses.getJSONObject(m);

                            JSONArray definitions = senseObj.getJSONArray("definitions");
                            for (int n = 0; n < definitions.length(); n++) {
                                String definition_row = definitions.getString(n);

                                resultDefinitions.add(definition_row);
                            }
                        }
                    }

                    //wdr.addCategory(nextCategory);
                }
                //wd.addResult(wdr);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultDefinitions.add("ERROR IN PARSER!");
            //wd = null;
        }

        return resultDefinitions;
    }
}
