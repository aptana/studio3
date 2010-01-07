package com.aptana.editor.css.parsing.ast;

public class CSSTermNode extends CSSExpressionNode {

    private String fTerm;

    public CSSTermNode(String term) {
        fTerm = term;
    }

    @Override
    public String toString() {
        return fTerm;
    }
}
