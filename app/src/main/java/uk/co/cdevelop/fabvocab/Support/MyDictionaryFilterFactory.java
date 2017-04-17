package uk.co.cdevelop.fabvocab.Support;

import uk.co.cdevelop.fabvocab.Adapters.MyDictionaryListAdaptor;

/**
 * Created by Chris on 04/03/2017.
 */

public class MyDictionaryFilterFactory {
    public enum Type {
        RECENT,
        SHOWALL,
        MASTERED,
        UNPRACTICED
    }

    private static class MyDictionaryFilterShowAll extends MyDictionaryFilter {

        public MyDictionaryFilterShowAll() {
            super("Show All");
        }

        @Override
        public void apply(MyDictionaryListAdaptor adapter) {
            adapter.showAll();
        }
    }

    private static class MyDictionaryFilterRecent extends MyDictionaryFilter {

        public MyDictionaryFilterRecent() {
            super("Recently Added");
        }

        @Override
        public void apply(MyDictionaryListAdaptor adapter) {
            adapter.showRecentlyAdded();
        }
    }

    public static MyDictionaryFilter makeFilter(Type type) {
        MyDictionaryFilter newFilter = null;

        switch(type) {
            case SHOWALL:
                newFilter = new MyDictionaryFilterShowAll();
                break;
            case RECENT:
                newFilter = new MyDictionaryFilterRecent();
                break;
            case MASTERED:
                // TODO:
                break;
            case UNPRACTICED:
                // TODO:
                break;
        }

        return newFilter;
    }

    // Show Mastered
    // Show unpracticed

}
