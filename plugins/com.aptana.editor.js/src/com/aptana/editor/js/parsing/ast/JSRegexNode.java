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
		this((String) identifier.value);
	}

	/**
	 * JSRegexNode
	 * 
	 * @param text
	 */
	public JSRegexNode(String text)
	{
		super(JSNodeTypes.REGEX, text);
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
