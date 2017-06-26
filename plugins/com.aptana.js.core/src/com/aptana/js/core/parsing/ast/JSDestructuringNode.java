package com.aptana.js.core.parsing.ast;

/**
 * Represents array/object destructuring. See http://es6-features.org/#ParameterContextMatching
 * 
 * @author cwilliams
 */
public class JSDestructuringNode extends JSNode
{

	public JSDestructuringNode(JSNode binding)
	{
		super(IJSNodeTypes.DESTRUCTURE, binding);
	}

	public JSDestructuringNode(JSNode binding, JSNode valueExpression)
	{
		super(IJSNodeTypes.DESTRUCTURE, binding, valueExpression);
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
