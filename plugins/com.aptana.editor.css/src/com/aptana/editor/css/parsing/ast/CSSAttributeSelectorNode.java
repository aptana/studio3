package com.aptana.editor.css.parsing.ast;

public class CSSAttributeSelectorNode extends CSSNode {

    private String fAttributeText;
    private CSSExpressionNode fFuncExpr;

    public CSSAttributeSelectorNode(String text) {
        fAttributeText = text;
    }

    public CSSAttributeSelectorNode(CSSExpressionNode function) {
        fFuncExpr = function;
    }

    @Override
    public String toString() {
        if (fAttributeText == null) {
            return fFuncExpr.toString();
        }
        return fAttributeText;
    }
}
