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
	/**
	 * CSSSelectorNode
	 * 
	 * @param parent
	 * @param simpleSelectors
	 * @param start
	 * @param end
	 */
	public CSSSelectorNode(CSSRuleNode parent, CSSSimpleSelectorNode[] simpleSelectors, int start, int end)
	{
		super(CSSNodeTypes.SELECTOR, start, end);
		setParent(parent);
		setChildren(simpleSelectors);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#accept(com.aptana.editor.css.parsing.ast.CSSTreeWalker)
	 */
	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CSSSelectorNode) && super.equals(obj);
	}

	/**
	 * getRule
	 * 
	 * @return
	 */
	public CSSRuleNode getRule()
	{
		return (CSSRuleNode) getParent();
	}
}
