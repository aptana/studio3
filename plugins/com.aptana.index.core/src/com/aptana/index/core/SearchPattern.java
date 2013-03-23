/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

public abstract class SearchPattern
{
	public static final int EXACT_MATCH = 0;
	public static final int PREFIX_MATCH = 0x0001;
	public static final int PATTERN_MATCH = 0x0002;
	public static final int CASE_SENSITIVE = 0x0008;
	public static final int REGEX_MATCH = 0x0010;

	// TODO Can we make this a real class that holds two enum values?
	// PatternType: PREFIX, PATTERN, EXACT, REGEX
	// Case: SENSITIVE, INSENSITIVE
}
