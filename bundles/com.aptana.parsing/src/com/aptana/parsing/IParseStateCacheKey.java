/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

/**
 * @author Fabio
 */
public interface IParseStateCacheKey
{

	/**
	 * @return if a reparse is required. It may be possible that 2 keys are equal but a reparse is still needed. (i.e.:
	 *         done given the need of having a result with or without comments -- in which case having the comments is
	 *         accepted if we ask for it without the comments but the other way around would need a reparse).
	 */
	public boolean requiresReparse(IParseStateCacheKey newCacheKey);

	/**
	 * Just making explicit that we must have equals/hashCode properly implemented as this will be a key in a map.
	 */
	public boolean equals(Object other);

	/**
	 * Just making explicit that we must have equals/hashCode properly implemented as this will be a key in a map.
	 */
	public int hashCode();

}
