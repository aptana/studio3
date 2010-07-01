package com.aptana.editor.js.parsing.ast;

public class JSEmptyNode extends JSNode
{
	/**
	 * JSEmptyNode
	 */
	public JSEmptyNode()
	{
		super(JSNodeTypes.EMPTY);
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
