/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.terminal.internal.handlers;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
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
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.terminal.TerminalPlugin;
import com.aptana.terminal.preferences.IPreferenceConstants;
import com.aptana.terminal.views.TerminalView;

public class OpenTerminalHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// checks the current selection first
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
				if (resource != null) {
					IContainer folder;
					if (resource instanceof IContainer) {
						folder = (IContainer) resource;
					} else {
						folder = resource.getParent();
					}
					TerminalView.openView(folder.getName(), folder.getName(), folder.getLocation());
					return true;
				}

				IFileStore fileStore = (IFileStore) ((IAdaptable) selectedObject).getAdapter(IFileStore.class);
				try {
					if (fileStore != null && fileStore.toLocalFile(EFS.NONE, null) != null) {
						if (!fileStore.fetchInfo().isDirectory()) {
							fileStore = fileStore.getParent();
						}
						TerminalView.openView(fileStore.getName(), fileStore.getName(),
								URIUtil.toPath(fileStore.toURI()));
						return true;
					}
				} catch (CoreException e) {
					IdeLog.logError(TerminalPlugin.getDefault(), e);
				}
			}
		}

		// checks the active editor
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		if (editorPart != null) {
			IEditorInput input = editorPart.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				IContainer folder = fileInput.getFile().getParent();
				TerminalView.openView(folder.getName(), folder.getName(), folder.getLocation());
				return true;
			}
			if (input instanceof IStorageEditorInput) {
				IStorageEditorInput fileInput = (IStorageEditorInput) input;
				try {
					IStorage storage = fileInput.getStorage();
					if (storage != null) {
						IPath fullPath = storage.getFullPath();
						if (fullPath != null) {
							IPath parentPath = fullPath.removeLastSegments(1);
							TerminalView.openView(parentPath.lastSegment(), parentPath.lastSegment(), parentPath);
							return true;
						}
					}
				} catch (CoreException e) {
					IdeLog.logError(TerminalPlugin.getDefault(), e);
				}
			}
			if (input instanceof IPathEditorInput) {
				IPath path = ((IPathEditorInput) input).getPath();
				IPath parentPath = path.removeLastSegments(1);
				TerminalView.openView(parentPath.lastSegment(), parentPath.lastSegment(), parentPath);
				return true;
			}
			if (input instanceof IURIEditorInput) {
				IURIEditorInput fileInput = (IURIEditorInput) input;
				URI uri = fileInput.getURI();
				if (uri != null) {
					if ("file".equals(uri.getScheme())) //$NON-NLS-1$
					{
						IPath path = Path.fromOSString(uri.getPath());
						IPath parentPath = path.removeLastSegments(1);
						TerminalView.openView(parentPath.lastSegment(), parentPath.lastSegment(), parentPath);
						return true;
					}
				}
			}
		}

		if (!openUserWorkingDirectory()) {
			// User has no specific directory set, just open with a null working dir...
			TerminalView.openView(null, Messages.OpenTerminalHandler_LBL_Terminal, null);
		}
		return null;
	}

	private boolean openUserWorkingDirectory() {
		String workingDirectoryPref = Platform.getPreferencesService().getString(TerminalPlugin.PLUGIN_ID,
				IPreferenceConstants.WORKING_DIRECTORY, null, null);
		if (!StringUtil.isEmpty(workingDirectoryPref)) {
			IPath workingDirectory = Path.fromOSString(workingDirectoryPref);
			if (workingDirectory.toFile().isDirectory()) {
				TerminalView.openView(null, Messages.OpenTerminalHandler_LBL_Terminal, workingDirectory);
				return true;
			}
		}
		return false;
	}
}
