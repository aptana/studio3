/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSAtRuleNode extends CSSNode
{
	private String fName;
	private String fId;
	private String fText;

	/**
	 * CSSCharSetNode
	 * 
	 * @param encoding
	 * @param start
	 * @param end
	 */
	public CSSAtRuleNode(String name, String id)
	{
		super(CSSNodeTypes.AT_RULE);
		
		fName = name;
		fId = id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#accept(com.aptana.editor.css.parsing.ast.CSSTreeWalker)
	 */
	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder buf = new StringBuilder();
			
			// TODO: take into acct semicolon vs. block (curly braces)
			buf.append(fName).append(" ").append(fId).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
			fText = buf.toString();
		}
		
		return fText;
	}
}
