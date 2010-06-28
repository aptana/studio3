package com.aptana.editor.js.parsing.ast;

import java.util.List;

import beaver.Symbol;

public class JSRegexNode extends JSPrimitiveNode
{
	/**
	 * JSRegexNode
	 * 
	 * @param identifier
	 */
	public JSRegexNode(Symbol identifier)
	{
		this(identifier.getStart(), identifier.getEnd(), (String) identifier.value);
	}
	
	/**
	 * JSRegexNode
	 * 
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSRegexNode(int start, int end, String text)
	{
		super(JSNodeTypes.REGEX, start, end, text);
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addReturnTypes(java.util.List)
	 */
	@Override
	protected void addReturnTypes(List<String> types)
	{
		types.add("RegExp");
	}
}
