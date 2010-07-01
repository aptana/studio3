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
	 * @see com.aptana.editor.js.parsing.ast.JSLabelStatementNode#getKeyword()
	 */
	@Override
	protected String getKeyword()
	{
		return "continue"; //$NON-NLS-1$
	}
}
