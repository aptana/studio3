package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.ParseRootNode;

public class JSParseRootNode extends ParseRootNode
{
	private Scope<JSNode> _globalScope;

	/**
	 * JSParseRootNode
	 */
	public JSParseRootNode()
	{
		this(new Symbol[0], 0, 0);
	}

	/**
	 * JSParseRootNode
	 * 
	 * @param children
	 * @param start
	 * @param end
	 */
	public JSParseRootNode(Symbol[] children, int start, int end)
	{
		super(IJSParserConstants.LANGUAGE, children, start, end);
	}

	/**
	 * getGlobalScope
	 * 
	 * @return
	 */
	public Scope<JSNode> getGlobalScope()
	{
		return this._globalScope;
	}

	/**
	 * setGlobalScope
	 * 
	 * @param globalScope
	 */
	public void setGlobalScope(Scope<JSNode> globalScope)
	{
		this._globalScope = globalScope;
	}
}
