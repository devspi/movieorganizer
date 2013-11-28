package spi.movieorganizer.data.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class StringMultiLang {

    private final Map<Locale, String> languageMap;

    public StringMultiLang() {
        this.languageMap = new HashMap<>();
    }

    public StringMultiLang(final Locale locale, final String value) {
        this();
        addValue(locale, value);
    }

    public void addValue(final Locale locale, final String value) {
        this.languageMap.put(locale, value);
    }

    public String getValue(final Locale locale) {
        return this.languageMap.get(locale);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<Locale, String> languageEntry : this.languageMap.entrySet())
            sb.append("[" + languageEntry.getKey().getLanguage() + ":" + languageEntry.getValue() + "]");
        return sb.toString();
    }
}
