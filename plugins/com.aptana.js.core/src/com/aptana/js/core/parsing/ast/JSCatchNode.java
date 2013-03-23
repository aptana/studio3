/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSCatchNode extends JSNode
{
	/**
	 * JSCatchNode
	 * 
	 * @param children
	 */
	public JSCatchNode(JSNode... children)
	{
		super(IJSNodeTypes.CATCH, children);
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
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(1);
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
}
