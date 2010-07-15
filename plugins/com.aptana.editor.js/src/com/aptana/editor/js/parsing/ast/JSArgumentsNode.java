package com.aptana.editor.js.parsing.ast;

public class JSArgumentsNode extends JSNaryNode
{
	/**
	 * JSArgumentsNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSArgumentsNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.ARGUMENTS, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendCloseText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendCloseText(StringBuilder buffer)
	{
		buffer.append(")"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("("); //$NON-NLS-1$
	}
}
