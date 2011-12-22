/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.util;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.ui.util.UIUtils;

/**
 * A class of utility methods for interacting with editors
 * 
 * @author Ingo Muschenetz
 */
@SuppressWarnings("restriction")
public class EditorUtil
{
	public static final int DEFAULT_SPACE_INDENT_SIZE = 2;

	/**
	 * Retrieves the indentation settings for the current editor, or falls back on default settings if the current
	 * editor is not available.
	 * 
	 * @return
	 */
	public static int getSpaceIndentSize()
	{
		int spaceIndentSize = 0;

		// Check the preferences of the active editor
		if (UIUtils.getActiveEditor() != null)
		{
			spaceIndentSize = Platform.getPreferencesService().getInt(UIUtils.getActiveEditor().getSite().getId(),
					AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 0, null);
		}

		// Fall back on CommonEditorPlugin or EditorsPlugin values if none are set for current editor
		if (spaceIndentSize == 0 && CommonEditorPlugin.getDefault() != null && EditorsPlugin.getDefault() != null)
		{
			spaceIndentSize = new ChainedPreferenceStore(new IPreferenceStore[] {
					CommonEditorPlugin.getDefault().getPreferenceStore(),
					EditorsPlugin.getDefault().getPreferenceStore() })
					.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		}

		return (spaceIndentSize != 0) ? spaceIndentSize : DEFAULT_SPACE_INDENT_SIZE;
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
		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
		if (uri.getPath() == null || uri.getPath().equals("/") || uri.getPath().trim().equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		IPath path = new Path(uri.getPath());
		return editorReg.getDefaultEditor(path.lastSegment());
	}

	/**
	 * getIndex
	 * 
	 * @param editor
	 * @return
	 */
	public static Index getIndex(AbstractThemeableEditor editor)
	{
		// NOTE: Moved from CommonContentAssistProcessor
		Index result = null;

		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				IFile file = fileEditorInput.getFile();
				IProject project = file.getProject();

				result = IndexManager.getInstance().getIndex(project.getLocationURI());
			}
			else if (editorInput instanceof IURIEditorInput)
			{
				IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
				URI uri = uriEditorInput.getURI();

				// FIXME This file may be a child, we need to check to see if there's an index with a parent URI.
				result = IndexManager.getInstance().getIndex(uri);
			}
		}

		return result;
	}

	/**
	 * getURI
	 * 
	 * @param editor
	 * @return
	 */
	public static URI getURI(AbstractThemeableEditor editor)
	{
		// NOTE: Moved from CommonContentAssistProcessor
		URI result = null;

		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IURIEditorInput)
			{
				IURIEditorInput fileEditorInput = (IURIEditorInput) editorInput;

				result = fileEditorInput.getURI();
			}
		}

		return result;
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
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			if (desc == null)
			{
				return IDE.openEditor(page, uri, IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID, true);
			}
			else
			{
				return IDE.openEditor(page, uri, desc.getId(), true);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), "Error open a file in the editor", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * getProject
	 * 
	 * @param editor
	 * @return
	 */
	public static IProject getProject(AbstractThemeableEditor editor)
	{
		IProject result = null;

		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				IFile file = fileEditorInput.getFile();
				result = file.getProject();
			}
		}

		return result;
	}

	/**
	 * getProjectURI
	 * 
	 * @return
	 */
	public static URI getProjectURI(AbstractThemeableEditor editor)
	{
		IProject project = getProject(editor);
		URI result = null;

		if (project != null)
		{
			result = project.getLocationURI();
		}

		return result;
	}
}
