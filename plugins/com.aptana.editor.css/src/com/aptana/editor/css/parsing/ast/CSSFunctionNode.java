package com.aptana.editor.css.parsing.ast;

public class CSSFunctionNode extends CSSExpressionNode {

    private CSSExpressionNode fExpression;

    public CSSFunctionNode(CSSExpressionNode expression, int start, int end) {
        fExpression = expression;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("(").append(fExpression).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
        return text.toString();
    }
}
