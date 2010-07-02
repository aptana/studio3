package com.aptana.editor.js.parsing.ast;

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
	 * @param identifier
	 */
	public JSContinueNode(String identifier)
	{
		super(JSNodeTypes.CONTINUE, identifier);
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
