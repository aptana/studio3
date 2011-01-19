/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSNamespaceNode extends CSSNode
{
	private String fPrefix;
	private String fUriStr;

	/**
	 * CSSNamespaceNode
	 * 
	 * @param start
	 * @param uri
	 * @param end
	 */
	public CSSNamespaceNode(int start, String uri, int end)
	{
		this(start, null, uri, end);
	}

	/**
	 * CSSNamespaceNode
	 * 
	 * @param start
	 * @param prefix
	 * @param uri
	 * @param end
	 */
	public CSSNamespaceNode(int start, String prefix, String uri, int end)
	{
		super(CSSNodeTypes.NAMESPACE, start, end);

		fPrefix = prefix;
		fUriStr = uri;
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
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSNamespaceNode))
		{
			return false;
		}

		CSSNamespaceNode other = (CSSNamespaceNode) obj;

		return toString().equals(other.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append("@namespace "); //$NON-NLS-1$

		if (fPrefix != null)
		{
			text.append(fPrefix).append(" "); //$NON-NLS-1$
		}

		text.append(fUriStr);
		text.append(";"); //$NON-NLS-1$

		return text.toString();
	}
}
