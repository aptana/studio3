/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.graphics.Color;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.hover.AbstractDocumentationHover;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public abstract class CommonTextHover extends AbstractDocumentationHover
{
	private static ThemeListener themeListener = new ThemeListener();

	/**
	 * Checks the common editor plugin to see if the user has enabled hovers on content assist
	 * 
	 * @return <code>true</code>, if hover is enabled; <code>false</code>, otherwise.
	 */
	public Boolean isHoverEnabled()
	{
		IScopeContext[] scopes = new IScopeContext[] { EclipseUtil.instanceScope(), EclipseUtil.defaultScope() };
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

	// Theme listener that caches the colors.
	// This listener is defined as static, and never disposed, as hovers popup are very common.
	public static class ThemeListener implements IPreferenceChangeListener
	{
		protected Color bgColor;
		protected Color fgColor;

		/**/ThemeListener()
		{
			getThemeColors();
			EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(this);
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
			ColorManager colorManager = ThemePlugin.getDefault().getColorManager();
			IThemeManager themeManager = ThemePlugin.getDefault().getThemeManager();
			Theme currentTheme = themeManager.getCurrentTheme();
			bgColor = colorManager.getColor(currentTheme.getBackground());
			fgColor = colorManager.getColor(currentTheme.getForeground());
		}
	}
}
