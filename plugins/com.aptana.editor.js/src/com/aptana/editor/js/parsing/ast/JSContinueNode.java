package com.aptana.editor.js.parsing.ast;

public class JSContinueNode extends JSLabelStatementNode
{
	/**
	 * JSContinueNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSContinueNode(int start, int end)
	{
		super(JSNodeTypes.CONTINUE, start, end);
	}

	/**
	 * JSContinueNode
	 * 
	 * @param start
	 * @param end
	 * @param identifier
	 */
	public JSContinueNode(int start, int end, String identifier)
	{
		super(JSNodeTypes.CONTINUE, start, end, identifier);
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
