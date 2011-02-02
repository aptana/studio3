/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

/**
 * IScopeSelector
 */
public interface IScopeSelector
{
	/**
	 * getMatchFragments
	 * 
	 * @return
	 */
	int getMatchFragments();

	/**
	 * getMatchLength
	 * 
	 * @return
	 */
	int getMatchLength();

	/**
	 * getMatchOffset
	 * 
	 * @return
	 */
	int getMatchOffset();

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
}