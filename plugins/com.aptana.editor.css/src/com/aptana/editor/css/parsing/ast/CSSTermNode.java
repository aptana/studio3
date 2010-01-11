package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSTermNode extends CSSExpressionNode {

    private String fTerm;

    public CSSTermNode(Symbol term) {
        fTerm = term.value.toString();
        this.start = term.getStart();
        this.end = term.getEnd();
    }

    @Override
    public String toString() {
        return fTerm;
    }
}
