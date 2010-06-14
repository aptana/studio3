package com.aptana.editor.js.parsing.ast;

import java.util.List;

import beaver.Symbol;

public class JSNumberNode extends JSPrimitiveNode
{
	/**
	 * JSNumberNode
	 * 
	 * @param identifier
	 */
	public JSNumberNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd(), (String) identifier.value);
	}

	/**
	 * JSNumberNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSNumberNode(int start, int end, String text)
	{
		super(JSNodeTypes.NUMBER, start, end, text);
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addReturnTypes(java.util.List)
	 */
	@Override
	protected void addReturnTypes(List<String> types)
	{
		types.add("Number");
	}
}
