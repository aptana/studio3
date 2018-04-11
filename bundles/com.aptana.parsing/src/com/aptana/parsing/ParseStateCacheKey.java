/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import com.aptana.core.util.ImmutableTupleN;

/**
 * @author Fabio
 */
public class ParseStateCacheKey extends ImmutableTupleN implements IParseStateCacheKey
{

	public ParseStateCacheKey(Object... tuple)
	{
		super(tuple);
	}

	public boolean requiresReparse(IParseStateCacheKey newCacheKey)
	{
		return !this.equals(newCacheKey);
	}
}
