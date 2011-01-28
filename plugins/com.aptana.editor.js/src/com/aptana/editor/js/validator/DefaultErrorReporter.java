/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class DefaultErrorReporter implements ErrorReporter
{

	private List<ErrorItem> items = new ArrayList<ErrorItem>();

	public DefaultErrorReporter()
	{
		items = new ArrayList<ErrorItem>();
	}

	public void error(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		items.add(new ErrorItem(IMarker.SEVERITY_ERROR, message, sourceURI, line, lineText, lineOffset));
	}

	public void warning(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		items.add(new ErrorItem(IMarker.SEVERITY_WARNING, message, sourceURI, line, lineText, lineOffset));
	}

	public EvaluatorException runtimeError(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		return new EvaluatorException(message, sourceURI, line, lineText, lineOffset);
	}

	public List<ErrorItem> getItems()
	{
		return Collections.unmodifiableList(items);
	}
}
