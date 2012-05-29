/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.ImmutableTupleN;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;

public class ParseState implements IParseState
{

	private String fSource;
	private int fStartingOffset;
	private List<IParseError> fErrors;

	private IRange[] fSkippedRanges;
	private Map<String, Object> fProperties;

	// represents the root node of the parsing result
	private IParseRootNode fParseResult;
	private IProgressMonitor fProgressMonitor;

	/**
	 * Used for determining if we need to re-parse or cache is valid. If 2 objects have the same cache-key, their parse
	 * results should be considered equal.
	 */
	private ImmutableTupleN fCacheKey;

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
		fProperties = new HashMap<String, Object>();
		fErrors = new ArrayList<IParseError>();
		fSource = (source != null) ? source : StringUtil.EMPTY;
		fStartingOffset = startingOffset;
		fSkippedRanges = ranges;

		int length = fSource.length();
		if (length < 11)
		{
			// If it's a small string, just keep it instead of using the hashCode().
			fCacheKey = new ImmutableTupleN(length, fSource, fStartingOffset);
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

			fCacheKey = new ImmutableTupleN(length, fSource.hashCode(), new String(chars), fStartingOffset);
		}
	}

	public void clearEditState()
	{
		fSource = null;
		fSkippedRanges = null;
	}

	public IParseRootNode getParseResult()
	{
		return fParseResult;
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

	public Map<String, Object> getProperties()
	{
		return fProperties;
	}

	public void setParseResult(IParseRootNode result)
	{
		fParseResult = result;
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

	protected void addProperty(String key, Object value)
	{
		fProperties.put(key, value);
	}

	public List<IParseError> getErrors()
	{
		return new ArrayList<IParseError>(fErrors);
	}

	public void copyErrorsFrom(IParseState cachedParseState)
	{
		for (IParseError error : cachedParseState.getErrors())
		{
			addError(error);
		}
	}

	public void addError(IParseError error)
	{
		fErrors.add(error);
	}

	public void clearErrors()
	{
		fErrors.clear();
	}

	public void removeError(IParseError error)
	{
		fErrors.remove(error);
	}

	public IParseStateCacheKey getCacheKey(String contentTypeId)
	{
		return new ParseStateCacheKey(contentTypeId, fCacheKey);
	}

}
