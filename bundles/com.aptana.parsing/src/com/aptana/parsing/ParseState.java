/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.ImmutableTupleN;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class ParseState implements IParseState
{

	private String fSource;
	private int fStartingOffset;

	private IRange[] fSkippedRanges;

	private IProgressMonitor fProgressMonitor;

	/**
	 * Used for determining if we need to re-parse or cache is valid. If 2 objects have the same cache-key, their parse
	 * results should be considered equal.
	 */
	private ImmutableTupleN fCacheKey;

	private String filename;

	public ParseState(String source)
	{
		this(source, 0);
	}

	public ParseState(String source, int startingOffset)
	{
		this(source, startingOffset, null);
	}

	public ParseState(String source, int startingOffset, IRange[] ranges)
	{
		fSource = (source != null) ? source : StringUtil.EMPTY;
		fStartingOffset = startingOffset;
		fSkippedRanges = ranges;
		fCacheKey = calculateCacheKey();
	}

	public ParseState(String source, String filename)
	{
		this(source);
		this.filename = filename;
	}

	/**
	 * @return the cache-key to be used. Subclasses may override.
	 */
	protected ImmutableTupleN calculateCacheKey()
	{
		int length = fSource.length();
		if (length < 11)
		{
			// If it's a small string, just keep it instead of using the hashCode().
			return new ImmutableTupleN(length, fSource, fStartingOffset);
		}
		else
		{
			// Note: we currently use the fSource.hashCode() in order to decide if the contents of this parse equal
			// the contents of another parse. Conflicts may still arise in this situation -- an md5 would be more
			// accurate. As an attempt to make this just a bit better we also get 5 chars from the string from
			// many locations of the string and add them to our result too, so that the chance of a collision is even
			// lower.
			char[] chars = new char[5];
			double factor = length / 4.0;

			chars[0] = fSource.charAt(0); // first char
			chars[1] = fSource.charAt((int) factor);
			chars[2] = fSource.charAt((int) (2 * factor));
			chars[3] = fSource.charAt((int) (3 * factor));
			chars[4] = fSource.charAt(length - 1); // last char

			return new ImmutableTupleN(length, fSource.hashCode(), new String(chars), fStartingOffset);
		}
	}

	public void clearEditState()
	{
		fSource = null;
		fSkippedRanges = null;
	}

	public String getSource()
	{
		return fSource;
	}

	public int getStartingOffset()
	{
		return fStartingOffset;
	}

	public void setSkippedRanges(IParseNode[] skippedRanges)
	{
		fSkippedRanges = skippedRanges;
	}

	public IRange[] getSkippedRanges()
	{
		return fSkippedRanges;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.IParseState#getProgressMonitor()
	 */
	public IProgressMonitor getProgressMonitor()
	{
		if (fProgressMonitor == null)
		{
			fProgressMonitor = new NullProgressMonitor();
		}
		return fProgressMonitor;
	}

	public void setProgressMonitor(IProgressMonitor monitor)
	{
		fProgressMonitor = monitor;
	}

	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append('@').append(fStartingOffset);
		text.append(':');
		return text.toString();
	}

	public IParseStateCacheKey getCacheKey(String contentTypeId)
	{
		return new ParseStateCacheKey(contentTypeId, fCacheKey);
	}

	public String getFilename()
	{
		return this.filename;
	}

}
