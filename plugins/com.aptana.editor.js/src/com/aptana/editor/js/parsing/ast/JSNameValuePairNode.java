package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSNameValuePairNode extends JSNode
{
	/**
	 * JSNameValuePairNode
	 * 
	 * @param children
	 */
	public JSNameValuePairNode(JSNode... children)
	{
		super(JSNodeTypes.NAME_VALUE_PAIR, children);
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
	 * getName
	 */
	public IParseNode getName()
	{
		return this.getChild(0);
	}
	
	/**
	 * getValue
	 * 
	 * @return
	 */
	public IParseNode getValue()
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
		buffer.append(": "); //$NON-NLS-1$
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
