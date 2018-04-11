/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.net.URI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.hover.AbstractDocumentationHover;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public abstract class CommonTextHover extends AbstractDocumentationHover
{
	protected static final String BASE_HELP_DOCS_URL = "/com.aptana.documentation/html/reference/api/"; //$NON-NLS-1$
	private static ThemeListener themeListener = new ThemeListener();

	/**
	 * Checks the common editor plugin to see if the user has enabled hovers on content assist
	 * 
	 * @return <code>true</code>, if hover is enabled; <code>false</code>, otherwise.
	 */
	public Boolean isHoverEnabled()
	{
		IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.CONTENT_ASSIST_HOVER, true, scopes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getForegroundColor()
	 */
	@Override
	protected Color getForegroundColor()
	{
		return themeListener.fgColor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getBackgroundColor()
	 */
	@Override
	protected Color getBackgroundColor()
	{
		return themeListener.bgColor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getBorderColor()
	 */
	@Override
	protected Color getBorderColor()
	{
		return themeListener.borderColor;
	}

	/**
	 * getActiveNode
	 * 
	 * @param textViewer
	 * @param offset
	 * @return
	 */
	protected IParseNode getActiveNode(ITextViewer textViewer, int offset)
	{
		IParseNode result = null;

		if (this.isHoverEnabled())
		{
			AbstractThemeableEditor editor = this.getEditor(textViewer);
			IParseNode ast = editor.getAST();

			if (ast != null)
			{
				result = ast.getNodeAtOffset(offset);

				// We won't get a current node if the cursor is outside of the positions
				// recorded by the AST
				if (result == null)
				{
					if (offset < ast.getStartingOffset())
					{
						result = ast.getNodeAtOffset(ast.getStartingOffset());
					}
					else if (ast.getEndingOffset() < offset)
					{
						result = ast.getNodeAtOffset(ast.getEndingOffset());
					}
				}
			}
		}

		return result;
	}

	/**
	 * getEditor
	 * 
	 * @param textViewer
	 * @return
	 */
	protected AbstractThemeableEditor getEditor(ITextViewer textViewer)
	{
		AbstractThemeableEditor result = null;

		if (textViewer instanceof IAdaptable)
		{
			result = (AbstractThemeableEditor) ((IAdaptable) textViewer).getAdapter(AbstractThemeableEditor.class);
		}

		return result;
	}

	/**
	 * getEditorURI
	 * 
	 * @param textViewer
	 * @return
	 */
	protected URI getEditorURI(IEditorPart editorPart)
	{
		AbstractThemeableEditor editor = (AbstractThemeableEditor) editorPart;
		return EditorUtil.getURI(editor);
	}

	/**
	 * getIndex
	 * 
	 * @param editorPart
	 * @return
	 */
	protected Index getIndex(IEditorPart editorPart)
	{
		AbstractThemeableEditor editor = (AbstractThemeableEditor) editorPart;
		return EditorUtil.getIndex(editor);
	}

	// Theme listener that caches the colors.
	// This listener is defined as static, and never disposed, as hovers popup are very common.
	private static class ThemeListener implements IPreferenceChangeListener
	{
		protected Color borderColor;
		protected Color bgColor;
		protected Color fgColor;

		ThemeListener()
		{
			getThemeColors();
			InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(this);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse
		 * .core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
		 */
		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (event.getKey().equals(IThemeManager.THEME_CHANGED))
			{
				getThemeColors();
			}
		}

		protected void getThemeColors()
		{
			ThemePlugin themePlugin = ThemePlugin.getDefault();
			ColorManager colorManager = themePlugin.getColorManager();
			IThemeManager themeManager = themePlugin.getThemeManager();
			Theme currentTheme = themeManager.getCurrentTheme();
			bgColor = colorManager.getColor(currentTheme.getBackground());
			fgColor = colorManager.getColor(currentTheme.getForeground());
			borderColor = colorManager.getColor(currentTheme.getSelectionAgainstBG());
		}
	}
}
