package com.aptana.editor.js.parsing.ast;

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
