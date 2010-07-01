package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSForNode extends JSNode
{
	/**
	 * JSForNode
	 * 
	 * @param children
	 */
	public JSForNode(JSNode... children)
	{
		super(JSNodeTypes.FOR, children);
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
	 * getAdvance
	 * 
	 * @return
	 */
	public IParseNode getAdvance()
	{
		return this.getChild(2);
	}

	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(3);
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

		if (!((JSNode) children[0]).isEmpty())
		{
			buffer.append(children[0]);
		}
		buffer.append(";"); //$NON-NLS-1$

		if (!((JSNode) children[1]).isEmpty())
		{
			buffer.append(" ").append(children[1]); //$NON-NLS-1$
		}
		buffer.append(";"); //$NON-NLS-1$

		if (!((JSNode) children[2]).isEmpty())
		{
			buffer.append(" ").append(children[2]); //$NON-NLS-1$
		}

		buffer.append(") ").append(children[3]); //$NON-NLS-1$

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
