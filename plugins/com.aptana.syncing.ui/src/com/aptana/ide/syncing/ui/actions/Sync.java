/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PlatformUI;

import com.aptana.ui.util.UIUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia
 */
public final class Sync
{

	/**
	 * Uploads the file in the current editor.
	 */
	public static void uploadCurrentEditor()
	{
		IEditorPart editor = UIUtils.getActiveEditor();
		if (editor != null)
		{
			uploadEditor(editor.getEditorInput());
		}
	}

	public static void uploadEditor(IEditorInput input)
	{
		if (input instanceof IFileEditorInput)
		{
			upload(((IFileEditorInput) input).getFile());
		}
		else if (input instanceof IPathEditorInput)
		{
			IPath path = ((IPathEditorInput) input).getPath();
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IFile file = workspaceRoot.getFileForLocation(path);
			if (file != null)
			{
				upload(file);
			}
			else
			{
				upload(path);
			}
		}
		else if (input instanceof IURIEditorInput)
		{
			IURIEditorInput editorInput = (IURIEditorInput) input;
			try
			{
				upload(EFS.getStore(editorInput.getURI()));
			}
			catch (CoreException e)
			{
			}
		}
	}

	private static void upload(IStructuredSelection selection)
	{
		UploadAction action = new UploadAction();
		action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart());
		action.setSelection(selection);
		action.run(null);
	}

	/**
	 * Uploads an IAdaptable that represents a file.
	 * 
	 * @param file
	 *            the IAdaptable object
	 */
	private static void upload(IAdaptable file)
	{
		upload(new StructuredSelection(file));
	}

	/**
	 * Uploads an IPath.
	 * 
	 * @param path
	 *            the IPath object
	 */
	private static void upload(IPath path)
	{
		try
		{
			upload(EFS.getStore(URIUtil.toURI(path)));
		}
		catch (CoreException e)
		{
		}
	}

	/**
	 * Downloads the file in the current editor.
	 */
	public static void downloadCurrentEditor()
	{
		IEditorPart editor = UIUtils.getActiveEditor();
		if (editor == null)
		{
			return;
		}

		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput)
		{
			download(((IFileEditorInput) input).getFile());
		}
		else if (input instanceof IPathEditorInput)
		{
			download(((IPathEditorInput) input).getPath());
		}
		else if (input instanceof IURIEditorInput)
		{
			IURIEditorInput editorInput = (IURIEditorInput) input;
			try
			{
				download(EFS.getStore(editorInput.getURI()));
			}
			catch (CoreException e)
			{
			}
		}
	}

	private static void download(IStructuredSelection selection)
	{
		DownloadAction action = new DownloadAction();
		action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart());
		action.setSelection(selection);
		action.run(null);
	}

	/**
	 * Downloads an IAdaptable that represents a file.
	 * 
	 * @param file
	 *            the IAdaptable object
	 */
	private static void download(IAdaptable file)
	{
		download(new StructuredSelection(file));
	}

	/**
	 * Downloads an IPath.
	 * 
	 * @param path
	 *            the IPath object
	 */
	private static void download(IPath path)
	{
		try
		{
			download(EFS.getStore(URIUtil.toURI(path)));
		}
		catch (CoreException e)
		{
		}
	}
}
