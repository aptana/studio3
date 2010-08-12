package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSBreakNode extends JSLabelStatementNode
{
	/**
	 * JSBreakNode
	 */
	public JSBreakNode()
	{
		super(JSNodeTypes.BREAK);
	}

	/**
	 * JSBreakNode
	 * 
	 * @param label
	 */
	public JSBreakNode(Symbol label)
	{
		super(JSNodeTypes.BREAK, label);
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
