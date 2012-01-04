/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
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
	 */
	public static int getSpaceIndentSize()
	{
		if (UIUtils.getActiveEditor() != null)
		{
			return getSpaceIndentSize(UIUtils.getActiveEditor().getSite().getId());
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
		if (spaceIndentSize > 0)
		{
			return spaceIndentSize;
		}
		// Fall back on CommonEditorPlugin or EditorsPlugin values if none are set for current editor
		return getDefaultSpaceIndentSize(preferencesQualifier);
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

}
