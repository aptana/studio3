package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSNameValuePairNode extends JSNode
{
	private Symbol _colon;
	
	/**
	 * JSNameValuePairNode
	 * 
	 * @param name
	 * @param colon
	 * @param value
	 */
	public JSNameValuePairNode(JSNode name, Symbol colon, JSNode value)
	{
		super(JSNodeTypes.NAME_VALUE_PAIR, name, value);
		
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
	 * getName
	 */
	public IParseNode getName()
	{
		return this.getChild(0);
	}

	/**
	 * getValue
	 * 
	 * @return
	 */
	public IParseNode getValue()
	{
		return this.getChild(1);
	}
}
