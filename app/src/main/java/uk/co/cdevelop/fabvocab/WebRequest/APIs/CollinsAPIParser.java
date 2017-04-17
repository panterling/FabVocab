package uk.co.cdevelop.fabvocab.WebRequest.APIs;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import android.text.Html;

import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;

/**
 * Created by Chris on 30/01/2017.
 */

public class CollinsAPIParser extends APIParser {

    @Override
    public APIResultSet parse(String response) {
        ArrayList<WordDefinition> definitions = new ArrayList<>();

        try {
            // Decode html special characters
            response = Html.fromHtml(response).toString();

            // Strip any text before the first xml tag
            response = response.substring(response.indexOf('<'));

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(response));

            String nextDefintion = "";
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if(tagName.equals("def")) {
                            nextDefintion = "";
                        }
                        break;
                    case XmlPullParser.TEXT:
                        nextDefintion = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("def")) {
                            definitions.add(new WordDefinition(super.word, nextDefintion, ""));
                        }
                        break;
                }

                eventType = parser.next();
            }

        } catch (Exception ex) {
            Log.e("CollinsAPI", "Exception in parse: " + ex.getMessage() + "\n" + ex.getStackTrace().toString());
        }





        return new APIResultSet(definitions, "");
    }
}
