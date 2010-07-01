package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSWithNode extends JSNode
{
	/**
	 * JSWithNode
	 * 
	 * @param children
	 */
	public JSWithNode(JSNode... children)
	{
		super(JSNodeTypes.WITH, children);
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
		return this.getChild(1);
	}
	
	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("with ("); //$NON-NLS-1$
		buffer.append(children[0]);
		buffer.append(") ");
		buffer.append(children[1]); //$NON-NLS-1$

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
