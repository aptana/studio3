package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSIdentifierNode extends JSPrimitiveNode
{
	/**
	 * JSIdentifierNode
	 * 
	 * @param identifier
	 */
	public JSIdentifierNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd(), (String) identifier.value);
	}
	
	/**
	 * JSIdentifierNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSIdentifierNode(int start, int end, String text)
	{
		super(JSNodeTypes.IDENTIFIER, start, end, text);
	}
}
