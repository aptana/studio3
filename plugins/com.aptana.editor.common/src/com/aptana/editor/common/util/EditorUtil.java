/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.util;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.editors.text.ILocationProviderExtension;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * A class of utility methods for interacting with editors
 * 
 * @author Ingo Muschenetz
 */
@SuppressWarnings("restriction")
public class EditorUtil
{

	protected static final int DEFAULT_SPACE_INDENT_SIZE = 2;

	/**
	 * Retrieves the indentation settings for the current editor, or falls back on default settings if the current
	 * editor is not available.
	 */
	public static int getSpaceIndentSize()
	{
		IEditorPart activeEditor = UIUtils.getActiveEditor();
		if (activeEditor != null)
		{
			return getSpaceIndentSize(activeEditor.getSite().getId());
		}
		return getSpaceIndentSize(null);
	}

	/**
	 * Retrieves the indentation settings from the given preferences qualifier, or falls back on default settings if the
	 * given qualifier is null, or the value of the indent-size is smaller than 1.
	 */
	public static int getSpaceIndentSize(String preferencesQualifier)
	{
		int spaceIndentSize = 0;
		if (preferencesQualifier != null)
		{
			spaceIndentSize = Platform.getPreferencesService().getInt(preferencesQualifier,
					AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 0, null);
		}
		// Fall back on CommonEditorPlugin or EditorsPlugin values if none are set for current editor
		return (spaceIndentSize > 0) ? spaceIndentSize : getDefaultSpaceIndentSize(preferencesQualifier);
	}

	public static int getDefaultSpaceIndentSize(String preferencesQualifier)
	{
		int spaceIndentSize = 0;
		if (CommonEditorPlugin.getDefault() != null && EditorsPlugin.getDefault() != null)
		{
			spaceIndentSize = new ChainedPreferenceStore(new IPreferenceStore[] {
					CommonEditorPlugin.getDefault().getPreferenceStore(),
					EditorsPlugin.getDefault().getPreferenceStore() })
					.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		}
		return (spaceIndentSize > 0) ? spaceIndentSize : DEFAULT_SPACE_INDENT_SIZE;
	}

