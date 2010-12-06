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
package com.aptana.ui.internal.commands;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.ui.UIPlugin;

/**
 * @author ashebanow
 */
public class ViewLogCommandHandler extends AbstractHandler
{

	/**
	 * 
	 */
	public ViewLogCommandHandler()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		String logFile = System.getProperty("osgi.logfile"); //$NON-NLS-1$

		try
		{
			openEditorForFile(logFile);
		}
		catch (Exception e)
		{
			UIPlugin.logError(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * @param filePath
	 *            TODO: we should really make this into a utility function, and have the Ruble::Editor.open method use
	 *            it. But I am giving up for now because Eclipse plugin dependencies drive me crazy!
	 */
	private void openEditorForFile(String filePath)
	{
		File file = new File(filePath);
		// Process an existing file.
		if (file.exists() && file.isFile())
		{
			Path path = new Path(filePath);
			IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
			IEditorDescriptor editorDescriptor = null;
			if (fileForLocation == null)
			{
				IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(filePath);
				editorDescriptor = editorRegistry.getDefaultEditor(filePath, contentType);
			}
			else
			{
				editorDescriptor = editorRegistry.getDefaultEditor(filePath);
			}
			String editorId = (editorDescriptor == null ? "com.aptana.editor.text" : editorDescriptor.getId()); //$NON-NLS-1$
			try
			{
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file.toURI(),
						editorId, true);
			}
			catch (PartInitException e)
			{
				UIPlugin.logError(e.getLocalizedMessage(), e);
			}
		}
	}
}
