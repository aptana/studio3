package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSSimpleSelectorNode extends CSSNode
{

	private String fTypeSelector;

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
		setChildren(attributeSelectors);

		if (typeSelector == null)
		{
			if (attributeSelectors.length > 0)
			{
				this.start = attributeSelectors[0].getStart();
				this.end = attributeSelectors[attributeSelectors.length - 1].getEnd();
			}
		}
		else
		{
			this.start = typeSelector.getStart();
			if (attributeSelectors.length == 0)
			{
				this.end = typeSelector.getEnd();
			}
			else
			{
				this.end = attributeSelectors[attributeSelectors.length - 1].getEnd();
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
		text.append(super.toString());
		return text.toString();
	}
}
