package com.aptana.editor.css.parsing.ast;

public class CSSSelectorNode extends CSSNode {

    private CSSRuleNode fParent;
    private CSSSimpleSelectorNode[] fSimpleSelectors;

    public CSSSelectorNode(CSSRuleNode parent, CSSSimpleSelectorNode[] simpleSelectors, int start,
            int end) {
        fParent = parent;
        fSimpleSelectors = simpleSelectors;
        this.start = start;
        this.end = end;
    }

    public CSSRuleNode getParent() {
        return fParent;
    }

    public CSSSimpleSelectorNode[] getSimpleSelectors() {
        return fSimpleSelectors;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < fSimpleSelectors.length; ++i) {
            text.append(fSimpleSelectors[i]);
            if (i < fSimpleSelectors.length - 1) {
                text.append(" "); //$NON-NLS-1$
            }
        }
        return text.toString();
    }
}
