package com.aptana.editor.css.parsing;

import java.io.IOException;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.css.parsing.lexer.CSSTokens;

import beaver.Scanner;
import beaver.Symbol;

public class CSSScanner extends Scanner {

    private CSSTokenScanner fTokenScanner;

    public CSSScanner() {
        fTokenScanner = new CSSTokenScanner();
    }

    public void setSource(String text) {
        fTokenScanner.setRange(new Document(text), 0, text.length());
    }

    @Override
    public Symbol nextToken() throws IOException, Exception {
        IToken token = fTokenScanner.nextToken();
        Object data = token.getData();
        while (token.isWhitespace()
                || (data != null && data.equals(CSSTokens.getTokenName(CSSTokens.COMMENT)))) {
            // ignores whitespace and comments
            token = fTokenScanner.nextToken();
            data = token.getData();
        }

        int offset = fTokenScanner.getTokenOffset();
        int length = fTokenScanner.getTokenLength();

        short type = CSSTokens.EOF;
        if (data != null) {
            type = CSSTokens.getToken(data.toString());
        }
        return new Symbol(type, offset, offset + length - 1, data);
    }
}
