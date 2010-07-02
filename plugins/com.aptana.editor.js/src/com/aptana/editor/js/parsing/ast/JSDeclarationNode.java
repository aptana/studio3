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
}
