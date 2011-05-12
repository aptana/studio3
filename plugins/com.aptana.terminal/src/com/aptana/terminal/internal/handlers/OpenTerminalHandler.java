/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.handlers;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.core.util.StringUtil;
import com.aptana.terminal.Activator;
import com.aptana.terminal.preferences.IPreferenceConstants;
import com.aptana.terminal.views.TerminalView;

public class OpenTerminalHandler extends AbstractHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// FIXME Before this would grab user's working dir pref and use that if it had a value no matter the context.
		// Now we fallback to it if all else fails...
		IProject project = null;
		Object input = HandlerUtil.getShowInInput(event);
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			project = fileInput.getFile().getProject();
		}
		else if (input instanceof IStorageEditorInput)
		{
			IStorageEditorInput fileInput = (IStorageEditorInput) input;
			try
			{
				IStorage storage = fileInput.getStorage();
				if (storage != null)
				{
					IPath fullPath = storage.getFullPath();
					if (fullPath != null)
					{
						IPath parentPath = fullPath.removeLastSegments(1);
						TerminalView.openView(parentPath.lastSegment(), parentPath.lastSegment(), parentPath);
						return true;
					}
				}
			}
			catch (CoreException e)
			{
				// ignore
			}
		}
		else if (input instanceof IURIEditorInput)
		{
			IURIEditorInput fileInput = (IURIEditorInput) input;
			URI uri = fileInput.getURI();
			if (uri != null)
			{
				if ("file".equals(uri.getScheme())) //$NON-NLS-1$
				{
					IPath path = Path.fromOSString(uri.getPath());
					IPath parentPath = path.removeLastSegments(1);
					TerminalView.openView(parentPath.lastSegment(), parentPath.lastSegment(), parentPath);
					return true;
				}
			}
		}
		else if (input instanceof IProject)
		{
			project = (IProject) input;
		}
		else if (input instanceof IAdaptable)
		{
			IAdaptable adapt = (IAdaptable) input;
			IResource resource = (IResource) adapt.getAdapter(IResource.class);
			if (resource != null)
			{
				project = resource.getProject();
			}
		}
		if (project != null)
		{
			TerminalView.openView(project.getName(), project.getName(), project.getLocation());
			return true;
		}
		return openUserWorkingDirectory();
	}

	private boolean openUserWorkingDirectory()
	{
		String workingDirectoryPref = Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
				IPreferenceConstants.WORKING_DIRECTORY, null, null);
		if (!StringUtil.isEmpty(workingDirectoryPref))
		{
			IPath workingDirectory = Path.fromOSString(workingDirectoryPref);
			if (workingDirectory.toFile().isDirectory())
			{
				TerminalView.openView(null, Messages.OpenTerminalHandler_LBL_Terminal, workingDirectory);
				return true;
			}
		}
		return false;
	}
}
