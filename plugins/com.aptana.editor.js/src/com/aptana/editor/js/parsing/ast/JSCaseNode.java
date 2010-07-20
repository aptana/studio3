package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSCaseNode extends JSNode
{
	private Symbol _colon;

	/**
	 * JSCaseNode
	 * 
	 * @param children
	 */
	public JSCaseNode(JSNode expression, Symbol colon, JSNode... children)
	{
		super(JSNodeTypes.CASE, expression);

		for (JSNode child : children)
		{
			this.addChild(child);
		}
		
		this._colon = colon;
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
	 * getColon
	 * 
	 * @return
	 */
	public Symbol getColon()
	{
		return this._colon;
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(0);
	}
}
