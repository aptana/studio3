/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;

public class Problem implements IProblem
{

	private final int severity;
	private final String message;
	private int offset;
	private final int length;
	private int lineNumber;
	private final String sourcePath;
	private int priority;

	public Problem(int severity, String message, int offset, int length, int lineNumber, String sourcePath)
	{
		this(severity, message, offset, length, lineNumber, sourcePath, IMarker.PRIORITY_NORMAL);
	}

	public Problem(int severity, String message, int offset, int length, int lineNumber, String sourcePath,
			int priority)
	{
		this.severity = severity;
		this.message = message;
		this.offset = offset;
		this.length = length;
		this.lineNumber = lineNumber;
		this.sourcePath = sourcePath;
		this.priority = priority;
	}

	public int getOffset()
	{
		return offset;
	}

	public int getLength()
	{
		return length;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public int getPriority()
	{
		return priority;
	}

	public String getMessage()
	{
		return message;
	}

	public int getSeverity()
	{
		return severity;
	}

	public String getSourcePath()
	{
		return sourcePath;
	}

	public Map<String, Object> createMarkerAttributes()
	{
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(IMarker.SEVERITY, getSeverity());
		int length = getLength();
		if (length > 0)
		{
			attributes.put(IMarker.CHAR_START, getOffset());
			attributes.put(IMarker.CHAR_END, getOffset() + length);
		}
		attributes.put(IMarker.MESSAGE, getMessage());
		attributes.put(IMarker.LINE_NUMBER, getLineNumber());

		return attributes;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Problem))
		{
			return false;
		}
		Problem other = (Problem) obj;
		return severity == other.severity && offset == other.offset && length == other.length
				&& message.equals(other.message) && sourcePath.equals(other.sourcePath);
	}

	@Override
	public int hashCode()
	{
		int hash = severity;
		hash = hash * 31 + offset;
		hash = hash * 31 + length;
		hash = hash * 31 + message.hashCode();
		hash = hash * 31 + sourcePath.hashCode();
		return hash;
	}

	public boolean isError()
	{
		return severity == IMarker.SEVERITY_ERROR;
	}

	public boolean isWarning()
	{
		return severity == IMarker.SEVERITY_WARNING;
	}
	
	public boolean isTask()
	{
		// FIXME This is wrong! We can have "problems" that are info, and tasks are separate!
		return severity == IMarker.SEVERITY_INFO;
	}
}
