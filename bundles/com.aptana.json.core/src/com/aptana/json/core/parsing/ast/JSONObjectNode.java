/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

/**
 * JSONObjectNode
 */
public class JSONObjectNode extends JSONNode
{
	/**
	 * JSONObjectNode
	 */
	public JSONObjectNode()
	{
		super(JSONNodeType.OBJECT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.core.parsing.ast.JSONNode#accept(com.aptana.json.core.parsing.ast.JSONTreeWalker)
	 */
	@Override
	public void accept(JSONTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getText()
	 */
	@Override
	public String getText()
	{
		String result = "<Object>"; //$NON-NLS-1$

		if (hasChildren())
		{
			for (IParseNode child : this)
			{
				if (child instanceof JSONEntryNode)
				{
					JSONEntryNode entry = (JSONEntryNode) child;
					IParseNode key = entry.getFirstChild();

					if (key instanceof JSONStringNode)
					{
						String name = key.getText();

						// TODO: store/retrieve property names in a preference
						if ("name".equals(name)) //$NON-NLS-1$
						{
							IParseNode value = entry.getLastChild();

							if (value instanceof JSONStringNode)
							{
								result += ": " + value.getText(); //$NON-NLS-1$
							}
						}
					}
				}
			}
		}

		return result;
	}
}
