package com.aptana.theme;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.theme.internal.InvasiveThemeHijacker;
import com.aptana.theme.internal.ThemeManager;
import com.aptana.theme.internal.fontloader.EditorFontOverride;

/**
 * The activator class controls the plug-in life cycle
 */
public class ThemePlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.theme"; //$NON-NLS-1$

	// The shared instance
	private static ThemePlugin plugin;

	private InvasiveThemeHijacker themeHijacker;
	private ColorManager fColorManager;

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

		themeHijacker = new InvasiveThemeHijacker();
		themeHijacker.schedule();

		new EditorFontOverride().schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(PLUGIN_ID);
			prefs.removePreferenceChangeListener(themeHijacker);

			if (fColorManager != null)
			{
				fColorManager.dispose();
			}
		}
		finally
		{
			themeHijacker = null;
			fColorManager = null;
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
	public ColorManager getColorManager()
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

	public static void logError(Exception e)
	{
		logError(e.getMessage(), e);
	}

	public static void logError(String string, Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, string, e));
	}

	public static void logWarning(String message)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, null));
	}
}
