/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing.ast;

import beaver.Symbol;

import com.aptana.dtd.core.IDTDConstants;
import com.aptana.parsing.ast.ParseRootNode;

public class DTDParseRootNode extends ParseRootNode
{
	/**
	 * DTDParseRootNode
	 */
	public DTDParseRootNode()
	{
		this(null);
	}

	/**
	 * DTDParseRootNode
	 * 
	 * @param children
	 */
	public DTDParseRootNode(Symbol[] children)
	{
		super(children);
	}

	public String getLanguage()
	{
		return IDTDConstants.CONTENT_TYPE_DTD;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.dtd.parsing.ast.DTDNode#accept(com.aptana.editor.dtd.parsing.ast.DTDTreeWalker)
	 */
	public void accept(DTDTreeWalker walker)
	{
		walker.visit(this);
	}
}
