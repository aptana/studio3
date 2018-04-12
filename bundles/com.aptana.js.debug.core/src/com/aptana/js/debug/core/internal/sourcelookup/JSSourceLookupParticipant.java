/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.sourcelookup;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;

import com.aptana.debug.core.sourcelookup.RemoteContentSourceLookupParticipant;
import com.aptana.js.debug.core.model.IJSScriptElement;
import com.aptana.js.debug.core.model.IJSStackFrame;
import com.aptana.js.debug.core.model.ISourceLink;

/**
 * The source lookup participant knows how to translate a JS stack frame into a source file name
 */
public class JSSourceLookupParticipant extends RemoteContentSourceLookupParticipant
{

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	public String getSourceName(Object object) throws CoreException
	{
		if (object instanceof IJSStackFrame)
		{
			URI uri = ((IJSStackFrame) object).getSourceFileName();
			if (uri == null)
			{
				return null;
			}
			return uri.toString();
		}
		else if (object instanceof IJSScriptElement)
		{
			URI uri = ((IJSScriptElement) object).getLocation();
			if (uri == null)
			{
				return null;
			}
			return uri.toString();
		}
		else if (object instanceof ISourceLink)
		{
			URI uri = ((ISourceLink) object).getLocation();
			if (uri == null)
			{
				return null;
			}
			return uri.toString();
		}
		else if (object instanceof IFileStore)
		{
			URI uri = ((IFileStore) object).toURI();
			if (uri == null)
			{
				return null;
			}
			return uri.toString();
		}
		else if (object instanceof URI)
		{
			return ((URI) object).toString();
		}
		else if (object instanceof String)
		{
			// assume it's a file name
			return (String) object;
		}
		return null;
	}

}