/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * @author Kevin Lindsey
 */
public class ParseNodeXPath extends BaseXPath
{
	private static final long serialVersionUID = -5097831277212173034L;

	/**
	 * @param xpathExpr
	 * @throws JaxenException
	 */
	public ParseNodeXPath(String xpathExpr) throws JaxenException
	{
		super(xpathExpr, ParseNodeNavigator.getInstance());
	}

	public ParseNodeXPath(String xpathExpr, ParseNodeNavigator navigator) throws JaxenException
	{
		super(xpathExpr, navigator);
	}
}
