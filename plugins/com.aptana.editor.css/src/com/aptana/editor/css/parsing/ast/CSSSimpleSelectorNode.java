package com.aptana.editor.css.parsing.ast;

public class CSSSimpleSelectorNode extends CSSNode {

    private String fTypeSelector;
    private CSSAttributeSelectorNode[] fAttributeSelectors;

    public CSSSimpleSelectorNode(String typeSelector) {
        this(typeSelector, new CSSAttributeSelectorNode[0]);
    }

    public CSSSimpleSelectorNode(CSSAttributeSelectorNode[] attributeSelectors) {
        this(null, attributeSelectors);
    }

    public CSSSimpleSelectorNode(String typeSelector, CSSAttributeSelectorNode[] attributeSelectors) {
        fTypeSelector = typeSelector;
        fAttributeSelectors = attributeSelectors;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        if (fTypeSelector != null) {
            text.append(fTypeSelector);
        }
        for (CSSAttributeSelectorNode attribute : fAttributeSelectors) {
            text.append(attribute);
        }
        return text.toString();
    }
}
