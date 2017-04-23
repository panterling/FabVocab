package uk.co.cdevelop.fabvocab.WebRequest;

import uk.co.cdevelop.fabvocab.WebRequest.APIs.MerriamWebsterAPIParser;
import uk.co.cdevelop.fabvocab.Support.Constants;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 12/02/2017.
 */

class MerriamWebsterAPIRequest extends CustomStringRequest {
    public MerriamWebsterAPIRequest( String word, AddWordsResultsView destination) {
        super(Constants.APIType.MW, Constants.URL_MERRIAMWEBSTER.replace(Constants.URL_WORDTOKEN, word), destination, new MerriamWebsterAPIParser());

        this.setTag("apiRequest");
    }
}
