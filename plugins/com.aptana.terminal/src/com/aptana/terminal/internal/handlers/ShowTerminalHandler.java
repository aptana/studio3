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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.terminal.Activator;
import com.aptana.terminal.preferences.IPreferenceConstants;
import com.aptana.terminal.views.TerminalView;

public class ShowTerminalHandler extends AbstractHandler
{

	private static final String EXPLORER_PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$
	private static final String EXPLORER_ACTIVE_PROJECT = "activeProject"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IPath workingDirectory = null;
		String title = Messages.ShowTerminalHandler_LBL_Terminal;
		String viewId = null;

		if (openUserWorkingDirectory())
		{
			return null;
		}

		if (openAccordingToContext(event))
		{
			return null;
		}

		openAppexplorerProjectRoot();

		TerminalView.openView(viewId, title, workingDirectory);
		return null;
	}

	private boolean openAccordingToContext(ExecutionEvent event)
	{
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof IEditorPart)
		{
			return openBasedOnActiveEditorInput(event);
		}

		// Open based on view selection
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structured = (IStructuredSelection) selection;
			Object firstElement = structured.getFirstElement();
			IResource resource = null;
			if (firstElement instanceof IAdaptable)
			{
				resource = (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
			}
			if (resource != null)
			{
				IProject project = resource.getProject();
				if (project != null)
				{
					TerminalView.openView(project.getName(), project.getName(), project.getLocation());
					return true;
				}
			}
		}
		return false;
	}

	protected boolean openBasedOnActiveEditorInput(ExecutionEvent event)
	{
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		if (editorPart == null)
		{
			return false;
		}
		IEditorInput input = editorPart.getEditorInput();
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			IProject project = fileInput.getFile().getProject();
			if (project != null)
			{
				TerminalView.openView(project.getName(), project.getName(), project.getLocation());
				return true;
			}
		}
		else if (input instanceof IStorageEditorInput)
		{
			IStorageEditorInput fileInput = (IStorageEditorInput) input;
			try
			{
				IStorage storage = fileInput.getStorage();
				if (storage != null)
				{
					IPath parentPath = storage.getFullPath().removeLastSegments(1);
					TerminalView.openView(parentPath.lastSegment(), parentPath.lastSegment(), parentPath);
					return true;
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
		return false;
	}

	private boolean openAppexplorerProjectRoot()
	{
		String activeProjectName = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID,
				EXPLORER_ACTIVE_PROJECT, null, null);
		if (activeProjectName != null)
		{
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
			if (project != null)
			{
				TerminalView.openView(project.getName(), project.getName(), project.getLocation());
				return true;
			}
		}

		return false;
	}

	private boolean openUserWorkingDirectory()
	{
		String workingDirectoryPref = Activator.getDefault().getPreferenceStore()
				.getString(IPreferenceConstants.WORKING_DIRECTORY);
		if (workingDirectoryPref != null && workingDirectoryPref.length() > 0)
		{
			IPath workingDirectory = Path.fromOSString(workingDirectoryPref);
			if (workingDirectory.toFile().isDirectory())
			{
				TerminalView.openView(null, Messages.ShowTerminalHandler_LBL_Terminal, workingDirectory);
				return true;
			}
		}
		return false;
	}
}
