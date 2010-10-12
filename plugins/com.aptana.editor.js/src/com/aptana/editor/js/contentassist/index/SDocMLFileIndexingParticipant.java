/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;

public class SDocMLFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index, org.eclipse.core.runtime.IProgressMonitor)
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
			sub.subTask(file.getName());

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
				TypeElement window = new TypeElement();
				window.setName(JSTypeConstants.WINDOW_TYPE);
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
					
					if (typeName.startsWith(JSTypeConstants.GENERIC_CLASS_OPEN) == false)
					{
						PropertyElement property = new PropertyElement();
						property.setName(typeName);
						
						ReturnTypeElement returnType = new ReturnTypeElement();
						returnType.setType(typeName);
						
						property.addType(returnType);
						
						JSTypeUtil.addAllUserAgents(property);
						
						window.addProperty(property);
					}
				}

				// write global type info
				indexer.writeType(index, window, location);
			}
			catch (Throwable e)
			{
				Activator.logError(e.getMessage(), e);
			}
		}
		finally
		{
			sub.done();
		}
	}

}
