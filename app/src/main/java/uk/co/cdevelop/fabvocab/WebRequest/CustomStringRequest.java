package uk.co.cdevelop.fabvocab.WebRequest;

/**
 * Created by Chris on 14/01/2017.
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;
import uk.co.cdevelop.fabvocab.WebRequest.APIs.APIParser;

import uk.co.cdevelop.fabvocab.Support.Constants.APIType;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

class CustomStringRequest extends StringRequest {


    HashMap<String, String> headers;

    CustomStringRequest(final APIType owner, String url, final RequestWord destination, final APIParser parser) {
        super(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(destination != null) {
                            destination.giveResults(owner, parser.parse(response));
                        } else {
                            Log.e("CustomStringRequest", "The destination object for this CustomStringRequest is null.");
                        }
                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ArrayList<String> errResponse = new ArrayList<>();
                        Log.e("DictionaryAPI", "ERROR IN RESPONSE");
                        Log.e("DictionaryAPI", "Network Code: " + (error.networkResponse != null ? error.networkResponse.statusCode : "Null Network Response"));

                        if(destination != null) {
                            destination.giveResults(owner, new APIResultSet());
                        } else {
                            Log.e("CustomStringRequest", "The destination object for this CustomStringRequest is null.");
                        }
                    }
                }
        );

        this.headers = new HashMap<>();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        headers.putAll(super.getHeaders());

        return headers;
    }
}