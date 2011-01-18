/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSSelectorNode extends CSSNode
{

	public CSSSelectorNode(CSSRuleNode parent, CSSSimpleSelectorNode[] simpleSelectors, int start, int end)
	{
		super(CSSNodeTypes.SELECTOR, start, end);
		setParent(parent);
		setChildren(simpleSelectors);
	}

	public CSSRuleNode getRule()
	{
		return (CSSRuleNode) getParent();
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CSSSelectorNode) && super.equals(obj);
	}
}
