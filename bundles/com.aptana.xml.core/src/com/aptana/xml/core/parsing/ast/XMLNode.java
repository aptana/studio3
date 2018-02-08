/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing.ast;

import com.aptana.parsing.ast.ParseNode;
import com.aptana.xml.core.IXMLConstants;

public class XMLNode extends ParseNode
{
	private XMLNodeType fType;

	/**
	 * XMLNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 */
	public XMLNode(XMLNodeType type, int start, int end)
	{
		super();

		fType = type;
		this.setLocation(start, end);
	}

	/**
	 * XMLNode
	 * 
	 * @param type
	 * @param children
	 * @param start
	 * @param end
	 */
	public XMLNode(XMLNodeType type, XMLNode[] children, int start, int end)
	{
		this(type, start, end);

		setChildren(children);
	}

	public String getLanguage()
	{
		return IXMLConstants.CONTENT_TYPE_XML;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNodeType()
	 */
	@Override
	public short getNodeType()
	{
		return fType.getIndex();
	}
}
