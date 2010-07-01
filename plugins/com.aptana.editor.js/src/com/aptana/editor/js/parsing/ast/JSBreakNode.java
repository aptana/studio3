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
	 * @see com.aptana.editor.js.parsing.ast.JSLabelStatementNode#getKeyword()
	 */
	@Override
	protected String getKeyword()
	{
		return "break"; //$NON-NLS-1$
	}
}
