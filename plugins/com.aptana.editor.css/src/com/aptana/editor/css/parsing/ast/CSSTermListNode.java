package com.aptana.editor.css.parsing.ast;

public class CSSTermListNode extends CSSExpressionNode {

    private CSSExpressionNode fLeftExpr;
    private CSSExpressionNode fRightExpr;
    private String fSeparator;

    public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right) {
        this(left, right, null);
    }

    public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right, String separator) {
        fLeftExpr = left;
        fRightExpr = right;
        fSeparator = separator;
        this.start = left.getStart();
        this.end = right.getEnd();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(fLeftExpr);
        if (fSeparator == null) {
            text.append(" "); //$NON-NLS-1$
        } else {
            text.append(fSeparator);
        }
        text.append(fRightExpr);
        return text.toString();
    }
}
