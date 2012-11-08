/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing;

import beaver.Symbol;

import com.aptana.editor.xml.IXMLConstants;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * @author cwilliams
 */
class XMLParseRootNode extends ParseRootNode
{

	public XMLParseRootNode(int start, int end)
	{
		super(new Symbol[0], start, end);
	}

	public String getLanguage()
	{
		return IXMLConstants.CONTENT_TYPE_XML;
	}

}
