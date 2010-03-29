package com.aptana.git.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.git.ui.internal.GitColors;

/**
 * The activator class controls the plug-in life cycle
 */
public class GitUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	private static final String PLUGIN_ID = "com.aptana.git.ui"; //$NON-NLS-1$

	// The shared instance
	private static GitUIPlugin plugin;

	private IPreferenceChangeListener themeChangeListener;

	/**
	 * The constructor
	 */
	public GitUIPlugin()
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
		// Listen for theme changes and force the quick diff colors to match our git diff colors.
		themeChangeListener = new IPreferenceChangeListener()
		{

			@SuppressWarnings("nls")
			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.ui.editors"); //$NON-NLS-1$
					// Quick Diff colors
					prefs.put("changeIndicationColor", toString(GitColors.greenBG().getRGB()));
					prefs.put("additionIndicationColor", toString(GitColors.greenBG().getRGB()));
					prefs.put("deletionIndicationColor", toString(GitColors.redBG().getRGB()));
					try
					{
						prefs.flush();
					}
					catch (BackingStoreException e)
					{
						GitUIPlugin.logError(e.getMessage(), e);
					}
				}
			}

			private String toString(RGB selection)
			{
				StringBuilder builder = new StringBuilder();
				builder.append(selection.red).append(',').append(selection.green).append(',').append(selection.blue);
				return builder.toString();
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(themeChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (themeChangeListener != null)
			{
				new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
						themeChangeListener);
			}
			themeChangeListener = null;
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GitUIPlugin getDefault()
	{
		return plugin;
	}

	public static void logInfo(String string)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, getPluginId(), string));
	}

	public static void trace(String string)
	{
		getDefault().getLog().log(new Status(IStatus.OK, getPluginId(), string));
	}

	public static String getPluginId()
	{
		return PLUGIN_ID;
	}

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), msg, e));
	}

	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	public static void logWarning(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, getPluginId(), msg));
	}

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(getPluginId(), string);
			if (id != null)
				getDefault().getImageRegistry().put(string, id);
		}
		return getDefault().getImageRegistry().get(string);
	}

}
