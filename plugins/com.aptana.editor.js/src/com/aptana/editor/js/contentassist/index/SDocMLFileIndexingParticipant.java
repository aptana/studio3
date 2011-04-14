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
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.AliasElement;
import com.aptana.editor.js.contentassist.model.FunctionElement;
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

				// create new Window type for this file
				JSIndexReader jsir = new JSIndexReader();
				TypeElement window = jsir.getType(index, JSTypeConstants.WINDOW_TYPE, true);

				if (window == null)
				{
					window = new TypeElement();
					window.setName(JSTypeConstants.WINDOW_TYPE);
				}

				// process results
				JSIndexWriter indexer = new JSIndexWriter();
				TypeElement[] types = reader.getTypes();
				AliasElement[] aliases = reader.getAliases();
				URI location = file.toURI();

				// write types and add properties to Window
				for (TypeElement type : types)
				{
					// apply user agents to type
					JSTypeUtil.addAllUserAgents(type);

					// apply user agents to all properties
					for (PropertyElement property : type.getProperties())
					{
						JSTypeUtil.addAllUserAgents(property);
					}

					String typeName = type.getName();

					if (typeName.contains(".") == false && typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN) == false) //$NON-NLS-1$
					{
						List<FunctionElement> constructors = type.getConstructors();

						if (constructors.isEmpty() == false)
						{
							for (FunctionElement constructor : constructors)
							{
								// remove the constructor and make it a global function
								type.removeProperty(constructor);
								window.addProperty(constructor);
							}

							// wrap the type name in Function<> and update the property owningType references to
							// that name
							String newName = JSTypeUtil.toFunctionType(typeName);

							type.setName(newName);

							for (PropertyElement property : type.getProperties())
							{
								property.setOwningType(newName);
							}
						}
						else
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

					// NOTE: we write the type after processing top-level types in case the type name changes
					indexer.writeType(index, type, location);
				}

				for (AliasElement alias : aliases)
				{
					String typeName = alias.getType();

					// NOTE: we currently assume we can only alias types that were encountered in this sdocml file
					PropertyElement property = window.getProperty(typeName);

					if (property != null)
					{
						// we found a property, now clone it
						if (property instanceof FunctionElement)
						{
							property = new FunctionElement((FunctionElement) property);
						}
						else
						{
							property = new PropertyElement(property);
						}

						// and change the name to match our alias
						property.setName(alias.getName());
					}
					else
					{
						// didn't find anything, so create a new property
						property = new PropertyElement();
						property.setName(alias.getName());
						property.addType(typeName);
					}

					window.addProperty(property);
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
