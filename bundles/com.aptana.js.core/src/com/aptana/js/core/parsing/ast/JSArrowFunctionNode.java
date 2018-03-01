package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSArrowFunctionNode extends JSFunctionNode
{

	public JSArrowFunctionNode()
	{
		super();
		setNodeType(IJSNodeTypes.ARROW_FUNCTION);
	}

	@Override
	public IParseNode getName()
	{
		return new JSEmptyNode(getStartingOffset());
	}

	public JSParametersNode getParameters()
	{
		return (JSParametersNode) getChild(0);
	}

	public JSNode getBody()
	{
		return (JSNode) getChild(1);
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
