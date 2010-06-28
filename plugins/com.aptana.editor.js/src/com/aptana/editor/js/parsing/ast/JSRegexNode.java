package com.aptana.editor.js.parsing.ast;

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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
}
