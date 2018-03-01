package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSYieldNode extends JSNode
{
	private final boolean _hasStar;

	public JSYieldNode(Symbol y)
	{
		super(IJSNodeTypes.YIELD);
		_hasStar = false;
	}

	public JSYieldNode(Symbol y, JSNode expression)
	{
		super(IJSNodeTypes.YIELD, expression);
		_hasStar = false;
	}

	public JSYieldNode(Symbol y, Symbol star, JSNode expression)
	{
		super(IJSNodeTypes.YIELD, expression);
		_hasStar = true;
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

	public IParseNode getExpression()
	{
		return getLastChild();
	}

	public boolean hasExpression()
	{
		return hasChildren();
	}

	public boolean hasStar()
	{
		return _hasStar;
	}
}
