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

}
