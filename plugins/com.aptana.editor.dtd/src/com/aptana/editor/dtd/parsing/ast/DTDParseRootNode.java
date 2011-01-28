/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.dtd.parsing.DTDParserConstants;
import com.aptana.parsing.ast.ParseRootNode;

public class DTDParseRootNode extends ParseRootNode
{
	/**
	 * DTDParseRootNode
	 */
	public DTDParseRootNode()
	{
		this(new Symbol[0]);
	}

	/**
	 * DTDParseRootNode
	 * 
	 * @param children
	 */
	public DTDParseRootNode(Symbol[] children)
	{
		super( //
			DTDParserConstants.LANGUAGE, //
			children, //
			(children != null && children.length > 0) ? children[0].getStart() : 0, //
			(children != null && children.length > 0) ? children[0].getEnd() : 0 //
		);
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
