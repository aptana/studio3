/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.theme.internal.ControlThemerFactory;
import com.aptana.theme.internal.InvasiveThemeHijacker;
import com.aptana.theme.internal.ThemeManager;
import com.aptana.theme.preferences.IPreferenceConstants;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class ThemePlugin extends AbstractUIPlugin
{

	public static final String IBEAM_BLACK = "/icons/ibeam-black.gif"; //$NON-NLS-1$
	public static final String IBEAM_WHITE = "/icons/ibeam-white.gif"; //$NON-NLS-1$

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
			if (!applyToAllEditors())
			{
				return;
			}
			if (AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND.equals(event.getKey()))
			{
				String newValue = (String) event.getNewValue();
				if (newValue != null)
				{
					RGB value = StringConverter.asRGB(newValue);
					RGB existing = getCurrentTheme().getForeground();
					if (!value.equals(existing))
					{
						getCurrentTheme().updateFG(value);
					}
				}
			}
			else if (AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND.equals(event.getKey()))
			{
				String newValue = (String) event.getNewValue();
				if (newValue != null)
				{
					RGB value = StringConverter.asRGB(newValue);
					RGB existing = getCurrentTheme().getBackground();
					if (!value.equals(existing))
					{
						getCurrentTheme().updateBG(value);
					}
				}
			}
			else if (AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND.equals(event.getKey()))
			{
				String newValue = (String) event.getNewValue();
				if (newValue != null)
				{
					RGB value = StringConverter.asRGB(newValue);
					RGB existing = getCurrentTheme().getSelectionAgainstBG();
					if (!value.equals(existing))
					{
						getCurrentTheme().updateSelection(value);
					}
				}
			}
			else if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR.equals(event.getKey()))
			{
				String newValue = (String) event.getNewValue();
				if (newValue != null)
				{
					RGB value = StringConverter.asRGB(newValue);
					RGB existing = getCurrentTheme().getLineHighlightAgainstBG();
					if (!value.equals(existing))
					{
						getCurrentTheme().updateLineHighlight(value);
					}
				}
			}
		}

		protected Theme getCurrentTheme()
		{
			return getThemeManager().getCurrentTheme();
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
	private Boolean fApplyThemeToAllEditors = false;
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

		Job job = new Job("Initializing theme plugin...") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				// Listen to when invasive themes is turned on or off and cache the value for perf sake
				fThemeChangeListener = new IPreferenceChangeListener()
				{
					public void preferenceChange(PreferenceChangeEvent event)
					{
						if (IPreferenceConstants.APPLY_TO_ALL_EDITORS.equals(event.getKey()))
						{
							fApplyThemeToAllEditors = Platform.getPreferencesService().getBoolean(
									ThemePlugin.PLUGIN_ID, IPreferenceConstants.APPLY_TO_ALL_EDITORS, false, null);
						}
					}
				};
				EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID)
						.addPreferenceChangeListener(fThemeChangeListener);

				fApplyThemeToAllEditors = Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID,
						IPreferenceConstants.APPLY_TO_ALL_EDITORS, false, null);

				themeHijacker = new InvasiveThemeHijacker();
				themeHijacker.apply();

				// Listen for changes to eclipse editor colors and synch them back to our theme
				fEclipseColorsListener = new EditorColorSyncher();
				EclipseUtil.instanceScope().getNode("org.eclipse.ui.editors") //$NON-NLS-1$
						.addPreferenceChangeListener(fEclipseColorsListener);

				revertConsoleColors();

				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/**
	 * Reverts the console colors back to defaults.
	 * 
	 * @deprecated This is a one-time migration to revert back to defaults from invasive theming. This code should be
	 *             removed in the next major revision of Studio (3.5? TISTUD 3.3)
	 */
	private void revertConsoleColors()
	{
		boolean reverted = Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID, "reverted_console",
				false, null);
		if (reverted)
		{
			// we've already reverted them
			return;
		}

		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode("org.eclipse.debug.ui"); //$NON-NLS-1$

				prefs.remove("org.eclipse.debug.ui.errorColor"); //$NON-NLS-1$
				prefs.remove("org.eclipse.debug.ui.outColor"); //$NON-NLS-1$
				prefs.remove("org.eclipse.debug.ui.inColor"); //$NON-NLS-1$
				prefs.remove("org.eclipse.debug.ui.consoleBackground"); //$NON-NLS-1$
				prefs.remove("org.eclipse.debug.ui.PREF_CHANGED_VALUE_BACKGROUND"); //$NON-NLS-1$

				try
				{
					prefs.flush();

					// Store that we've reverted the console
					IEclipsePreferences themePrefs = EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID);
					themePrefs.putBoolean("reverted_console", true); //$NON-NLS-1$
					try
					{
						themePrefs.flush();
					}
					catch (BackingStoreException e)
					{
						IdeLog.logError(ThemePlugin.getDefault(), e);
					}
				}
				catch (BackingStoreException e)
				{
					IdeLog.logError(ThemePlugin.getDefault(), e);
				}
			}
		});
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

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		reg.put(IBEAM_BLACK, imageDescriptorFromPlugin(PLUGIN_ID, IBEAM_BLACK));
		reg.put(IBEAM_WHITE, imageDescriptorFromPlugin(PLUGIN_ID, IBEAM_WHITE));
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

	/**
	 * @deprecated This option is being removed!
	 */
	public static synchronized boolean applyToViews()
	{
		return false;
	}

	public static synchronized boolean applyToAllEditors()
	{
		return (plugin == null) ? false : plugin.fApplyThemeToAllEditors;
	}
}
