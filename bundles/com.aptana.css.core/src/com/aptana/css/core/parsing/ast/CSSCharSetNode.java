/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSCharSetNode extends CSSNode
{
	private String fEncoding;
	private String fText;

	/**
	 * CSSCharSetNode
	 * 
	 * @param encoding
	 */
	public CSSCharSetNode(String encoding)
	{
		fEncoding = encoding;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.CHAR_SET;
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
		if (!super.equals(obj) || !(obj instanceof CSSCharSetNode))
		{
			return false;
		}

		CSSCharSetNode other = (CSSCharSetNode) obj;

		return fEncoding.equals(other.fEncoding);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fEncoding.hashCode();
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

			buf.append("@charset ").append(fEncoding).append(';'); //$NON-NLS-1$
			fText = buf.toString();
		}

		return fText;
	}
}
