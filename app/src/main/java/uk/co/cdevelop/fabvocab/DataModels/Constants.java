package uk.co.cdevelop.fabvocab.DataModels;

/**
 * Created by Chris on 22/01/2017.
 */

public class Constants {
    public enum APIType {
        OXFORD,
        MW,
        COLLINS;
    }

    public static final String APIKEY_MERRIAMWEBSTER = "3700b370-13b6-478d-b808-63fb1f258773";

    public static final String APIKEY_COLLINS = "s0viS7YXQlrMXDZADSV6MOLvjqzqbKwVOOunGfzBSmV7TI9IP5HSCSExsEusQav4";

    public static final String APIKEY_OXFORD = "f989171647da945771c6b2f95b8af6a0";
    public static final String APIID_OXFORD = "b8a2254b";

    public static final String URL_WORDTOKEN = "{*}";
    public static final String URL_MERRIAMWEBSTER = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + URL_WORDTOKEN + "?key=" + Constants.APIKEY_MERRIAMWEBSTER;
    public static final String URL_OXFORD = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" + URL_WORDTOKEN + "/definitions";
    public static final String URL_COLLINS = "https://api.collinsdictionary.com/api/v1/dictionaries/english/search/first?q=" + URL_WORDTOKEN + "&format=xml";

}
