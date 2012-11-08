/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

/**
 * A cache key which has options to attach and collect comments. It's done in a way where the equals/hashCode don't use
 * the information on comments, but the requiresReparse does, so that if the current (cached) parse has comments it may
 * be reused if the parse is asked without comments (and the other way requires a new parse).
 * 
 * @author Fabio
 */
public class ParseStateCacheKeyWithComments implements IParseStateCacheKey
{

	private final boolean fAttachComments;
	private final boolean fCollectComments;
	private final IParseStateCacheKey fParentCacheKey;

	public ParseStateCacheKeyWithComments(boolean attachComments, boolean collectComments,
			IParseStateCacheKey parentCacheKey)
	{
		this.fParentCacheKey = parentCacheKey;
		this.fAttachComments = attachComments;
		this.fCollectComments = collectComments;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ParseStateCacheKeyWithComments))
		{
			return false;
		}
		ParseStateCacheKeyWithComments otherParentCacheKey = (ParseStateCacheKeyWithComments) obj;
		return fParentCacheKey.equals(otherParentCacheKey.fParentCacheKey);
	}

	@Override
	public int hashCode()
	{
		return fParentCacheKey.hashCode();
	}

	public boolean requiresReparse(IParseStateCacheKey newCacheKey)
	{
		if (!(newCacheKey instanceof ParseStateCacheKeyWithComments))
		{
			return true;
		}
		ParseStateCacheKeyWithComments newCacheKeyWithComments = (ParseStateCacheKeyWithComments) newCacheKey;
		if (fParentCacheKey.requiresReparse(newCacheKeyWithComments.fParentCacheKey))
		{
			return true;
		}
		if (newCacheKeyWithComments.fAttachComments && !this.fAttachComments)
		{
			return true;
		}
		if (newCacheKeyWithComments.fCollectComments && !this.fCollectComments)
		{
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return this.getClass().getName()
				+ "[attach: " + fAttachComments + ", collect: " + fCollectComments + ", parent: " + fParentCacheKey.toString() + "]"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
