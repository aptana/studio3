/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.hyperlink;

import java.util.ArrayList;
import java.util.List;

import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;

/**
 * JSIdentifierCollector
 */
public class JSIdentifierCollector extends JSTreeWalker
{
	private String name;
	private List<JSIdentifierNode> identifiers = new ArrayList<JSIdentifierNode>();

	/**
	 * JSHyperlinkCollector
	 */
	public JSIdentifierCollector(String name)
	{
		this.name = name;
	}

	/**
	 * addIdentifier
	 * 
	 * @param link
	 */
	protected void addIdentifier(JSIdentifierNode identifier)
	{
		if (identifier != null)
		{
			identifiers.add(identifier);
		}
	}

	/**
	 * getIdentifiers
	 * 
	 * @return
	 */
	public List<JSIdentifierNode> getIdentifiers()
	{
		return identifiers;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		if (name != null && name.equals(node.getText()))
		{
			addIdentifier(node);
		}
	}
}
