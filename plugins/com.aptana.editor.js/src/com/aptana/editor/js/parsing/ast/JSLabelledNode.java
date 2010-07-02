package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSLabelledNode extends JSNode
{
	/**
	 * JSLabelledNode
	 * 
	 * @param children
	 */
	public JSLabelledNode(JSNode... children)
	{
		super(JSNodeTypes.LABELLED, children);
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
		return this.getChild(1);
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
}
