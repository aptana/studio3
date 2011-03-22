/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;

public class ValidationItem implements IValidationItem
{

	private final int severity;
	private final String message;
	private int offset;
	private final int length;
	private int lineNumber;
	private final String sourcePath;

	public ValidationItem(int severity, String message, int offset, int length, int lineNumber, String sourcePath)
	{
		this.severity = severity;
		this.message = message;
		this.offset = offset;
		this.length = length;
		this.lineNumber = lineNumber;
		this.sourcePath = sourcePath;
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
		attributes.put(IMarker.CHAR_START, getOffset());
		attributes.put(IMarker.CHAR_END, getOffset() + getLength());
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
}