	/**
	 * Converts current indent string to a specific number of spaces or tabs
	 * 
	 * @param indent
	 * @param indentSize
	 * @param useTabs
	 * @return
	 */
	public static String convertIndent(String indent, int indentSize, boolean useTabs)
	{
		if (indent == null)
		{
			return StringUtil.EMPTY;
		}

		if (useTabs && indent.contains(" ")) //$NON-NLS-1$
		{
			int i;
			String newIndent = ""; //$NON-NLS-1$
			int spacesCount = indent.replaceAll("\t", "").length(); //$NON-NLS-1$ //$NON-NLS-2$
			// Add tabs based on previous number of tabs, and total number of spaces (if they can be converted to the
			// tab equivalent)
			for (i = 0; i < (indent.length() - spacesCount) + (spacesCount / indentSize); i++)
			{
				newIndent += '\t';
			}
			// Add back remaining spaces
			for (i = 0; i < spacesCount % indentSize; i++)
			{
				newIndent += ' ';
			}
			return newIndent;
		}
		if (!useTabs && indent.contains("\t")) //$NON-NLS-1$
		{
			String newIndent = ""; //$NON-NLS-1$
			int tabCount = indent.replaceAll(" ", "").length(); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < (indent.length() - tabCount) + (tabCount * indentSize); i++)
			{
				newIndent += " "; //$NON-NLS-1$
			}
			return newIndent;
		}
		return indent;
	}

	/**
	 * Returns the editor descriptor for the given URI. The editor descriptor is computed by the last segment of the URI
	 * (the file name).
	 * 
	 * @param uri
	 *            A file URI
	 * @return the descriptor of the default editor, or null if not found
	 */
	public static IEditorDescriptor getEditorDescriptor(URI uri)
	{
		// NOTE: Moved from PHP's EditorUtils
		String uriPath = uri.getPath();
		if (StringUtil.isEmpty(uriPath) || uriPath.equals("/")) //$NON-NLS-1$
		{
			return null;
		}
		IPath path = new Path(uriPath);
		return PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(path.lastSegment());
	}

	/**
	 * Gets the indexing associated with the editor.
	 * 
	 * @param editor
	 * @return
	 */
	public static Index getIndex(AbstractThemeableEditor editor)
	{
		// NOTE: Moved from CommonContentAssistProcessor
		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				IFile file = fileEditorInput.getFile();

				return getIndexManager().getIndex(file.getProject().getLocationURI());
			}
			if (editorInput instanceof IURIEditorInput)
			{
				IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;

				// FIXME This file may be a child, we need to check to see if there's an index with a parent URI.
				return getIndexManager().getIndex(uriEditorInput.getURI());
			}
			if (editorInput instanceof IPathEditorInput)
			{
				IPathEditorInput pathEditorInput = (IPathEditorInput) editorInput;

				// FIXME This file may be a child, we need to check to see if there's an index with a parent URI.
				return getIndexManager().getIndex(URIUtil.toURI(pathEditorInput.getPath()));
			}
		}

		return null;
	}

	protected static IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/**
	 * Gets the URI associated with the editor.
	 * 
	 * @param editor
	 * @return
	 */
	public static URI getURI(IEditorPart editor)
	{
		// NOTE: Moved from CommonContentAssistProcessor
		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IURIEditorInput)
			{
				IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
				return uriEditorInput.getURI();
			}
			if (editorInput instanceof IPathEditorInput)
			{
				IPathEditorInput pathEditorInput = (IPathEditorInput) editorInput;
				return URIUtil.toURI(pathEditorInput.getPath());
			}
			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				return fileEditorInput.getFile().getLocationURI();
			}
			try
			{
				if (editorInput instanceof IStorageEditorInput)
				{
					IStorageEditorInput storageEditorInput = (IStorageEditorInput) editorInput;
					IStorage storage = storageEditorInput.getStorage();
					if (storage != null)
					{
						IPath path = storage.getFullPath();
						if (path != null)
						{
							return URIUtil.toURI(path);
						}
					}
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}
			if (editorInput instanceof ILocationProviderExtension)
			{
				ILocationProviderExtension lpe = (ILocationProviderExtension) editorInput;
				return lpe.getURI(null);
			}
			if (editorInput instanceof ILocationProvider)
			{
				ILocationProvider lp = (ILocationProvider) editorInput;
				return URIUtil.toURI(lp.getPath(null));
			}
		}

		return null;
	}

	public static String getFileName(IEditorPart editor)
	{
		URI uri = getURI(editor);
		return com.aptana.core.util.URIUtil.getFileName(uri);
	}

	/**
	 * Open a file in an editor and return the opened editor part.<br>
	 * This method will try to open the file in an internal editor, unless there is no editor descriptor assigned to
	 * that file type.
	 * 
	 * @param file
	 * @return The {@link IEditorPart} that was created when the file was opened; Return null in case of an error
	 */
	public static IEditorPart openInEditor(File file)
	{
		// NOTE: Moved from PHP's EditorUtils
		if (file == null)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(),
					"Error open a file in the editor", new IllegalArgumentException("file is null")); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		try
		{
			URI uri = file.toURI();
			IEditorDescriptor desc = getEditorDescriptor(uri);
			String editorId = (desc == null) ? IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID : desc.getId();
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			return IDE.openEditor(page, uri, editorId, true);
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), "Error open a file in the editor", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Returns the project that the file for the editor belongs.
	 * 
	 * @param editor
	 *            a file editor
	 * @return the project the editor belongs
	 */
	public static IProject getProject(AbstractThemeableEditor editor)
	{
		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				return fileEditorInput.getFile().getProject();
			}
		}

		return null;
	}

	/**
	 * Gets the project URI associated with the editor.
	 * 
	 * @return the project URI
	 */
	public static URI getProjectURI(AbstractThemeableEditor editor)
	{
		IProject project = getProject(editor);
		return (project == null) ? null : project.getLocationURI();
	}

	/**
	 * Finds the editor for the specified file in the specified project and prompts a save if the editor is dirty. If
	 * the fileName is empty, it will save all dirty project files
	 * 
	 * @param project
	 *            A non-null project reference.
	 * @param fileName
	 *            A file name (can be null)
	 * @param promptQuestion
	 * @return
	 */
	public static boolean verifySaveEditor(final IProject project, final String fileName, final String promptQuestion)
	{
		if (project == null)
		{
			throw new IllegalArgumentException("verifySaveEditor - Project cannot be null"); //$NON-NLS-1$
		}
		final boolean[] result = new boolean[] { true };
		UIUtils.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				IEditorPart[] dirtyEditors = UIUtils.getDirtyEditors();
				List<IEditorPart> applicableEditors = new ArrayList<IEditorPart>();
				if (dirtyEditors != null && dirtyEditors.length > 0)
				{
					IFile projectFile = null;

					if (!StringUtil.isEmpty(fileName))
					{
						projectFile = project.getFile(fileName);

						// If the file doesn't exist, then it doesn't need to be saved
						if (projectFile == null || !projectFile.exists())
						{
							return;
						}
					}

					for (IEditorPart editor : dirtyEditors)
					{
						IFile file = (IFile) (editor.getEditorInput().getAdapter(IFile.class));
						// We only look at dirty editors from the given project
						if (file.getProject().equals(project))
						{
							if (file != null && (projectFile == null || file.equals(projectFile)))
							{
								applicableEditors.add(editor);
							}
						}
					}

					if (!CollectionsUtil.isEmpty(applicableEditors))
					{
						// prompt for save
						if (MessageDialog.openQuestion(UIUtils.getActiveShell(),
								Messages.EditorUtil_VerfiySavePromptTitle_lbl, promptQuestion))
						{
							for (IEditorPart editor : applicableEditors)
							{
								editor.doSave(new NullProgressMonitor());
							}
						}
						else
						{
							result[0] = false;
						}
					}
				}
			}
		});

		return result[0];
	}

	/**
	 * Returns the resource file that the editor belongs to.
	 * 
	 * @param editor
	 * @return
	 */
	public static IFile getEditorFile(AbstractThemeableEditor editor)
	{
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			return fileEditorInput.getFile();
		}
		else if (editorInput instanceof FileStoreEditorInput)
		{
			FileStoreEditorInput input = (FileStoreEditorInput) editorInput;
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(input.getURI());
			if (files != null && files.length > 0)
			{
				return files[0];
			}
		}
		return null;
	}
}
