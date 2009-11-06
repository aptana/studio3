package com.aptana.radrails.editor.ruby;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jruby.parser.Tokens;

import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class RubyCodeScanner implements ITokenScanner {

    private ITokenScanner fScanner;

    public RubyCodeScanner() {
        fScanner = new RubyTokenScanner();
    }

    public int getTokenLength() {
        return fScanner.getTokenLength();
    }

    public int getTokenOffset() {
        return fScanner.getTokenOffset();
    }

    public IToken nextToken() {
        IToken intToken = fScanner.nextToken();
        if (intToken == null || intToken.isEOF()) {
            return Token.EOF;
        }
        Integer data = (Integer) intToken.getData();
        if (data == null) {
            return Token.EOF;
        }

        // Convert the integer tokens into tokens containing color information!
        if (isKeyword(data.intValue())) {
            return ThemeUtil.getToken("keyword.control.ruby"); //$NON-NLS-1$
        }
        switch (data.intValue()) {
        case RubyTokenScanner.CHARACTER:
            return ThemeUtil.getToken("character.ruby"); //$NON-NLS-1$
        case Tokens.tFLOAT:
        case Tokens.tINTEGER:
            return ThemeUtil.getToken("constant.numeric.ruby"); //$NON-NLS-1$
        case Tokens.tSYMBEG:
            return ThemeUtil.getToken("symbol.ruby"); //$NON-NLS-1$
        case Tokens.tGVAR:
            return ThemeUtil.getToken("global.ruby"); //$NON-NLS-1$
        case Tokens.tIVAR:
            return ThemeUtil.getToken("variable.other.readwrite.instance.ruby"); //$NON-NLS-1$
        case Tokens.tCVAR:
            return ThemeUtil.getToken("keyword.control.class.ruby"); //$NON-NLS-1$
        case Tokens.yyErrorCode:
            return ThemeUtil.getToken("error.ruby"); //$NON-NLS-1$
        default:
            return ThemeUtil.getToken("default.ruby"); //$NON-NLS-1$
        }
    }

    public void setRange(IDocument document, int offset, int length) {
        fScanner.setRange(document, offset, length);
    }

    private boolean isKeyword(int i) {
        return (i >= RubyTokenScanner.MIN_KEYWORD && i <= RubyTokenScanner.MAX_KEYWORD);
    }
}
