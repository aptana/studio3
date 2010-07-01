package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSThrowNode extends JSPreUnaryOperatorNode
{
	/**
	 * JSThrowNode
	 * 
	 * @param expression
	 */
	public JSThrowNode(JSNode expression)
	{
		super(JSNodeTypes.THROW, expression);
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
	 * @see com.aptana.editor.js.parsing.ast.JSUnaryOperatorNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = this.getChildren();

		buffer.append("throw "); //$NON-NLS-1$
		buffer.append(children[0]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
