/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

class ErrorItem
{

	private final int severity;
	private final String message;
	private final String sourceURI;
	private final int line;
	private final String lineText;
	private final int lineOffset;

	public ErrorItem(int severity, String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		this.severity = severity;
		this.message = message;
		this.sourceURI = sourceURI;
		this.line = line;
		this.lineText = lineText;
		this.lineOffset = lineOffset;
	}

	public int getSeverity()
	{
		return severity;
	}

	public String getMessage()
	{
		return message;
	}

	public String getSourceURI()
	{
		return sourceURI;
	}

	public int getLine()
	{
		return line;
	}

	public String getLineText()
	{
		return lineText;
	}

	public int getLineOffset()
	{
		return lineOffset;
	}
}
