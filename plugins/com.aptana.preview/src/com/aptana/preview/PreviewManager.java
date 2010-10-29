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

import java.util.WeakHashMap;

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
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.preview.internal.DefaultPreviewHandler;
import com.aptana.preview.internal.EditorUtils;
import com.aptana.preview.internal.PreviewEditorInput;
import com.aptana.preview.internal.PreviewEditorPart;
import com.aptana.preview.internal.PreviewHandlers;

/**
 * @author Max Stepanov
 * 
 */
public final class PreviewManager {

	private static PreviewManager instance;
	private IPropertyListener editorPropertyListener;
	private WeakHashMap<IEditorPart, PreviewEditorInput> trackedEditors = new WeakHashMap<IEditorPart, PreviewEditorInput>();

	/**
	 * 
	 */
	private PreviewManager() {
		editorPropertyListener = new IPropertyListener() {
			public void propertyChanged(Object source, int propId) {
				if (source instanceof IEditorPart && EditorPart.PROP_DIRTY == propId
						&& !((EditorPart) source).isDirty() && trackedEditors.containsKey(source)) {
					IEditorPart editorPart = (IEditorPart) source;
					try {
						openPreview(editorPart, editorPart.getEditorInput(), null);
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		};
	}

	public static PreviewManager getInstance() {
		if (instance == null) {
			instance = new PreviewManager();
		}
		return instance;
	}

	public void openPreviewForEditor(IEditorPart editorPart) {
		try {
			IEditorInput editorInput = editorPart.getEditorInput();
			if (!editorPart.isDirty()) {
				openPreview(editorPart, editorInput, null);
			} else if (editorPart instanceof AbstractTextEditor) {
				IDocumentProvider documentProvider = ((AbstractTextEditor) editorPart).getDocumentProvider();
				if (documentProvider != null) {
					if (documentProvider.canSaveDocument(editorInput)) {
						IDocument document = documentProvider.getDocument(editorInput);
						if (document != null) {
							try {
								openPreview(editorPart, editorInput, document.get());
							} catch (CoreException e) {
								Activator.log(e);
							}
						}
					} else {
						openPreview(editorPart, editorInput, null);
					}
				}
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	public void openPreviewForEditorInput(IEditorInput editorInput) {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = null;
		if (workbenchWindow != null) {
			workbenchPage = workbenchWindow.getActivePage();
		}
		IEditorPart editorPart = null;
		if (workbenchPage != null) {
			editorPart = workbenchPage.findEditor(editorInput);
		}
		try {
			openPreview(editorPart, editorInput, null);
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	private void openPreview(IEditorPart editorPart, IEditorInput editorInput, String content) throws CoreException {
		String fileName = null;
		IProject project = null;
		IPath path = null;
		IPath workspacePath = null;
		if (editorInput instanceof IPathEditorInput) {
			path = ((IPathEditorInput) editorInput).getPath();
			if (path != null) {
				fileName = path.lastSegment();
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(path.toFile().toURI());
				if (files.length > 0) {
					project = files[0].getProject();
					workspacePath = files[0].getFullPath();
				}
			}
		} else if (editorInput instanceof IStorageEditorInput) {

		} else if (editorInput instanceof IURIEditorInput) {

		} else if (editorInput instanceof PreviewEditorInput) {
			return;
		}
		if (fileName == null) {
			return;
		}
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(fileName);
		IPreviewHandler handler = PreviewHandlers.getInstance().getHandler(contentType);
		if (handler == null) {
			handler = DefaultPreviewHandler.getInstance();
		}
		SourceConfig sourceConfig = new SourceConfig(editorInput, project, project != null ? workspacePath : path,
				content);
		PreviewConfig previewConfig = handler.handle(sourceConfig);
		if (previewConfig == null && !(handler instanceof DefaultPreviewHandler)) {
			previewConfig = DefaultPreviewHandler.getInstance().handle(sourceConfig);
		}
		if (previewConfig != null) {
			showEditor(editorPart, sourceConfig, previewConfig);
		} else {
			// TODO: add some user notification
		}
	}

	private void showEditor(IEditorPart editorPart, SourceConfig sourceConfig, PreviewConfig previewConfig) throws CoreException {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = null;
		if (workbenchWindow != null) {
			workbenchPage = workbenchWindow.getActivePage();
		}
		if (workbenchPage == null) {
			throw new PartInitException("Cannot get Workbench page"); //$NON-NLS-1$
		}
		IEditorPart[] openedPreviewEditors = new IEditorPart[0];
		if (editorPart != null) {
			PreviewEditorInput previewEditorInput = trackedEditors.get(editorPart);
			if (previewEditorInput != null) {
				openedPreviewEditors = EditorUtils.findEditors(previewEditorInput, PreviewEditorPart.EDITOR_ID);
			}
		}
		PreviewEditorInput input = new PreviewEditorInput(previewConfig.getURL(), sourceConfig.getEditorInput()
				.getName(), sourceConfig.getEditorInput().getToolTipText());
		if (openedPreviewEditors.length > 0) {
			for (IEditorPart previewEditorPart : openedPreviewEditors) {
				previewEditorPart.getSite().getPage().reuseEditor((IReusableEditor) previewEditorPart, input);
			}
		} else {
			openedPreviewEditors = EditorUtils.findEditors(input, PreviewEditorPart.EDITOR_ID);
			if (openedPreviewEditors.length > 0) {
				for (IEditorPart previewEditorPart : openedPreviewEditors) {
					previewEditorPart.getSite().getPage().reuseEditor((IReusableEditor) previewEditorPart, input);
				}				
			} else {
				workbenchPage.openEditor(input, PreviewEditorPart.EDITOR_ID, true, IWorkbenchPage.MATCH_INPUT);
			}
		}
		if (editorPart != null && !trackedEditors.containsKey(editorPart)) {
			editorPart.addPropertyListener(editorPropertyListener);
			trackedEditors.put(editorPart, input);
		}
	}

}
