package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

/**
 * This is the "rest" arguments given to an arrow or normal function declaration; or in an array binding This holds the
 * ... symbol and the identifier
 * 
 * @author cwilliams
 */
public class JSRestElementNode extends JSNode
{
	private Symbol _ellipsis;

	public JSRestElementNode(Symbol ellipsis, JSIdentifierNode ident)
	{
		this(ellipsis);
		addChild(ident);
	}

	/**
	 * USed by ANTLR AST
	 * 
	 * @param ellipsis
	 */
	public JSRestElementNode(Symbol ellipsis)
	{
		super(IJSNodeTypes.REST_ELEMENT);
		this._ellipsis = ellipsis;
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

	public Symbol getEllipsis()
	{
		return this._ellipsis;
	}

}
