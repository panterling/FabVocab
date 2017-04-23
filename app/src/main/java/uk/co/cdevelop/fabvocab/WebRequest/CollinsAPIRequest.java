package uk.co.cdevelop.fabvocab.WebRequest;

import uk.co.cdevelop.fabvocab.WebRequest.APIs.CollinsAPIParser;
import uk.co.cdevelop.fabvocab.Support.Constants;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 12/02/2017.
 */

public class CollinsAPIRequest extends CustomStringRequest {
    public CollinsAPIRequest(String word, RequestWord destination) {
        super(Constants.APIType.COLLINS, Constants.URL_COLLINS.replace(Constants.URL_WORDTOKEN, word), destination, new CollinsAPIParser());

        this.setTag("apiRequest");

        super.headers.put("Accept", "application/xml");
        super.headers.put("accessKey", Constants.APIKEY_COLLINS);
    }
}
