package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSTryNode extends JSNode
{
	/**
	 * JSTryNode
	 * 
	 * @param children
	 */
	public JSTryNode(JSNode... children)
	{
		super(JSNodeTypes.TRY, children);
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
	 * getCatchBlock
	 * 
	 * @return
	 */
	public IParseNode getCatchBlock()
	{
		return this.getChild(1);
	}
	
	/**
	 * getFinallyBlock
	 * 
	 * @return
	 */
	public IParseNode getFinallyBlock()
	{
		return this.getChild(2);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("try "); //$NON-NLS-1$
		buffer.append(children[0]);

		if (!((JSNode) children[1]).isEmpty())
		{
			buffer.append(" ").append(children[1]); //$NON-NLS-1$
		}

		if (!((JSNode) children[2]).isEmpty())
		{
			buffer.append(" ").append(children[2]); //$NON-NLS-1$
		}

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}
