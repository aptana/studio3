package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSVarNode extends JSNaryNode
{
	/**
	 * JSVarNode
	 * 
	 * @param children
	 */
	public JSVarNode(JSNode... children)
	{
		super(JSNodeTypes.VAR, children);
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
	 * getDeclarations
	 * 
	 * @return
	 */
	public IParseNode[] getDeclarations()
	{
		return this.getChildren();
	}
}
