/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.idl.IIDLConstants;
import com.aptana.parsing.ast.ParseRootNode;

public class IDLParseRootNode extends ParseRootNode
{
	/**
	 * IDLParseRootNode
	 */
	public IDLParseRootNode()
	{
		this(null);
	}

	/**
	 * IDLParseRootNode
	 * 
	 * @param children
	 */
	public IDLParseRootNode(Symbol[] children)
	{
		super(children);
	}

	public String getLanguage()
	{
		return IIDLConstants.CONTENT_TYPE_IDL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.dtd.parsing.ast.DTDNode#accept(com.aptana.editor.dtd.parsing.ast.DTDTreeWalker)
	 */
	public void accept(IDLTreeWalker walker)
	{
		walker.visit(this);
	}
}
