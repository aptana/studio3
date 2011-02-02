/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.json.parsing.IJSONParserConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * @author klindsey
 */
public class JSONParseRootNode extends ParseRootNode
{
	/**
	 * JSParseRootNode
	 */
	public JSONParseRootNode()
	{
		this(new Symbol[0]);
	}

	/**
	 * JSParseRootNode
	 * 
	 * @param children
	 * @param start
	 * @param end
	 */
	public JSONParseRootNode(Symbol[] children)
	{
		super( //
			IJSONParserConstants.LANGUAGE, //
			children, //
			(children != null && children.length > 0) ? children[0].getStart() : 0, //
			(children != null && children.length > 0) ? children[children.length - 1].getEnd() : 0);
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSONTreeWalker walker)
	{
		walker.visit(this);
	}
}
