package com.aptana.editor.css.parsing.ast;

import java.util.List;

import beaver.Symbol;

public class CSSRuleNode extends CSSNode {

    private List<CSSSimpleSelectorNode>[] fSimpleSelectors;
    private CSSDeclarationNode[] fDeclarations;

    public CSSRuleNode(Symbol[] selectors) {
        this(selectors, null);
    }

    @SuppressWarnings("unchecked")
    public CSSRuleNode(Symbol[] selectors, Object declarations) {
        fSimpleSelectors = new List[selectors.length];
        for (int i = 0; i < selectors.length; ++i) {
            fSimpleSelectors[i] = (List<CSSSimpleSelectorNode>) selectors[i].value;
        }
        if (declarations instanceof CSSDeclarationNode) {
            fDeclarations = new CSSDeclarationNode[1];
            fDeclarations[0] = (CSSDeclarationNode) declarations;
        } else if (declarations instanceof List<?>) {
            List<CSSDeclarationNode> list = (List<CSSDeclarationNode>) declarations;
            int size = list.size();
            fDeclarations = new CSSDeclarationNode[size];
            for (int i = 0; i < size; ++i) {
                fDeclarations[i] = list.get(i);
            }
        } else {
            fDeclarations = new CSSDeclarationNode[0];
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        List<CSSSimpleSelectorNode> selectors;
        int size;
        for (int i = 0; i < fSimpleSelectors.length; ++i) {
            selectors = fSimpleSelectors[i];
            size = selectors.size();
            for (int j = 0; j < size; ++j) {
                text.append(selectors.get(j));
                if (j < size - 1) {
                    text.append(" "); //$NON-NLS-1$
                }
            }
            if (i < fSimpleSelectors.length - 1) {
                text.append(", "); //$NON-NLS-1$
            }
        }
        text.append(" {"); //$NON-NLS-1$
        for (int i = 0; i < fDeclarations.length; ++i) {
            text.append(fDeclarations[i]);
            if (i < fDeclarations.length - 1) {
                text.append(" "); //$NON-NLS-1$
            }
        }
        text.append("}"); //$NON-NLS-1$
        return text.toString();
    }
}
