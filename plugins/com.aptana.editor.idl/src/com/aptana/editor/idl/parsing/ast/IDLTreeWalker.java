/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class IDLTreeWalker
{
	/**
	 * visit
	 * 
	 * @param node
	 */
	public void visit(IDLParseRootNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof IDLNode)
			{
				((IDLNode) child).accept(this);
			}
		}
	}

	/**
	 * visitChildren
	 * 
	 * @param node
	 */
	protected void visitChildren(IDLNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof IDLNode)
			{
				((IDLNode) child).accept(this);
			}
		}
	}
}
