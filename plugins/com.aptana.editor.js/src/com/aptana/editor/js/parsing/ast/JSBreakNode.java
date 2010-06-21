package com.aptana.editor.js.parsing.ast;

public class JSBreakNode extends JSLabelStatementNode
{
	/**
	 * JSBreakNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSBreakNode(int start, int end)
	{
		super(JSNodeTypes.BREAK, start, end);
	}

	/**
	 * JSBreakNode
	 * 
	 * @param start
	 * @param end
	 * @param identifier
	 */
	public JSBreakNode(int start, int end, String identifier)
	{
		super(JSNodeTypes.BREAK, start, end, identifier);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSLabelStatementNode#getKeyword()
	 */
	@Override
	protected String getKeyword()
	{
		return "break"; //$NON-NLS-1$
	}
}
