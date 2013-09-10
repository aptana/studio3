/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.ui.internal.WebPerspectiveFactory;
import com.aptana.ui.preferences.IPreferenceConstants;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.UsagePlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIPlugin extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIPlugin plugin;
	private IPreferenceChangeListener autoBuildListener;

	private final IPerspectiveListener perspectiveListener = new PerspectiveChangeResetListener(
			WebPerspectiveFactory.ID, PLUGIN_ID, IPreferenceConstants.PERSPECTIVE_VERSION,
			WebPerspectiveFactory.VERSION);

	private boolean hasMainWindowActivated = false;

	private final IWindowListener windowListener = new IWindowListener()
	{

		public void windowActivated(IWorkbenchWindow window)
		{
		}

		public void windowClosed(IWorkbenchWindow window)
		{
			window.removePerspectiveListener(perspectiveListener);
		}

		public void windowDeactivated(IWorkbenchWindow window)
		{
		}

		public void windowOpened(IWorkbenchWindow window)
		{
			window.addPerspectiveListener(perspectiveListener);
			if (!hasMainWindowActivated)
			{
				hasMainWindowActivated = true;
				IWorkbenchPage page = window.getActivePage();
				if (page != null)
				{
					perspectiveListener.perspectiveActivated(page, page.getPerspective());
				}
			}
		}
	};

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

		Job job = new Job("Initializing UI Plugin") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				updateInitialPerspectiveVersion();
				addPerspectiveListener();
				addAutoBuildListener();
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();

		// force usage plugin to start
		UsagePlugin.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			removePerspectiveListener();
			removeAutoBuildListener();
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
	public static UIPlugin getDefault()
	{
		return plugin;
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

	public static ImageDescriptor getImageDescriptor(String string)
	{
		if (getDefault().getImageRegistry().getDescriptor(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
			{
				getDefault().getImageRegistry().put(string, id);
			}
		}
		return getDefault().getImageRegistry().getDescriptor(string);
	}

	/**
	 * Adds a listener to changes in the Project->Build Automatically changes.
	 */
	private void addAutoBuildListener()
	{
		IEclipsePreferences node = EclipseUtil.instanceScope().getNode(ResourcesPlugin.PI_RESOURCES);
		autoBuildListener = new AutoBuildListener();
		node.addPreferenceChangeListener(autoBuildListener);
	}

	/**
	 * Remove the auto-build action listener.
	 */
	private void removeAutoBuildListener()
	{
		if (autoBuildListener != null)
		{
			IEclipsePreferences node = EclipseUtil.instanceScope().getNode(ResourcesPlugin.PI_RESOURCES);
			node.removePreferenceChangeListener(autoBuildListener);
			autoBuildListener = null;
		}
	}

	private void addPerspectiveListener()
	{
		try
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null)
			{
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				for (IWorkbenchWindow window : windows)
				{
					window.addPerspectiveListener(perspectiveListener);
				}
				// listens on any future windows
				PlatformUI.getWorkbench().addWindowListener(windowListener);
			}
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
	}

	private void removePerspectiveListener()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
		if (workbench != null)
		{
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			for (IWorkbenchWindow window : windows)
			{
				window.removePerspectiveListener(perspectiveListener);
			}
			PlatformUI.getWorkbench().removeWindowListener(windowListener);
		}
	}

	private void updateInitialPerspectiveVersion()
	{
		// updates the initial stored version so that user won't get a prompt on a new workspace
		boolean hasStartedBefore = Platform.getPreferencesService().getBoolean(PLUGIN_ID,
				IPreferenceConstants.IDE_HAS_LAUNCHED_BEFORE, false, null);
		if (!hasStartedBefore)
		{
			IEclipsePreferences prefs = (EclipseUtil.instanceScope()).getNode(PLUGIN_ID);
			prefs.putInt(IPreferenceConstants.PERSPECTIVE_VERSION, WebPerspectiveFactory.VERSION);
			prefs.putBoolean(IPreferenceConstants.IDE_HAS_LAUNCHED_BEFORE, true);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				IdeLog.logError(getDefault(), Messages.UIPlugin_ERR_FailToSetPref, e);
			}
		}
	}

	/**
	 * A listener for changes in the Project->Build Automatically... action. <br>
	 * The listener will detect when the user turn off the auto-building, and will prompt with a warning.
	 */
	public class AutoBuildListener implements IPreferenceChangeListener
	{

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse
		 * .core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
		 */
		@SuppressWarnings("restriction")
		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (ResourcesPlugin.PREF_AUTO_BUILDING.equals(event.getKey()))
			{
				if ((Boolean.FALSE.toString().equals(event.getNewValue())))
				{
					// APSTUD-4350 - We make sure that the preference change was done through the ToggleAutoBuildAction
					// (e.g. the menu action), or though the Workspace preference page. Any other trigger for that
					// preference change will not show the dialog.
					String buildToggleActionClassName = org.eclipse.ui.internal.ide.actions.ToggleAutoBuildAction.class
							.getCanonicalName();
					String workspacePreferencePage = org.eclipse.ui.internal.ide.dialogs.IDEWorkspacePreferencePage.class
							.getCanonicalName();
					StackTraceElement[] stackTrace = new Exception().getStackTrace();
					for (StackTraceElement element : stackTrace)
					{
						String className = element.getClassName();
						if (className.equals(buildToggleActionClassName) || className.equals(workspacePreferencePage))
						{
							MessageDialog.openWarning(UIUtils.getActiveShell(),
									Messages.UIPlugin_automaticBuildsWarningTitle,
									Messages.UIPlugin_automaticBuildsWarningMessage);
						}
					}
				}
			}
		}
	}
}
