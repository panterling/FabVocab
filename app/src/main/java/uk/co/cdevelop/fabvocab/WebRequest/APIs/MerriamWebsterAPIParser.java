package uk.co.cdevelop.fabvocab.WebRequest.APIs;

import android.util.Log;
import android.util.Xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;

/**
 * Created by Chris on 30/01/2017.
 */

public class MerriamWebsterAPIParser extends APIParser {

    @Override
    public APIResultSet parse(String response) {

        ArrayList<WordDefinition> definitions = new ArrayList<>();
        String wavUrl = "";

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(response));


            String nextDefinition = "";
            String currentWordType = "undefined: ";

            boolean inDt = false;
            boolean inWav = false;
            boolean inWordType = false;

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();

                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if(tagname.equals("dt")) {
                            inDt = true;
                            nextDefinition = "";
                        } else if(inDt) {
                            nextDefinition += " ";
                        } else if (tagname.equals("wav")) {
                            inWav = true;
                        } else if (tagname.equals("fl")) {
                            inWordType = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if(inDt) {
                            nextDefinition += parser.getText();
                        } else if (inWav) {
                            wavUrl = parser.getText();
                        }
                        else if (inWordType) {
                            currentWordType = parser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagname.equals("dt")) {
                            inDt = false;

                            if(nextDefinition.charAt(0) == ':') {
                                nextDefinition = nextDefinition.substring(1);
                            }
                            nextDefinition = nextDefinition.replace("\n", "");
                            nextDefinition = nextDefinition.trim();
                            definitions.add(new WordDefinition(super.word, nextDefinition, currentWordType));
                        } else if(inDt) {
                            nextDefinition += " ";
                        } else if (tagname.equals("wav")) {
                            inWav = false;
                        } else if (tagname.equals("fl")) {
                            inWordType = false;
                        }
                        break;

                    default:
                        break;
                }


                eventType = parser.next();
            }

        } catch (Exception ex) {
            Log.e("MerriamWebsterAPI", "Exception in parse: " + ex.getMessage() + "\n" + ex.getStackTrace().toString());
        }

        if(wavUrl.length() > 0) {
            wavUrl = "http://media.merriam-webster.com/soundc11/" + wavUrl.charAt(0) + "/" + wavUrl;
        }

        //return new APIResultSet(word, definitions, wavUrl);

        return new APIResultSet(definitions, wavUrl);
    }


    // TODO: Examine this and understand the process - copy-pasted from android dev docs!
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

}
