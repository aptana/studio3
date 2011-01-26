/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.formatter.nodes.IFormatterNode;
import com.aptana.formatter.nodes.IFormatterTextNode;

@SuppressWarnings("restriction")
public class FormatterUtils
{

	public static boolean isSpace(char c)
	{
		return c == '\t' || c == ' ';
	}

	public static boolean isLineSeparator(char c)
	{
		return c == '\r' || c == '\n';
	}

	public static boolean isNewLine(IFormatterNode node)
	{
		if (node instanceof IFormatterTextNode)
		{
			final IFormatterTextNode textNode = (IFormatterTextNode) node;
			final IFormatterDocument document = node.getDocument();
			int start = textNode.getStartOffset();
			if (start < textNode.getEndOffset())
			{
				if (document.charAt(start) == '\n')
				{
					++start;
				}
				else if (document.charAt(start) == '\r')
				{
					++start;
					if (start < textNode.getEndOffset() && document.charAt(start) == '\n')
					{
						++start;
					}
				}
				else
				{
					return false;
				}
			}
			while (start < textNode.getEndOffset())
			{
				if (!isSpace(document.charAt(start)))
				{
					return false;
				}
				++start;
			}
			return true;
		}
		return false;
	}

	/**
	 * @param node
	 * @return
	 */
	public static boolean isEmptyText(IFormatterNode node)
	{
		if (node instanceof IFormatterTextNode)
		{
			final String text = ((IFormatterTextNode) node).getText();
			for (int i = 0; i < text.length(); ++i)
			{
				char c = text.charAt(i);
				if (!Character.isWhitespace(c))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @since 2.0
	 */
	public static IFormatterNode[] toTextNodeArray(List<IFormatterNode> list)
	{
		if (list != null)
		{
			return list.toArray(new IFormatterNode[list.size()]);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns the editor's tab width as set in the given editor's-specific preferences. In case a value is not found,
	 * or the preferences are null, the workspace settings for the editor's tab-width will be returned.
	 * 
	 * @param preferenceStore
	 *            The editor's preferences store; Null, in case a workspace setting is needed.
	 * @return The editor's tab-width
	 */
	public static int getEditorTabWidth(IPreferenceStore preferenceStore)
	{
		if (preferenceStore == null)
		{
			preferenceStore = EditorsPlugin.getDefault().getPreferenceStore();
		}
		int tabWidth = preferenceStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		if (tabWidth <= 0)
		{
			tabWidth = EditorsPlugin.getDefault().getPreferenceStore().getInt(
					AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		}
		return tabWidth;
	}

	/**
	 * Returns the editor's setting for 'Insert Spaces for Tabs'. This setting exists in the general 'Text Editors'
	 * preference page.
	 * 
	 * @return The value for the generic editor's 'Insert Spaces for Tabs'
	 */
	public static boolean shouldInsertSpacesForTabs()
	{
		return EditorsPlugin.getDefault().getPreferenceStore().getBoolean(
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
	}
}
