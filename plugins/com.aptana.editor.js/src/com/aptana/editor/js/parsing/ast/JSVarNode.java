package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSVarNode extends JSNaryNode
{
	/**
	 * JSVarNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSVarNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.VAR, start, end, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("var "); //$NON-NLS-1$
	}
	
	/**
	 * getDeclarations
	 * 
	 * @return
	 */
	public IParseNode[] getDeclarations()
	{
		return this.getChildren();
	}
}
