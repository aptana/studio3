package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSStringNode extends JSPrimitiveNode
{
	/**
	 * JSStringNode
	 * 
	 * @param identifier
	 */
	public JSStringNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd(), (String) identifier.value);
	}

	/**
	 * JSStringNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSStringNode(int start, int end, String text)
	{
		super(JSNodeTypes.STRING, start, end, text);
	}
}
