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

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseError;
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

	public ParseState()
	{
		fSource = StringUtil.EMPTY;
		fProperties = new HashMap<String, Object>();
		fErrors = new ArrayList<IParseError>();

	}

	public void clearEditState()
	{
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

	public IRange[] getSkippedRanges()
	{
		return fSkippedRanges;
	}

	public Map<String, Object> getProperties()
	{
		return fProperties;
	}

	public void setEditState(String source, int startingOffset)
	{
		fSource = (source != null) ? source : StringUtil.EMPTY;
		fStartingOffset = startingOffset;
		fSkippedRanges = null;
	}

	public void setParseResult(IParseRootNode result)
	{
		fParseResult = result;
	}

	public void setSkippedRanges(IRange[] ranges)
	{
		fSkippedRanges = ranges;
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

	public void addError(IParseError error)
	{
		fErrors.add(error);
	}

	public void clearErrors()
	{
		fErrors.clear();
	}

}
