package com.aptana.editor.js.parsing.ast;

public class JSBreakNode extends JSLabelStatementNode
{
	/**
	 * JSBreakNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSBreakNode()
	{
		super(JSNodeTypes.BREAK);
	}

	/**
	 * JSBreakNode
	 * 
	 * @param start
	 * @param end
	 * @param identifier
	 */
	public JSBreakNode(String identifier)
	{
		super(JSNodeTypes.BREAK, identifier);
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
