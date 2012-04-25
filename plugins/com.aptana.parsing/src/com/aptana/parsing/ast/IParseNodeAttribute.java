/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.IRange;

/**
 * @author Kevin Lindsey
 */
public interface IParseNodeAttribute
{
	/**
	 * getName
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Gets the parent of the node.
	 * 
	 * @return Returns the parent of the node.
	 */
	IParseNode getParent();

	/**
	 * getValue
	 * 
	 * @return String
	 */
	String getValue();

	/**
	 * getNameRange
	 * 
	 * @return
	 */
	IRange getNameRange();

	/**
	 * getValueRange
	 * 
	 * @return
	 */
	IRange getValueRange();
}
