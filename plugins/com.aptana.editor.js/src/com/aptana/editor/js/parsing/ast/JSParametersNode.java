package com.aptana.editor.js.parsing.ast;

public class JSParametersNode extends JSNaryNode
{
	/**
	 * JSParametersNode
	 * 
	 * @param children
	 */
	public JSParametersNode(JSNode... children)
	{
		super(JSNodeTypes.PARAMETERS, children);
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
