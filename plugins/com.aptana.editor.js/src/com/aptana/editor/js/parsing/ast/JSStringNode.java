package com.aptana.editor.js.parsing.ast;

import java.util.List;

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
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addReturnTypes(java.util.List)
	 */
	@Override
	protected void addReturnTypes(List<String> types)
	{
		types.add("String");
	}
}
