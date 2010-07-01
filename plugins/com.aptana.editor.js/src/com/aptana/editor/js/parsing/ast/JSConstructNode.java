package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSConstructNode extends JSNode
{
	/**
	 * JSConstructNode
	 * 
	 * @param children
	 */
	public JSConstructNode(JSNode... children)
	{
		super(JSNodeTypes.CONSTRUCT, children);
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
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("new "); //$NON-NLS-1$
		buffer.append(children[0]);
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
