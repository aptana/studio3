package com.aptana.editor.js.parsing.ast;

public class JSDefaultNode extends JSNaryNode
{
	/**
	 * JSDefaultNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSDefaultNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.DEFAULT, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("default: "); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#getDelimiter()
	 */
	@Override
	protected String getDelimiter()
	{
		return "";
	}
}
