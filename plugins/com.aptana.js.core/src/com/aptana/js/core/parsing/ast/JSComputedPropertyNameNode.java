package com.aptana.js.core.parsing.ast;

public class JSComputedPropertyNameNode extends JSNode
{

	public JSComputedPropertyNameNode(JSNode expression)
	{
		super(IJSNodeTypes.COMPUTED_PROPERTY, expression);
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
