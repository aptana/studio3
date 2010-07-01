package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSDoNode extends JSNode
{
	/**
	 * JSDoNode
	 * 
	 * @param children
	 */
	public JSDoNode(JSNode... children)
	{
		super(JSNodeTypes.DO, children);
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
	
	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(0);
	}
	
	/**
	 * getCondition
	 * 
	 * @return
	 */
	public IParseNode getCondition()
	{
		return this.getChild(1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("do ").append(children[0]); //$NON-NLS-1$

		if (children[0].getNodeType() != JSNodeTypes.STATEMENTS)
		{
			buffer.append(";"); //$NON-NLS-1$
		}

		buffer.append(" while (").append(children[1]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
