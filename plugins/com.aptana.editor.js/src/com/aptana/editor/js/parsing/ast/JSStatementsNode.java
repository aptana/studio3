package com.aptana.editor.js.parsing.ast;


public class JSStatementsNode extends JSNaryNode
{
	/**
	 * JSStatementsNode
	 * 
	 * @param children
	 */
	public JSStatementsNode(JSNode... children)
	{
		super(JSNodeTypes.STATEMENTS, children);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendCloseText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendCloseText(StringBuilder buffer)
	{
		buffer.append("}"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("{"); //$NON-NLS-1$
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
