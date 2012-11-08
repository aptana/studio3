/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver.parsing.ast;

import beaver.Symbol;
import beaver.spec.ast.GrammarTreeRoot;

import com.aptana.editor.beaver.IBeaverConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * BeaverParseRootNode
 */
public class BeaverParseRootNode extends ParseRootNode
{
	private GrammarTreeRoot root;

	public BeaverParseRootNode(GrammarTreeRoot root)
	{
		super(new Symbol[0], 0, 0);

		this.root = root;
	}

	public String getLanguage()
	{
		return IBeaverConstants.CONTENT_TYPE_BEAVER;
	}

	public GrammarTreeRoot getRoot()
	{
		return root;
	}
}
