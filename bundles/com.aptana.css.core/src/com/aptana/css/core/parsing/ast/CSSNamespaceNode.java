/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;


public class CSSNamespaceNode extends CSSNode
{
	private String fPrefix;
	private String fUriStr;

	/**
	 * CSSNamespaceNode
	 * 
	 * @param uri
	 */
	public CSSNamespaceNode(String uri)
	{
		this(null, uri);
	}

	/**
	 * CSSNamespaceNode
	 * 
	 * @param prefix
	 * @param uri
	 */
	public CSSNamespaceNode(String prefix, String uri)
	{
		fPrefix = prefix;
		fUriStr = uri;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.NAMESPACE;
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
			text.append(fPrefix).append(' ');
		}

		text.append(fUriStr);
		text.append(';');

		return text.toString();
	}
}
