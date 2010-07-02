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
}
