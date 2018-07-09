/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

public class JSParametersNode extends JSNode
{

	public JSParametersNode(int lParen, int rParen)
	{
		super(IJSNodeTypes.PARAMETERS);
		this.setLocation(lParen, rParen);
	}

	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
}
