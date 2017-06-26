package com.aptana.js.core.parsing.ast;

public class JSClassNode extends JSNode
{

	public JSClassNode(JSIdentifierNode ident, JSStatementsNode tail)
	{
		super(IJSNodeTypes.CLASS, ident, tail);
	}

	/**
	 * ClassExpression (no identifier)
	 * 
	 * @param tail
	 */
	public JSClassNode(JSStatementsNode tail)
	{
		super(IJSNodeTypes.CLASS, tail);
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

	public boolean hasName()
	{
		return getChildCount() > 1;
	}

	public JSStatementsNode getTail()
	{
		return (JSStatementsNode) getLastChild();
	}
}
