package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSForInNode extends JSNode
{
	/**
	 * JSForInNode
	 * 
	 * @param children
	 */
	public JSForInNode(JSNode... children)
	{
		super(JSNodeTypes.FOR_IN, children);
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
		return this.getChild(2);
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(1);
	}

	/**
	 * getInitialization
	 * 
	 * @return
	 */
	public IParseNode getInitialization()
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

		buffer.append("for ("); //$NON-NLS-1$
		buffer.append(children[0]);
		buffer.append(" in "); //$NON-NLS-1$
		buffer.append(children[1]).append(") "); //$NON-NLS-1$
		buffer.append(children[2]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
