package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSContinueNode extends JSLabelStatementNode
{
	/**
	 * JSContinueNode
	 */
	public JSContinueNode()
	{
		super(JSNodeTypes.CONTINUE);
	}

	/**
	 * JSContinueNode
	 * 
	 * @param label
	 */
	public JSContinueNode(Symbol label)
	{
		super(JSNodeTypes.CONTINUE, label);
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
