package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSDeclarationNode extends JSNode
{
	/**
	 * JSDeclarationNode
	 * 
	 * @param children
	 */
	public JSDeclarationNode(JSNode... children)
	{
		super(JSNodeTypes.DECLARATION, children);
	}

	/**
	 * getIdentifier
	 * 
	 * @return
	 */
	public IParseNode getIdentifier()
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

		if (!((JSNode) children[1]).isEmpty())
		{
			buffer.append(" = ").append(children[1]); //$NON-NLS-1$
		}

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
