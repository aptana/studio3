package com.aptana.radrails.editor.erb.html;

import com.aptana.radrails.editor.erb.common.ERBContentDescriber;

public class RHTMLContentDescriber extends ERBContentDescriber {

    private static final String HTML_PREFIX = "<!DOCTYPE HTML"; //$NON-NLS-1$

    @Override
    protected String getPrefix() {
        return HTML_PREFIX;
    }
}
