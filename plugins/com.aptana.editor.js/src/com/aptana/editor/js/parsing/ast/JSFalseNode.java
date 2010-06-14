package com.aptana.editor.js.parsing.ast;

import java.util.List;

import beaver.Symbol;

public class JSFalseNode extends JSPrimitiveNode
{
	/**
	 * JSFalseNode
	 * 
	 * @param identifier
	 */
	public JSFalseNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd());
	}

	/**
	 * JSFalseNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public JSFalseNode(int start, int end)
	{
		super(JSNodeTypes.FALSE, start, end, "false");
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addReturnTypes(java.util.List)
	 */
	@Override
	protected void addReturnTypes(List<String> types)
	{
		types.add("Boolean");
	}
}
