package com.aptana.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.ui.preferences.IPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIPlugin plugin;

	/**
	 * The constructor
	 */
	public UIPlugin()
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
		// TODO remove when we ship the 3.6-based standalone
		showUpdateWarning();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UIPlugin getDefault()
	{
		return plugin;
	}

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * logInfo
	 * 
	 * @param string
	 */
	public static void logInfo(String string)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, string));
	}

	/**
	 * logWarning
	 * 
	 * @param msg
	 */
	public static void logWarning(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg));
	}

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
			{
				getDefault().getImageRegistry().put(string, id);
			}
		}
		return getDefault().getImageRegistry().get(string);
	}

	private void showUpdateWarning()
	{
		// in standalone, warns user that the next update will be 3.6 based and will require a reinstall
		if (EclipseUtil.isStandalone()
				&& !Platform.inDevelopmentMode()
				&& !Platform.getPreferencesService().getBoolean(UIPlugin.PLUGIN_ID,
						IPreferenceConstants.ECLIPSE_36_UPDATE_WARNING_SHOWN, false, null))
		{
			// shows a dialog to warn user this will be the final 3.5-based update
			MessageDialog.openWarning(UIUtils.getActiveShell(), Messages.UIPlugin_UpdateWarning_Title,
					Messages.UIPlugin_UpdateWarning_Message);
			// shows the dialog once per installation
			IEclipsePreferences prefs = (new ConfigurationScope()).getNode(UIPlugin.PLUGIN_ID);
			prefs.putBoolean(IPreferenceConstants.ECLIPSE_36_UPDATE_WARNING_SHOWN, true);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
			}
		}
	}
}
