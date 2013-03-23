/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSEmptyNode extends JSNode
{
	/**
	 * JSEmptyNode
	 * 
	 * @param symbol
	 */
	public JSEmptyNode(Symbol symbol)
	{
		this(symbol.getEnd() + 1);
	}

	/**
	 * JSEmptyNode
	 */
	public JSEmptyNode(int offset)
	{
		super(IJSNodeTypes.EMPTY);

		this.setLocation(offset, offset - 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#setSemicolonIncluded(boolean)
	 */
	@Override
	public void setSemicolonIncluded(boolean included)
	{
		super.setSemicolonIncluded(included);

		if (included)
		{
			this.setLocation(this.getStart(), this.getStart());
		}
		else
		{
			this.setLocation(this.getStart(), this.getStart() - 1);
		}
	}
}
