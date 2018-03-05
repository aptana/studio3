/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSTryNode extends JSNode
{

	public JSTryNode(int start, int end)
	{
		super(IJSNodeTypes.TRY);
		this.setLocation(start, end);
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
		return this.getChild(0);
	}

	/**
	 * getCatchBlock
	 * 
	 * @return
	 */
	public IParseNode getCatchBlock()
	{
		return this.getChild(1);
	}

	/**
	 * getFinallyBlock
	 * 
	 * @return
	 */
	public IParseNode getFinallyBlock()
	{
		return this.getChild(2);
	}
}
