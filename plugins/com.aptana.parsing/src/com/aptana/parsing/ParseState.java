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

import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;

public class ParseState implements IParseState
{

	private static final char[] NO_CHARS = new char[0];

	private char[] fSource;
	private char[] fInsertedText;
	private int fStartingOffset;
	private int fRemovedLength;
	private List<IParseError> fErrors;


	private IRange[] fSkippedRanges;
	private Map<String, Object> fProperties;

	// represents the root node of the parsing result
	private IParseRootNode fParseResult;
	private IProgressMonitor fProgressMonitor;

	public ParseState()
	{
		fSource = NO_CHARS;
		fInsertedText = NO_CHARS;
		fProperties = new HashMap<String, Object>();
		fErrors = new ArrayList<IParseError>();

	}

	public void clearEditState()
	{
		fInsertedText = NO_CHARS;
		fRemovedLength = 0;
		fSkippedRanges = null;
	}

	public IParseRootNode getParseResult()
	{
		return fParseResult;
	}

	public char[] getInsertedText()
	{
		return fInsertedText;
	}

	public int getRemovedLength()
	{
		return fRemovedLength;
	}

	public char[] getSource()
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

	public void setEditState(String source, String insertedText, int startingOffset, int removedLength)
	{
		fSource = (source != null) ? source.toCharArray() : NO_CHARS;
		fInsertedText = (insertedText != null) ? insertedText.toCharArray() : NO_CHARS;
		fStartingOffset = startingOffset;
		fRemovedLength = removedLength;
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

	/* (non-Javadoc)
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

		if (fRemovedLength > 0)
		{
			text.append(":r").append(fRemovedLength); //$NON-NLS-1$
		}

		int insertedLength = fInsertedText.length;
		if (insertedLength > 0)
		{
			text.append(":i").append(insertedLength).append(':').append(fInsertedText); //$NON-NLS-1$
		}
		else
		{
			// outputs closing delimiter for proper parsing of the offset
			text.append(':');
		}

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
