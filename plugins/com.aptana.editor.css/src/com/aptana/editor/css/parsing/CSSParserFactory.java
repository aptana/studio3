package com.aptana.editor.css.parsing;

import com.aptana.parsing.IParser;

public class CSSParserFactory {

    private static CSSParserFactory fInstance;

    private IParser fParser;

    public static CSSParserFactory getInstance() {
        if (fInstance == null) {
            fInstance = new CSSParserFactory();
        }

        return fInstance;
    }

    public IParser getParser() {
        return fParser;
    }

    private CSSParserFactory() {
        fParser = new CSSParser();
    }
}
