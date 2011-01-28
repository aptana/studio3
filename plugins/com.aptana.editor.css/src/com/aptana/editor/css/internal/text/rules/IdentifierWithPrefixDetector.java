/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text.rules;

/**
 * Detects words starting with the provided character and containing letters or digits.
 * 
 * @author Chris Williams
 *
 */
public class IdentifierWithPrefixDetector extends KeywordIdentifierDetector
{
	private char start;
	
	public IdentifierWithPrefixDetector(char start)
	{
		this.start = start;
	}
	
	@Override
	public boolean isWordStart(char c) 
	{
		return c == start;
	}
}
