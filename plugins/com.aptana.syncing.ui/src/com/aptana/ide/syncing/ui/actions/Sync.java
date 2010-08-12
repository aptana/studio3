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
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.URIUtil;
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

import com.aptana.ui.UIUtils;

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
		if (editor == null)
		{
			return;
		}

		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput)
		{
			upload(((IFileEditorInput) input).getFile());
		}
		else if (input instanceof IPathEditorInput)
		{
			upload(((IPathEditorInput) input).getPath());
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

	public static void upload(IStructuredSelection selection)
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
	public static void upload(IAdaptable file)
	{
		upload(new StructuredSelection(file));
	}

	/**
	 * Uploads an IPath.
	 * 
	 * @param path
	 *            the IPath object
	 */
	public static void upload(IPath path)
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

	public static void download(IStructuredSelection selection)
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
	public static void download(IAdaptable file)
	{
		download(new StructuredSelection(file));
	}

	/**
	 * Downloads an IPath.
	 * 
	 * @param path
	 *            the IPath object
	 */
	public static void download(IPath path)
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
