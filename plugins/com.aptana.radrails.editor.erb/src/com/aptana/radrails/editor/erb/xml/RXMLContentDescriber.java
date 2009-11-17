package com.aptana.radrails.editor.erb.xml;

import com.aptana.radrails.editor.erb.common.ERBContentDescriber;

public class RXMLContentDescriber extends ERBContentDescriber {

    private static final String XML_PREFIX = "<?xml "; //$NON-NLS-1$

    @Override
    protected String getPrefix() {
        return XML_PREFIX;
    }
}
