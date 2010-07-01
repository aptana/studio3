package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSFinallyNode extends JSNode
{
	/**
	 * JSFinallyNode
	 * 
	 * @param children
	 */
	public JSFinallyNode(JSNode... children)
	{
		super(JSNodeTypes.FINALLY, children);
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
	 * getBlock
	 * 
	 * @return
	 */
	public IParseNode getBlock()
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

		buffer.append("finally "); //$NON-NLS-1$
		buffer.append(children[0]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
