/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

import com.aptana.core.logging.IdeLog;

public abstract class AbstractFileIndexingParticipant implements IFileStoreIndexingParticipant, IExecutableExtension
{

	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$

	private int priority;

	protected void addIndex(Index index, URI uri, String category, String word)
	{
		index.addEntry(category, word, uri);
	}

	protected String getIndexingMessage(Index index, URI uri)
	{
		String relativePath = null;
		if (index != null)
		{
			relativePath = index.getRelativeDocumentPath(uri).toString();
		}
		else
		{
			relativePath = uri.toString();
		}

		return MessageFormat.format("Indexing {0}", relativePath); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#getPriority()
	 */
	public int getPriority()
	{
		return priority;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		priority = DEFAULT_PRIORITY;

		try
		{
			String priorityString = config.getAttribute(ATTR_PRIORITY);
			priority = Integer.parseInt(priorityString);
		}
		catch (NumberFormatException e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
		catch (InvalidRegistryObjectException e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
	}
}
