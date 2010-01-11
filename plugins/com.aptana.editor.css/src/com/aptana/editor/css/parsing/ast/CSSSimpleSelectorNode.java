package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSSimpleSelectorNode extends CSSNode
{

	private String fTypeSelector;
	private CSSAttributeSelectorNode[] fAttributeSelectors;

	public CSSSimpleSelectorNode(Symbol typeSelector)
	{
		this(typeSelector, new CSSAttributeSelectorNode[0]);
	}

	public CSSSimpleSelectorNode(CSSAttributeSelectorNode[] attributeSelectors)
	{
		this(null, attributeSelectors);
	}

	public CSSSimpleSelectorNode(Symbol typeSelector, CSSAttributeSelectorNode[] attributeSelectors)
	{
		fTypeSelector = (typeSelector == null) ? null : typeSelector.value.toString();
		fAttributeSelectors = attributeSelectors;

		if (typeSelector == null)
		{
			if (fAttributeSelectors.length > 0)
			{
				this.start = fAttributeSelectors[0].getStart();
				this.end = fAttributeSelectors[fAttributeSelectors.length - 1].getEnd();
			}
		}
		else
		{
			this.start = typeSelector.getStart();
			if (fAttributeSelectors.length == 0)
			{
				this.end = typeSelector.getEnd();
			}
			else
			{
				this.end = fAttributeSelectors[fAttributeSelectors.length - 1].getEnd();
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		if (fTypeSelector != null)
		{
			text.append(fTypeSelector);
		}
		for (CSSAttributeSelectorNode attribute : fAttributeSelectors)
		{
			text.append(attribute);
		}
		return text.toString();
	}
}
