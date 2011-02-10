/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * CSSParseRootNode
 */
public class CSSParseRootNode extends ParseRootNode
{
	/**
	 * CSSParseRootNode
	 */
	public CSSParseRootNode()
	{
		this(new Symbol[0]);
	}
	
	/**
	 * CSSParseRootNode
	 * 
	 * @param children
	 */
	public CSSParseRootNode(Symbol[] children)
	{
		super( //
			ICSSParserConstants.LANGUAGE, //
			(children != null) ? children : new Symbol[0], //
			(children != null && children.length > 0) ? children[0].getStart() : 0, //
			(children != null && children.length > 0) ? children[children.length - 1].getEnd() : 0);
	}
	
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}
}
