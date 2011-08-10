/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.core.logging.IdeLog;
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
			IdeLog.logError(UIPlugin.getDefault(), e);
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
			String editorId = (editorDescriptor == null) ? "com.aptana.editor.text" : editorDescriptor.getId(); //$NON-NLS-1$
			try
			{
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file.toURI(),
						editorId, true);
			}
			catch (PartInitException e)
			{
				IdeLog.logError(UIPlugin.getDefault(), e);
			}
		}
	}
}
