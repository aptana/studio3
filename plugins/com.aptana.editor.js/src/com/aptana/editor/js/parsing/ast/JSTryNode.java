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
}
