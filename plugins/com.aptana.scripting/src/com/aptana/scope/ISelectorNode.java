/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.List;

public interface ISelectorNode
{
	/**
	 * See {@link IScopeSelector#getMatchResults()}. Should never return null, if there are no matches, return an empty
	 * List.
	 * 
	 * @return
	 */
	List<Integer> getMatchResults();

	/**
	 * Determines if this selector node matches the current scope as encapsulated in the MatchContext
	 * 
	 * @param context
	 * @return
	 */
	boolean matches(MatchContext context);
}
