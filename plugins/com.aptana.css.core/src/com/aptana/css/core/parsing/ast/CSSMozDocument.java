/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseNode;

/**
 * CSSMozDocument
 */
public class CSSMozDocument extends CSSNode
{
	private static final String MOZ_DOCUMENT = "@-moz-document "; //$NON-NLS-1$
	private static final IMap<IParseNode, String> PARSE_NODE_STRING_MAPPER = new IMap<IParseNode, String>()
	{
		public String map(IParseNode item)
		{
			return item.toString();
		}
	};

	/**
	 * CSSMozDocument
	 */
	public CSSMozDocument()
	{
	}

	/**
	 * CSSMozDocument
	 * 
	 * @param start
	 * @param end
	 */
	public CSSMozDocument(int start, int end)
	{
		super(start, end);
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.MOZ_DOCUMENT;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		IParseNode functions = getFirstChild();
		IParseNode rules = getLastChild();

		text.append(MOZ_DOCUMENT);

		if (functions != null)
		{
			// @formatter:off
			text.append(
				StringUtil.join(
					",", //$NON-NLS-1$
					CollectionsUtil.map(functions.iterator(), PARSE_NODE_STRING_MAPPER)
				)
			);
			// @formatter:on
		}

		text.append(" { "); //$NON-NLS-1$

		if (rules != null)
		{
			// @formatter:off
			text.append(
				StringUtil.join(
					" ", //$NON-NLS-1$
					CollectionsUtil.map(rules.iterator(), PARSE_NODE_STRING_MAPPER)
				)
			);
			// @formatter:on
		}

		text.append("}"); //$NON-NLS-1$

		return text.toString();
	}
}
