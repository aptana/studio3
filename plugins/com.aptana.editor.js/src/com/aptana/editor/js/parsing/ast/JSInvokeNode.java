package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSInvokeNode extends JSNode
{
	/**
	 * JSInvokeNode
	 * 
	 * @param children
	 */
	public JSInvokeNode(JSNode... children)
	{
		super(JSNodeTypes.INVOKE, children);
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
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(0);
	}

	/**
	 * getArguments
	 * 
	 * @return
	 */
	public IParseNode getArguments()
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

		buffer.append(children[0]);
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
