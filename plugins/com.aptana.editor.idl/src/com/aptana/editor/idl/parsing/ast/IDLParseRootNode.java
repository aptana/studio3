/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.idl.parsing.IDLParserConstants;
import com.aptana.parsing.ast.ParseRootNode;

public class IDLParseRootNode extends ParseRootNode
{
	/**
	 * IDLParseRootNode
	 */
	public IDLParseRootNode()
	{
		this(new Symbol[0]);
	}

	/**
	 * IDLParseRootNode
	 * 
	 * @param children
	 */
	public IDLParseRootNode(Symbol[] children)
	{
		super( //
			IDLParserConstants.LANGUAGE, //
			children, //
			(children != null && children.length > 0) ? children[0].getStart() : 0, //
			(children != null && children.length > 0) ? children[0].getEnd() : 0 //
		);
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
