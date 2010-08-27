package com.aptana.editor.js.parsing.ast;

public class JSErrorNode extends JSNode
{
	/**
	 * JSErrorNode
	 */
	public JSErrorNode()
	{
		super(JSNodeTypes.ERROR);
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
}
