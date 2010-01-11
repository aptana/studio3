package com.aptana.editor.css.parsing.ast;

import java.util.List;

public class CSSPageNode extends CSSNode {

    private String fPageSelector;
    private CSSDeclarationNode[] fDeclarations;

    public CSSPageNode(int start, int end) {
        this(null, null, start, end);
    }

    public CSSPageNode(String pageSelector, int start, int end) {
        this(pageSelector, null, start, end);
    }

    public CSSPageNode(Object declarations, int start, int end) {
        this(null, declarations, start, end);
    }

    @SuppressWarnings("unchecked")
    public CSSPageNode(String pageSelector, Object declarations, int start, int end) {
        fPageSelector = pageSelector;
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
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("@page "); //$NON-NLS-1$
        if (fPageSelector != null) {
            text.append(":").append(fPageSelector).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
        }
        text.append("{"); //$NON-NLS-1$
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
