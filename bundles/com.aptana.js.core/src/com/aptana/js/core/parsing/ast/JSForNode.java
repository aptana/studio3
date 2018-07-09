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

public class JSForNode extends JSAbstractForNode
{
	private Symbol _semicolon1;
	private Symbol _semicolon2;

	/**
	 * Used by ANTLR AST
	 * 
	 * @param leftParenthesis
	 * @param semicolon1
	 * @param semicolon2
	 * @param rightParenthesis
	 */
	public JSForNode(int start, int end, Symbol leftParenthesis, Symbol semicolon1, Symbol semicolon2, Symbol rightParenthesis)
	{
		super(IJSNodeTypes.FOR, start, end, leftParenthesis, rightParenthesis);

		this._semicolon1 = semicolon1;
		this._semicolon2 = semicolon2;
	}
	
	@Override
	public void replaceInit(JSNode combinedVarDecls)
	{
		super.replaceInit(combinedVarDecls);
		// Fix the first semicolon!
		int semi1 = combinedVarDecls.getEnd() + 1;
		Symbol newSemi1 = new Symbol(_semicolon1.getId(), semi1, semi1, _semicolon1.value);
		this._semicolon1 = newSemi1;
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
	 * getAdvance
	 * 
	 * @return
	 */
	public IParseNode getAdvance()
	{
		return this.getChild(2);
	}

	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(3);
	}

	/**
	 * getCondition
	 * 
	 * @return
	 */
	public IParseNode getCondition()
	{
		return this.getChild(1);
	}

	/**
	 * getInitialization
	 * 
	 * @return
	 */
	public IParseNode getInitializer()
	{
		return this.getChild(0);
	}

	/**
	 * getSemicolon1
	 * 
	 * @return
	 */
	public Symbol getSemicolon1()
	{
		return this._semicolon1;
	}

	/**
	 * getSemicolon2
	 * 
	 * @return
	 */
	public Symbol getSemicolon2()
	{
		return this._semicolon2;
	}
}
