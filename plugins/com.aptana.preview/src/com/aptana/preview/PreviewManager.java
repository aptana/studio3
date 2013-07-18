/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyAdapter;
import net.contentobjects.jnotify.JNotifyException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.editor.common.IEditorLinkedResources;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.preview.internal.DefaultPreviewHandler;
import com.aptana.preview.internal.EditorUtils;
import com.aptana.preview.internal.PreviewHandlers;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 * 
 */
public final class PreviewManager {

	private static PreviewManager instance;
	private IPropertyListener editorPropertyListener;
	private Map<IEditorPart, PreviewEditorInput> trackedEditors = new WeakHashMap<IEditorPart, PreviewEditorInput>();
	private Map<IEditorPart, Integer> filewatchIds = new HashMap<IEditorPart, Integer>();
	private Set<URI> trackedURIs = new HashSet<URI>();

	private IPartListener editorPartListener = new IPartListener() {

		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part instanceof IEditorPart) {
				part.removePropertyListener(editorPropertyListener);
				IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
				if (editorInput instanceof IURIEditorInput) {
					trackedURIs.remove(((IURIEditorInput) editorInput).getURI());
				}
			}
			if (trackedEditors.containsKey(part)) {
				trackedEditors.remove(part);
				// if it's in trackedEditors, it's guaranteed to be an IEditorPart
				removeFilewatchListener((IEditorPart) part);
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
			if (part instanceof IEditorPart) {
				part.addPropertyListener(editorPropertyListener);
				IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
				if (editorInput instanceof IURIEditorInput) {
					trackedURIs.add(((IURIEditorInput) editorInput).getURI());
				}
			}
		}
	};

	private final IWindowListener windowListener = new IWindowListener() {

		public void windowActivated(IWorkbenchWindow window) {
		}

		public void windowClosed(IWorkbenchWindow window) {
			IPartService partService = window.getPartService();
			if (partService != null) {
				partService.removePartListener(editorPartListener);
			}
		}

		public void windowDeactivated(IWorkbenchWindow window) {
		}

		public void windowOpened(IWorkbenchWindow window) {
			IPartService partService = window.getPartService();
			if (partService != null) {
				partService.addPartListener(editorPartListener);
			}
		}
	};

	/**
	 * 
	 */
	private PreviewManager() {
		editorPropertyListener = new IPropertyListener() {

			public void propertyChanged(Object source, int propId) {
				if (source instanceof IEditorPart && EditorPart.PROP_DIRTY == propId
						&& !((EditorPart) source).isDirty()) {
					if (trackedEditors.containsKey(source)) {
						IEditorPart editorPart = (IEditorPart) source;
						try {
							openPreview(editorPart, editorPart.getEditorInput(), null, false);
						} catch (CoreException e) {
							PreviewPlugin.log(e);
						}
					} else {
						IEditorInput editorInput = ((IEditorPart) source).getEditorInput();
						if (editorInput instanceof IURIEditorInput) {
							checkLinkedEditor(((IURIEditorInput) editorInput).getURI());
						}
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

	public void init() {
		addPartListener();
		// attaches property listener to all opened editors
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IWorkbenchPage[] pages;
		IEditorReference[] editors;
		IEditorPart editorPart;
		IEditorInput editorInput;
		for (IWorkbenchWindow window : windows) {
			pages = window.getPages();
			for (IWorkbenchPage page : pages) {
				editors = page.getEditorReferences();
				for (IEditorReference editor : editors) {
					editorPart = editor.getEditor(false);
					if (editorPart != null) {
						editorPart.addPropertyListener(editorPropertyListener);
						editorInput = ((IEditorPart) editorPart).getEditorInput();
						if (editorInput instanceof IURIEditorInput) {
							trackedURIs.add(((IURIEditorInput) editorInput).getURI());
						}
					}
				}
			}
		}
	}

	public void dispose() {
		removePartListener();
	}

	public void openPreviewForEditor(final IEditorPart editorPart) {
		try {
			final IEditorInput editorInput = editorPart.getEditorInput();
			if (!editorPart.isDirty()) {
				openPreview(editorPart, editorInput, null);
			} else if (editorPart instanceof AbstractTextEditor) {
				IDocumentProvider documentProvider = ((AbstractTextEditor) editorPart).getDocumentProvider();
				if (documentProvider != null) {
					if (documentProvider.canSaveDocument(editorInput)) {
						IDocument document = documentProvider.getDocument(editorInput);
						if (document != null) {
							try {
								if (!openPreview(editorPart, editorInput, document.get())) {
									final boolean[] openPreview = new boolean[1];
									UIUtils.getDisplay().syncExec(new Runnable() {
										public void run() {
											openPreview[0] = MessageDialog.openQuestion(editorPart.getSite().getShell(), Messages.PreviewManager_UnsavedPrompt_Title, Messages.PreviewManager_UnsavedPrompt_Message);
										}
									});
									if (openPreview[0]) {
										openPreview(editorPart, editorInput, null);
									}

								}
							} catch (CoreException e) {
								PreviewPlugin.log(e);
							}
						}
					} else {
						openPreview(editorPart, editorInput, null);
					}
				}
			}
		} catch (CoreException e) {
			PreviewPlugin.log(e);
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
			PreviewPlugin.log(e);
		}
	}

	public boolean testEditorInputForPreview(IEditorInput editorInput) {
		try {
			SourceConfig sourceConfig = getSourceConfig(editorInput, null);
			if (sourceConfig != null) {
				IPreviewHandler handler = PreviewHandlers.getInstance().getHandler(sourceConfig.getContentType());
				if (handler == null) {
					handler = DefaultPreviewHandler.getInstance();
				}

				PreviewConfig previewConfig = handler.handle(sourceConfig);
				if (previewConfig == null && !(handler instanceof DefaultPreviewHandler)) {
					previewConfig = DefaultPreviewHandler.getInstance().handle(sourceConfig);
				}
				return previewConfig != null;
			}
		} catch (CoreException e) {
			PreviewPlugin.log(e);
		}
		return false;
	}

	private boolean openPreview(IEditorPart editorPart, IEditorInput editorInput, String content) throws CoreException {
		return openPreview(editorPart, editorInput, content, true);
	}

	private SourceConfig getSourceConfig(IEditorInput editorInput, String content) throws CoreException {
		String fileName = null;
		IProject project = null;
		IPath path = null;
		IPath workspacePath = null;
		if (editorInput instanceof IPathEditorInput) {
			try {
				path = ((IPathEditorInput) editorInput).getPath();
				if (path != null) {
					fileName = path.lastSegment();
					IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(path.toFile().toURI());
					if (files.length > 0) {
						project = files[0].getProject();
						workspacePath = files[0].getFullPath();
					}
				}
			} catch (Exception e) {
				return null;
			}
		} else if (editorInput instanceof IStorageEditorInput) {
			// TODO
		} else if (editorInput instanceof IURIEditorInput) {
			IFileStore fileStore = EFS.getStore(((IURIEditorInput) editorInput).getURI());
			if (fileStore != null) {
				File file = fileStore.toLocalFile(EFS.NONE, null);
				if (file != null) {
					fileName = file.getName();
					path = Path.fromOSString(file.getAbsolutePath());
					IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(file.toURI());
					if (files.length > 0) {
						project = files[0].getProject();
						workspacePath = files[0].getFullPath();
					}
				}
			}
		} else if (editorInput instanceof PreviewEditorInput) {
			return null;
		}
		if (fileName == null) {
			return null;
		}
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(fileName);
		return new SourceConfig(editorInput, project, project != null ? workspacePath : path, content, contentType);
	}

	private boolean openPreview(final IEditorPart editorPart, IEditorInput editorInput, String content, boolean forceOpen) throws CoreException {
		SourceConfig sourceConfig = getSourceConfig(editorInput, content);
		PreviewConfig previewConfig = null;
		if (sourceConfig != null) {
			IPreviewHandler handler = PreviewHandlers.getInstance().getHandler(sourceConfig.getContentType());
			if (handler == null) {
				handler = DefaultPreviewHandler.getInstance();
			}
			previewConfig = handler.handle(sourceConfig);
			if (previewConfig == null && !(handler instanceof DefaultPreviewHandler)) {
				previewConfig = DefaultPreviewHandler.getInstance().handle(sourceConfig);
			}
		}
		if (previewConfig != null) {
			showEditor(editorPart, sourceConfig, previewConfig, forceOpen);
			return true;
		}
		return false;
	}

	private void showEditor(IEditorPart editorPart, SourceConfig sourceConfig, PreviewConfig previewConfig,
			boolean forceOpen) throws CoreException {
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
			if (forceOpen) {
				workbenchPage.activate(openedPreviewEditors[0]);
			}
		} else {
			openedPreviewEditors = EditorUtils.findEditors(input, PreviewEditorPart.EDITOR_ID);
			if (openedPreviewEditors.length > 0) {
				for (IEditorPart previewEditorPart : openedPreviewEditors) {
					previewEditorPart.getSite().getPage().reuseEditor((IReusableEditor) previewEditorPart, input);
				}
				if (forceOpen) {
					workbenchPage.activate(openedPreviewEditors[0]);
				}
			} else if (forceOpen) {
				workbenchPage.openEditor(input, PreviewEditorPart.EDITOR_ID, true, IWorkbenchPage.MATCH_INPUT);
			}
		}
		if (editorPart != null && !trackedEditors.containsKey(editorPart)) {
			editorPart.addPropertyListener(editorPropertyListener);
			trackedEditors.put(editorPart, input);
			addFilewatchListener(editorPart);
		}
	}

	private void checkLinkedEditor(URI uri) {
		IEditorPart editorPart = null;
		for (IEditorPart editor : trackedEditors.keySet()) {
			IEditorLinkedResources editorLinkedResources = (IEditorLinkedResources) editor.getAdapter(IEditorLinkedResources.class);
			if (editorLinkedResources != null) {
				if (editorLinkedResources.hasReference(uri)) {
					editorPart = editor;
					// TODO: what if multiple editors in the tracked list
					// need to update?
					// Need a way to know which editor the Preview editor is
					// currently
					// previewing against
					break;
				}
			}
		}
		if (editorPart != null) {
			final IEditorPart finalEditorPart = editorPart;
			UIUtils.getDisplay().asyncExec(new Runnable() {

				public void run() {
					try {
						openPreview(finalEditorPart, finalEditorPart.getEditorInput(), null, false);
					} catch (CoreException e) {
						PreviewPlugin.log(e);
					}
				}
			});
		}
	}

	private void addFilewatchListener(IEditorPart editorPart) {
		IEditorInput editorInput = editorPart.getEditorInput();
		String watchPath = null;
		if (editorInput instanceof IFileEditorInput) {
			watchPath = ((IFileEditorInput) editorInput).getFile().getProject().getLocation().toOSString();
		} else if (editorInput instanceof IPathEditorInput) {
			watchPath = ((IPathEditorInput) editorInput).getPath().toFile().getParentFile().getAbsolutePath();
		} else if (editorInput instanceof IURIEditorInput) {
			try {
				IFileStore fileStore = EFS.getStore(((IURIEditorInput) editorInput).getURI());
				if (fileStore != null) {
					File file = fileStore.toLocalFile(EFS.NONE, null);
					if (file != null) {
						watchPath = file.getParentFile().getAbsolutePath();
					}
				}
			} catch (CoreException e) {
				PreviewPlugin.log(e);
			}
		}
		if (watchPath != null) {
			try {
				int watchId = FileWatcher.addWatch(watchPath, IJNotify.FILE_ANY, true, new JNotifyAdapter() {

					@Override
					public void fileCreated(int wd, String rootPath, String name) {
						fileModified(wd, rootPath, name);
					}

					@Override
					public void fileModified(int wd, String rootPath, String name) {
						URI fileURI = URIUtil.toURI(new Path(rootPath).append(name));
						if (!trackedURIs.contains(fileURI)) {
							checkLinkedEditor(fileURI);
						}
					}
				});
				filewatchIds.put(editorPart, watchId);
			} catch (JNotifyException e) {
				PreviewPlugin.log(e);
			}
		}
	}

	private void removeFilewatchListener(IEditorPart editorPart) {
		try {
			Integer id = filewatchIds.get(editorPart);
			if (id != null) {
				FileWatcher.removeWatch(id);
			}
		} catch (JNotifyException e) {
			PreviewPlugin.log(e);
		}
		filewatchIds.remove(editorPart);
	}

	private void addPartListener() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IPartService partService;
		for (IWorkbenchWindow window : windows) {
			partService = window.getPartService();
			if (partService != null) {
				partService.addPartListener(editorPartListener);
			}
		}
		// Listen on any future windows
		PlatformUI.getWorkbench().addWindowListener(windowListener);
	}

	private void removePartListener() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IPartService partService;
		for (IWorkbenchWindow window : windows) {
			partService = window.getPartService();
			if (partService != null) {
				partService.removePartListener(editorPartListener);
			}
		}
		PlatformUI.getWorkbench().removeWindowListener(windowListener);
	}
}
