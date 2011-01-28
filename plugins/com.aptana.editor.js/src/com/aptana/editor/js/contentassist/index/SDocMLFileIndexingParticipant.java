/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;

public class SDocMLFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);

		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}

			Thread.yield(); // be nice to other threads, let them get in before each file...

			this.indexFileStore(index, file, sub.newChild(100));
		}

		sub.done();
	}

	/**
	 * indexFileStore
	 * 
	 * @param index
	 * @param file
	 * @param monitor
	 */
	private void indexFileStore(Index index, IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		if (file == null)
		{
			return;
		}
		try
		{
			sub.subTask(getIndexingMessage(index, file));

			try
			{
				JSMetadataReader reader = new JSMetadataReader();

				InputStream stream = file.openInputStream(EFS.NONE, sub.newChild(20));

				// parse
				reader.loadXML(stream);
				sub.worked(50);

				// process results
				JSIndexWriter indexer = new JSIndexWriter();

				// create new Window type for this file
				JSIndexReader jsir = new JSIndexReader();
				TypeElement window = jsir.getType(index, JSTypeConstants.WINDOW_TYPE, true);

				if (window == null)
				{
					window = new TypeElement();
					window.setName(JSTypeConstants.WINDOW_TYPE);
				}

				URI location = file.toURI();

				// write types and add properties to Window
				for (TypeElement type : reader.getTypes())
				{
					// apply user agents to type
					JSTypeUtil.addAllUserAgents(type);

					// apply user agents to all properties
					for (PropertyElement property : type.getProperties())
					{
						JSTypeUtil.addAllUserAgents(property);
					}

					// write type
					indexer.writeType(index, type, location);

					String typeName = type.getName();

					if (typeName.contains(".") == false && typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN) == false) //$NON-NLS-1$
					{
						PropertyElement property = window.getProperty(typeName);

						if (property == null)
						{
							property = new PropertyElement();

							property.setName(typeName);
							property.addType(typeName);

							JSTypeUtil.addAllUserAgents(property);

							window.addProperty(property);
						}
					}
				}

				// write global type info
				indexer.writeType(index, window, location);
			}
			catch (Throwable e)
			{
				JSPlugin.logError(e.getMessage(), e);
			}
		}
		finally
		{
			sub.done();
		}
	}

}
