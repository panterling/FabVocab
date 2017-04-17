package uk.co.cdevelop.fabvocab.WebRequest.APIs;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.DataModels.APIResultSet;

/**
 * Created by Chris on 22/01/2017.
 */
public abstract class APIParser {

    protected String word = "undefined";

    public abstract APIResultSet parse(String response);
}
