package com.aptana.editor.css.parsing.ast;

public class CSSDeclarationNode extends CSSNode {

    private String fIdentifier;
    private CSSExpressionNode fValue;
    private String fStatus;
    private boolean fHasSemicolon;

    public CSSDeclarationNode(boolean hasSemicolon) {
        fHasSemicolon = hasSemicolon;
    }

    public CSSDeclarationNode(String identifier, CSSExpressionNode value) {
        this(identifier, value, null);
    }

    public CSSDeclarationNode(String identifier, CSSExpressionNode value, String status) {
        fIdentifier = identifier;
        fValue = value;
        fStatus = status;
    }

    public void setHasSemicolon(boolean has) {
        fHasSemicolon = has;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(fIdentifier);
        text.append(": ").append(fValue); //$NON-NLS-1$
        if (fStatus != null) {
            text.append(" ").append(fStatus); //$NON-NLS-1$
        }
        if (fHasSemicolon) {
            text.append(";"); //$NON-NLS-1$
        }
        return text.toString();
    }
}
