package com.aptana.editor.css.parsing.ast;

public class CSSCharSetNode extends CSSNode {

    private String fEncoding;

    public CSSCharSetNode(String encoding) {
        fEncoding = encoding;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("@charset ").append(fEncoding).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
        return buf.toString();
    }
}
