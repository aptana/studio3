/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSFinallyNode extends JSNode
{
	public JSFinallyNode(int start, int end, JSNode statements)
	{
		super(IJSNodeTypes.FINALLY, statements);
		this.setLocation(start, end);
	}

	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	public IParseNode getBlock()
	{
		return this.getChild(0);
	}
}
