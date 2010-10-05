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

package com.aptana.preview;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.preview.internal.DefaultPreviewHandler;
import com.aptana.preview.internal.PreviewEditorPart;
import com.aptana.preview.internal.PreviewHandlers;

/**
 * @author Max Stepanov
 *
 */
public final class PreviewManager {

	private static PreviewManager instance;
	
	/**
	 * 
	 */
	private PreviewManager() {
	}
	
	public static PreviewManager getInstance() {
		if (instance == null) {
			instance = new PreviewManager();
		}
		return instance;
	}

	public void openPreviewForEditor(IEditorPart editorPart) {
		IEditorInput editorInput = editorPart.getEditorInput();
		if (!editorPart.isDirty()) {
			openPreviewForEditorInput(editorInput);
		} else if (editorPart instanceof AbstractTextEditor) {
			IDocumentProvider documentProvider = ((AbstractTextEditor) editorPart).getDocumentProvider();
			if (documentProvider != null) {
				if (documentProvider.canSaveDocument(editorInput)) {
					IDocument document = documentProvider.getDocument(editorInput);
					if (document != null) {
						try {
							openPreview(editorInput, document.get());
						} catch (CoreException e) {
							Activator.log(e);
						}
					}
				} else {
					openPreviewForEditorInput(editorInput);
				}
			}
		}
	}
	
	public void openPreviewForEditorInput(IEditorInput editorInput) {
		try {
			openPreview(editorInput, null);
		} catch (CoreException e) {
			Activator.log(e);
		}
	}
	
	private void openPreview(IEditorInput editorInput, String content) throws CoreException {
		String fileName = null;
		IProject project = null;
		if (editorInput instanceof IPathEditorInput) {
			IPath path = ((IPathEditorInput) editorInput).getPath();
			if (path != null) {
				fileName = path.lastSegment();
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(path.toFile().toURI());
				if (files.length > 0) {
					project = files[0].getProject();
				}
			}
		} else if (editorInput instanceof IStorageEditorInput) {
			
		} else if (editorInput instanceof IURIEditorInput) {
			
		}
		if (fileName == null) {
			return;
		}
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(fileName);
		IPreviewHandler handler = PreviewHandlers.getInstance().getHandler(contentType);
		if (handler == null) {
			handler = DefaultPreviewHandler.getInstance();
		}
		SourceConfig sourceConfig = new SourceConfig();
		PreviewConfig previewConfig = handler.handle(sourceConfig);
		if (previewConfig != null) {
			showEditor();
		}
	}
	
	private void showEditor() throws CoreException {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = null;
		if (workbenchWindow != null) {
			workbenchPage = workbenchWindow.getActivePage();
		}
		if (workbenchPage == null) {
			throw new PartInitException("Cannot get Workbench page");
		}
		workbenchPage.openEditor(input, PreviewEditorPart.EDITOR_ID, true, IWorkbenchPage.MATCH_INPUT);
	}

}
