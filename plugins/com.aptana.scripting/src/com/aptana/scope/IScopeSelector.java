/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.List;

/**
 * IScopeSelector
 */
public interface IScopeSelector
{

	/**
	 * matches
	 * 
	 * @param scope
	 * @return
	 */
	boolean matches(String scope);

	/**
	 * matches
	 * 
	 * @param scopes
	 * @return
	 */
	boolean matches(String[] scopes);

	/**
	 * Returns a list of integers. This list matches up with the match length for each segment of the scope we matched
	 * against. We break the scope up by spaces, and for each part there, we have a value at that offset in this list.
	 * That value is the length of that part that matched. This is used to determine which scope selector is a better
	 * match.
	 * 
	 * @return
	 */
	List<Integer> matchResults();
}