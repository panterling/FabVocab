package uk.co.cdevelop.fabvocab.WebRequest;

import android.content.Context;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 13/02/2017.
 */

public class RequestWord {
    private RequestQueue queue;
    public RequestWord(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void requestAll(String word, AddWordsResultsView destination) throws UnsupportedEncodingException {
        word = URLEncoder.encode(word, "utf-8");

        // Oxford API
        queue.add(new OxfordAPIRequest(word, destination));

        // MW API
        queue.add(new MerriamWebsterAPIRequest(word, destination));

        // Collins API
        queue.add(new CollinsAPIRequest(word, destination));
    }

}
