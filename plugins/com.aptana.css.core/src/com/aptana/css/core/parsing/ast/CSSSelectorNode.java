/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import com.aptana.core.util.StringUtil;

public class CSSSelectorNode extends CSSNode
{
	private String fCombinator;

	/**
	 * CSSSelectorNode
	 * 
	 * @param simpleSelectors
	 */
	public CSSSelectorNode(CSSSimpleSelectorNode... simpleSelectors)
	{
		setChildren(simpleSelectors);
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.SELECTOR;
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
	 * getCombinator
	 * 
	 * @return
	 */
	public String getCombinator()
	{
		return StringUtil.getStringValue(fCombinator);
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

	/**
	 * setCombinator
	 * 
	 * @param combinator
	 */
	public void setCombinator(String combinator)
	{
		fCombinator = combinator;
	}
}
