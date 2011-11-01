/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.osgi.framework.BundleContext;

import com.aptana.core.util.EclipseUtil;
import com.aptana.theme.internal.ControlThemerFactory;
import com.aptana.theme.internal.InvasiveThemeHijacker;
import com.aptana.theme.internal.ThemeManager;
import com.aptana.theme.preferences.IPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class ThemePlugin extends AbstractUIPlugin
{

	/**
	 * This listens for changes to the editor FG/BG/line highlight/selection colors from Eclipse's pref page. if
	 * invasive theming is on, we'll apply changes onto our current Aptana theme.
	 * 
	 * @author cwilliams
	 */
	private final class EditorColorSyncher implements IPreferenceChangeListener
	{
		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (!invasiveThemesEnabled())
			{
				return;
			}
			if (AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND.equals(event.getKey()))
			{
				RGB value = StringConverter.asRGB((String) event.getNewValue());
				RGB existing = getThemeManager().getCurrentTheme().getForeground();
				if (!value.equals(existing))
				{
					getThemeManager().getCurrentTheme().updateFG(value);
				}
			}
			else if (AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND.equals(event.getKey()))
			{
				RGB value = StringConverter.asRGB((String) event.getNewValue());
				RGB existing = getThemeManager().getCurrentTheme().getBackground();
				if (!value.equals(existing))
				{
					getThemeManager().getCurrentTheme().updateBG(value);
				}
			}
			else if (AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND.equals(event.getKey()))
			{
				RGB value = StringConverter.asRGB((String) event.getNewValue());
				RGB existing = getThemeManager().getCurrentTheme().getSelectionAgainstBG();
				if (!value.equals(existing))
				{
					getThemeManager().getCurrentTheme().updateSelection(value);
				}
			}
			else if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR.equals(event.getKey()))
			{
				RGB value = StringConverter.asRGB((String) event.getNewValue());
				RGB existing = getThemeManager().getCurrentTheme().getLineHighlightAgainstBG();
				if (!value.equals(existing))
				{
					getThemeManager().getCurrentTheme().updateLineHighlight(value);
				}
			}
		}
	}

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.theme"; //$NON-NLS-1$

	// The shared instance
	private static ThemePlugin plugin;

	private InvasiveThemeHijacker themeHijacker;
	private ColorManager fColorManager;

	private IControlThemerFactory fControlThemerFactory;

	// Store latest value of whether invasive theme is on so we don't need to query platform prefs every time.
	private Boolean fInvasiveThemesEnabled;
	private IPreferenceChangeListener fThemeChangeListener;
	private IPreferenceChangeListener fEclipseColorsListener;

	/**
	 * The constructor
	 */
	public ThemePlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		// Listen to when invasive themes is turned on or off and cache the value for perf sake
		fThemeChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IPreferenceConstants.INVASIVE_THEMES))
				{
					fInvasiveThemesEnabled = Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID,
							IPreferenceConstants.INVASIVE_THEMES, false, null);
				}
			}
		};
		EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
		fInvasiveThemesEnabled = Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID,
				IPreferenceConstants.INVASIVE_THEMES, false, null);

		themeHijacker = new InvasiveThemeHijacker();
		themeHijacker.apply();

		// Listen for changes to eclipse editor colors and synch them back to our theme
		fEclipseColorsListener = new EditorColorSyncher();
		EclipseUtil.instanceScope().getNode("org.eclipse.ui.editors") //$NON-NLS-1$
				.addPreferenceChangeListener(fEclipseColorsListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (fThemeChangeListener != null)
			{
				EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID)
						.removePreferenceChangeListener(fThemeChangeListener);
				fThemeChangeListener = null;
			}

			if (fEclipseColorsListener != null)
			{
				EclipseUtil.instanceScope().getNode("org.eclipse.ui.editors") //$NON-NLS-1$
						.removePreferenceChangeListener(fEclipseColorsListener);
				fEclipseColorsListener = null;
			}

			if (themeHijacker != null)
			{
				themeHijacker.dispose();
			}

			if (fColorManager != null)
			{
				fColorManager.dispose();
			}

			if (fControlThemerFactory != null)
			{
				fControlThemerFactory.dispose();
			}
		}
		finally
		{
			themeHijacker = null;
			fColorManager = null;
			fControlThemerFactory = null;
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ThemePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * getColorManager
	 * 
	 * @return
	 */
	public synchronized ColorManager getColorManager()
	{
		if (this.fColorManager == null)
		{
			this.fColorManager = new ColorManager();
		}

		return this.fColorManager;
	}

	public IThemeManager getThemeManager()
	{
		return ThemeManager.instance();
	}

	public synchronized IControlThemerFactory getControlThemerFactory()
	{
		if (fControlThemerFactory == null)
		{
			fControlThemerFactory = new ControlThemerFactory();
		}
		return fControlThemerFactory;
	}

	public static synchronized boolean invasiveThemesEnabled()
	{
		return getDefault().fInvasiveThemesEnabled;
	}
}
