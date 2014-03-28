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

import com.aptana.core.util.ObjectUtil;

public class Problem implements IProblem
{

	private final int severity;
	private final String message;
	private int offset;
	private final int length;
	private int lineNumber;
	private final String sourcePath;
	private int priority;
	private Map<String, Object> fAttributes;

	public Problem(int severity, String message, int offset, int length, int lineNumber, String sourcePath)
	{
		this(severity, message, offset, length, lineNumber, sourcePath, IMarker.PRIORITY_NORMAL);
	}

	public Problem(int severity, String message, int offset, int length, int lineNumber, String sourcePath, int priority)
	{
		this.severity = severity;
		this.message = message;
		this.offset = offset;
		this.length = length;
		this.lineNumber = lineNumber;
		this.sourcePath = sourcePath;
		this.priority = priority;
		this.fAttributes = new HashMap<String, Object>(3);
	}

	public void setAttribute(String attrName, Object value)
	{
		fAttributes.put(attrName, value);
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

	public Severity getSeverity()
	{
		return IProblem.Severity.create(severity);
	}

	public String getSourcePath()
	{
		return sourcePath;
	}

	public Map<String, Object> createMarkerAttributes()
	{
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.putAll(getAttributes());

		attributes.put(IMarker.SEVERITY, getSeverity().intValue());
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

	public Map<String, Object> getAttributes()
	{
		return fAttributes;
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
		return severity == other.severity && priority == other.priority && offset == other.offset
				&& length == other.length && ObjectUtil.areEqual(message, other.message)
				&& ObjectUtil.areEqual(sourcePath, other.sourcePath);
	}

	@Override
	public int hashCode()
	{
		int hash = severity;
		hash = hash * 31 + priority;
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

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("problem "); //$NON-NLS-1$
		if (isError())
		{
			buf.append("ERROR"); //$NON-NLS-1$
		}
		else if (isWarning())
		{
			buf.append("WARNING"); //$NON-NLS-1$
		}
		else if (severity == IMarker.SEVERITY_INFO)
		{
			buf.append("INFO"); //$NON-NLS-1$
		}
		else
		{
			buf.append("severity="); //$NON-NLS-1$
			buf.append(severity);
		}
		buf.append(": "); //$NON-NLS-1$
		buf.append(getSourcePath());
		buf.append(" line="); //$NON-NLS-1$
		buf.append(getLineNumber());
		buf.append(" offset="); //$NON-NLS-1$
		buf.append(getOffset());
		buf.append(" length="); //$NON-NLS-1$
		buf.append(getLength());
		buf.append(' ');
		buf.append(message);
		return buf.toString();
	}
}
