/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSVarNode extends JSNode
{
	private Symbol _var;

	public JSVarNode(int start, int end, Symbol var)
	{
		super(IJSNodeTypes.VAR);

		this._var = var;
		this.setLocation(start, end);
	}

	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	public IParseNode[] getDeclarations()
	{
		return this.getChildren();
	}

	public Symbol getVar()
	{
		return this._var;
	}

	/**
	 * "let", "const", or "var"
	 * 
	 * @return
	 */
	public String getVariableType()
	{
		return (String) this._var.value;
	}
}
