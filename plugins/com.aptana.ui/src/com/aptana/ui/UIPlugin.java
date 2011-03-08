/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ui.internal.WebPerspectiveFactory;
import com.aptana.ui.preferences.IPreferenceConstants;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIPlugin plugin;

	private final IPerspectiveListener perspectiveListener = new IPerspectiveListener()
	{

		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
		{
			if (WebPerspectiveFactory.ID.equals(perspective.getId()))
			{
				int version = Platform.getPreferencesService().getInt(PLUGIN_ID,
						IPreferenceConstants.PERSPECTIVE_VERSION, 0, null);
				if (WebPerspectiveFactory.VERSION > version)
				{
					resetPerspective(page);
					// we will only ask once regardless if user chose to update the perspective
					IEclipsePreferences prefs = (new InstanceScope()).getNode(PLUGIN_ID);
					prefs.putInt(IPreferenceConstants.PERSPECTIVE_VERSION, WebPerspectiveFactory.VERSION);
					try
					{
						prefs.flush();
					}
					catch (BackingStoreException e)
					{
						// ignores the exception
					}
				}
			}
		}

		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
		{
		}

		private void resetPerspective(final IWorkbenchPage page)
		{
			UIJob job = new UIJob("Resetting Studio perspective...") //$NON-NLS-1$
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (MessageDialog.openQuestion(UIUtils.getActiveShell(),
							com.aptana.ui.Messages.UIPlugin_ResetPerspective_Title,
							com.aptana.ui.Messages.UIPlugin_ResetPerspective_Description))
					{
						page.resetPerspective();
					}
					return Status.OK_STATUS;
				}
			};
			job.setSystem(true);
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
		}
	};

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
		updateInitialPerspectiveVersion();
		addPerspectiveListener();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		removePerspectiveListener();
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

	public static void log(Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg)
	{
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e)
	{
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
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

	private void addPerspectiveListener()
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
				window.addPerspectiveListener(perspectiveListener);
			}
			// listens on any future windows
			PlatformUI.getWorkbench().addWindowListener(windowListener);
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
			IEclipsePreferences prefs = (new InstanceScope()).getNode(PLUGIN_ID);
			prefs.putInt(IPreferenceConstants.PERSPECTIVE_VERSION, WebPerspectiveFactory.VERSION);
			prefs.putBoolean(IPreferenceConstants.IDE_HAS_LAUNCHED_BEFORE, true);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				log(new Status(IStatus.ERROR, PLUGIN_ID, Messages.UIPlugin_ERR_FailToSetPref, e));
			}
		}
	}
}
