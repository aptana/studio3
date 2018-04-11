/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSVarNode extends JSNode
{
	private Symbol _var;

	/**
	 * JSVarNode
	 * 
	 * @param children
	 */
	public JSVarNode(Symbol var, JSNode... children)
	{
		super(IJSNodeTypes.VAR, children);

		this._var = var;

		// NOTE: we set the range here to simplify JSParser, specifically when
		// var-declarations are used within for-declarations. This is not needed
		// for statement level var-declarations, but it doesn't hurt to do this
		// in those cases too.
		if (var != null)
		{
			if (children != null && children.length > 0)
			{
				this.setLocation(var.getStart(), children[children.length - 1].getEndingOffset());
			}
			else
			{
				this.setLocation(var.getStart(), var.getEnd());
			}
		}
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

	/**
	 * getDeclarations
	 * 
	 * @return
	 */
	public IParseNode[] getDeclarations()
	{
		return this.getChildren();
	}

	/**
	 * getVar
	 * 
	 * @return
	 */
	public Symbol getVar()
	{
		return this._var;
	}
}
