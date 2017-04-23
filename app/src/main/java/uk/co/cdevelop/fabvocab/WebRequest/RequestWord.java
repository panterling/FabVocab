package uk.co.cdevelop.fabvocab.WebRequest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.Subscription.RequestWordCompleteSubscriber;
import uk.co.cdevelop.fabvocab.Support.Constants;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 13/02/2017.
 */

public class RequestWord {

    public enum State {
        IDLE,
        RESULTS_PENDING,
        ALL_RECEIVED,
        CANCELLED
    }

    public State state = State.IDLE;

    private RequestQueue queue;
    private AddWordsResultsView destination;

    private HashMap<Integer, Boolean> receiveFlags;
    private ArrayList<RequestWordCompleteSubscriber> completeSubscribers = new ArrayList<>();

    public RequestWord(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void requestAll(String word, AddWordsResultsView destination) throws UnsupportedEncodingException {
        this.destination = destination;
        this.state = State.RESULTS_PENDING;

        word = URLEncoder.encode(word, "utf-8");

        // Oxford API
        queue.add(new OxfordAPIRequest(word, this));

        // MW API
        queue.add(new MerriamWebsterAPIRequest(word, this));

        // Collins API
        queue.add(new CollinsAPIRequest(word, this));

        receiveFlags = new HashMap<>();
        receiveFlags.put(Constants.APIType.OXFORD.ordinal(), false);
        receiveFlags.put(Constants.APIType.COLLINS.ordinal(), false);
        receiveFlags.put(Constants.APIType.MW.ordinal(), false);

    }

    public void giveResults(Constants.APIType owner, APIResultSet resultset) {
        destination.giveResults(owner, resultset);

        receiveFlags.put(owner.ordinal(), true);

        for(HashMap.Entry<Integer, Boolean> flag : receiveFlags.entrySet()) {
            if(flag.getValue() == false) {
                return;
            }
        }

        notifyAllReceived();
    }

    public void cancelAll() {
        state = State.CANCELLED;
        queue.cancelAll("apiRequest");
    }

    public void allCompleteSubscribe(RequestWordCompleteSubscriber subscriber) {
        completeSubscribers.add(subscriber);
    }
    public void notifyAllReceived() {
        for(RequestWordCompleteSubscriber subscriber : completeSubscribers) {
            subscriber.requestComplete();
        }
    }
}
