/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;


public class JSInvokeNode extends JSNode
{
	/**
	 * JSInvokeNode
	 * 
	 * @param expression
	 * @param args
	 */
	public JSInvokeNode(JSNode expression, JSArgumentsNode args)
	{
		super(IJSNodeTypes.INVOKE, expression, args);
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
	 * getArguments
	 * 
	 * @return
	 */
	public JSArgumentsNode getArguments()
	{
		return (JSArgumentsNode) this.getChild(1);
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public JSNode getExpression()
	{
		return (JSNode) this.getChild(0);
	}
}
