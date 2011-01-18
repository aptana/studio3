/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;
import java.util.List;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

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
		super(CSSNodeTypes.SIMPLE_SELECTOR);
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

	public CSSAttributeSelectorNode[] getAttributeSelectors()
	{
		List<IParseNode> list = Arrays.asList(getChildren());
		return list.toArray(new CSSAttributeSelectorNode[list.size()]);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSSimpleSelectorNode))
		{
			return false;
		}
		CSSSimpleSelectorNode other = (CSSSimpleSelectorNode) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		if (fTypeSelector != null)
		{
			text.append(fTypeSelector);
		}
		CSSAttributeSelectorNode[] attributeSelectors = getAttributeSelectors();
		int size = attributeSelectors.length;
		for (int i = 0; i < size; ++i)
		{
			text.append(attributeSelectors[i]);
			if (i < size - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}
		return text.toString();
	}
}
