/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;



public interface IParser
{
	/**
	 * Parse the content contained within the specified parse state
	 * 
	 * @param parseState
	 * @return
	 * @throws Exception
	 */
	public ParseResult parse(IParseState parseState) throws Exception; // $codepro.audit.disable declaredExceptions
}
