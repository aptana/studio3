/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import com.aptana.core.util.StringUtil;

public class CSSAtRuleNode extends CSSNode
{
	private String fName;
	private String fId;
	private String fText;

	/**
	 * CSSAtRuleNode
	 * 
	 * @param name
	 * @param id
	 */
	public CSSAtRuleNode(String name, String id)
	{
		fName = name;
		fId = id;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.AT_RULE;
	}

	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * getRuleId
	 * 
	 * @return
	 */
	public String getRuleId()
	{
		return fId;
	}

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder buf = new StringBuilder();

			// TODO: take into acct semicolon vs. block (curly braces)
			buf.append(getName());

			String id = getRuleId();
			if (!StringUtil.isEmpty(id))
			{
				buf.append(' ').append(id).append(';');
			}

			fText = buf.toString();
		}

		return fText;
	}
}
