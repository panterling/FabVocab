package uk.co.cdevelop.fabvocab.WebRequest.APIs;

import android.util.Log;
import android.util.Xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by Chris on 30/01/2017.
 */

public class MerriamWebsterAPIParser implements IAPIParser {

    @Override
    public ArrayList<String> parse(String response) {

        ArrayList<String> definitions = new ArrayList<String>();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(response));


            String nextDefinition = "";
            boolean inDt = false;

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
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if(inDt) {
                            nextDefinition += parser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagname.equals("dt")) {
                            inDt = false;

                            if(nextDefinition.charAt(0) == ':') {
                                nextDefinition = nextDefinition.substring(1);
                            }
                            nextDefinition = nextDefinition.replace("\n", "");
                            definitions.add(nextDefinition);
                        } else if(inDt) {
                            nextDefinition += " ";
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

        return definitions;
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
