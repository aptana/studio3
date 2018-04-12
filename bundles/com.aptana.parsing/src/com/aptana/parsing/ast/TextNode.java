/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.aptana.core.util.StringUtil;

/**
 * TextNode
 */
public class TextNode extends ParseNode
{
	private String text;

	public TextNode(String text)
	{
		super();

		this.text = text;
	}

	public String getLanguage()
	{
		return StringUtil.EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	public String getText()
	{
		return text;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<IParseNode> iterator()
	{
		return new Iterator<IParseNode>()
		{
			public boolean hasNext()
			{
				return false;
			}

			public IParseNode next()
			{
				throw new NoSuchElementException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}

			public void remove()
			{
				throw new UnsupportedOperationException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#addChild(com.aptana.parsing.ast.IParseNode)
	 */
	public void addChild(IParseNode child)
	{
		throw new UnsupportedOperationException(); // $codepro.audit.disable exceptionUsage.exceptionCreation
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getChildIndex(com.aptana.parsing.ast.IParseNode)
	 */
	public int getChildIndex(IParseNode child)
	{
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getElementName()
	 */
	public String getElementName()
	{
		return "#text"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.IParseNode#getNodeType()
	 */
	public short getNodeType()
	{
		return -3;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		return text;
	}
}
