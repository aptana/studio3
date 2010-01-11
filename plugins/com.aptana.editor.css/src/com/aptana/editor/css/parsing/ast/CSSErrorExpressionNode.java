package com.aptana.editor.css.parsing.ast;

public class CSSErrorExpressionNode extends CSSExpressionNode {

    public CSSErrorExpressionNode(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public String toString() {
        return ""; //$NON-NLS-1$
    }
}
