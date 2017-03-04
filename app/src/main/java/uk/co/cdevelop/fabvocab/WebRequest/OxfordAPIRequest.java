package uk.co.cdevelop.fabvocab.WebRequest;

import uk.co.cdevelop.fabvocab.WebRequest.APIs.OxfordAPIParser;
import uk.co.cdevelop.fabvocab.DataModels.Constants;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 12/02/2017.
 */

public class OxfordAPIRequest extends CustomStringRequest {
    public OxfordAPIRequest(String word, AddWordsResultsView destination) {
        super(Constants.APIType.OXFORD, Constants.URL_OXFORD.replace(Constants.URL_WORDTOKEN, word), destination, new OxfordAPIParser());

        String url = Constants.URL_OXFORD.replace(Constants.URL_WORDTOKEN, word);
        super.headers.put("Accept", "application/json");
        super.headers.put("app_id", Constants.APIID_OXFORD);
        super.headers.put("app_key", Constants.APIKEY_OXFORD);


    }
}
