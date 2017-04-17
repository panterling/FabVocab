package uk.co.cdevelop.fabvocab.Support;

import uk.co.cdevelop.fabvocab.Adapters.MyDictionaryListAdaptor;

/**
 * Created by Chris on 04/03/2017.
 */

public abstract class MyDictionaryFilter {

    private String spinnerText;

    public MyDictionaryFilter(String text) {
        this.spinnerText = text;
    }

    @Override
    public String toString() {
        return this.spinnerText;
    }

    public abstract void apply(MyDictionaryListAdaptor adapter);
}