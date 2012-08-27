/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.js.debug.ui.internal;

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchHistory;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationDelegate;
import com.aptana.js.debug.core.JSLaunchConfigurationDelegate.Listener;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.ui.PopupSchedulingRule;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public final class LaunchConfigurationsHelper
{

	private LaunchConfigurationsHelper()
	{
	}

	public static void doCheckDefaultLaunchConfigurations()
	{
		JSLaunchConfigurationDelegate.setCheckFirefoxLocationListener(new Listener()
		{

			public String checkFirefoxLocation()
			{
				return showBrowserNotFoundDialog(true);
			}
		});

		UIJob job = new UIJob("Checking default launch configuration") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				new LaunchConfigurationsHelper().checkDefaultLaunchConfiguration();
				WorkbenchCloseListener.init();
				return Status.OK_STATUS;
			}

		};
		job.setRule(PopupSchedulingRule.INSTANCE);
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	private void checkDefaultLaunchConfiguration()
	{
		Stack<ILaunchConfiguration> defaultConfigurations = new Stack<ILaunchConfiguration>();
		ILaunchConfiguration configuration;
		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		ILaunchConfiguration[] history = manager.getLaunchHistory(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP).getHistory();

		/* Firefox */
		configuration = getOrCreateDefaultLaunchConfiguration(JSLaunchConfigurationHelper.FIREFOX);
		if (configuration != null)
		{
			defaultConfigurations.push(configuration);
		}

		/* IE */
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			configuration = getOrCreateDefaultLaunchConfiguration(JSLaunchConfigurationHelper.INTERNET_EXPLORER);
			if (configuration != null)
			{
				defaultConfigurations.push(configuration);
			}
		}

		for (ILaunchConfiguration i : history)
		{
			for (Iterator<ILaunchConfiguration> j = defaultConfigurations.iterator(); j.hasNext();)
			{
				if (i.equals(j.next()))
				{
					j.remove();
					break;
				}
			}
		}
		while (!defaultConfigurations.empty())
		{
			configuration = (ILaunchConfiguration) defaultConfigurations.pop();
			setRecentLaunchHistory(IDebugUIConstants.ID_RUN_LAUNCH_GROUP, configuration);
			setRecentLaunchHistory(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP, configuration);
		}
	}

	private void setRecentLaunchHistory(String groupId, final ILaunchConfiguration configuration)
	{
		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		LaunchHistory history = manager.getLaunchHistory(groupId);

		/* Launch history hack */
		history.launchAdded(new ILaunch()
		{

			public ILaunchConfiguration getLaunchConfiguration()
			{
				return configuration;
			}

			/* All other methods are stubs */
			public Object[] getChildren()
			{
				return null;
			}

			public IDebugTarget getDebugTarget()
			{
				return null;
			}

			public IProcess[] getProcesses()
			{
				return null;
			}

			public IDebugTarget[] getDebugTargets()
			{
				return null;
			}

			public void addDebugTarget(IDebugTarget target)
			{
			}

			public void removeDebugTarget(IDebugTarget target)
			{
			}

			public void addProcess(IProcess process)
			{
			}

			public void removeProcess(IProcess process)
			{
			}

			public ISourceLocator getSourceLocator()
			{
				return null;
			}

			public void setSourceLocator(ISourceLocator sourceLocator)
			{
			}

			public String getLaunchMode()
			{
				return null;
			}

			public void setAttribute(String key, String value)
			{
			}

			public String getAttribute(String key)
			{
				return null;
			}

			public boolean hasChildren()
			{
				return false;
			}

			public boolean canTerminate()
			{
				return false;
			}

			public boolean isTerminated()
			{
				return false;
			}

			public void terminate() throws DebugException
			{
			}

			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter)
			{
				return null;
			}
		});

	}

	private ILaunchConfiguration getOrCreateDefaultLaunchConfiguration(String nature)
	{
		ILaunchConfigurationType configType = getLaunchConfigType();
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		try
		{
			ILaunchConfiguration[] configs = manager.getLaunchConfigurations(configType);
			for (ILaunchConfiguration config : configs)
			{
				if (nature.equals(config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_NATURE,
						StringUtil.EMPTY)))
				{
					if (JSLaunchConfigurationHelper.FIREFOX.equals(nature))
					{
						String browserExecutable = config.getAttribute(
								ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, StringUtil.EMPTY);
						if (StringUtil.isEmpty(browserExecutable))
						{
							ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
							JSLaunchConfigurationHelper.setBrowserDefaults(wc, nature);
							wc.doSave();
						}
					}
					return config;
				}
			}
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault()
					.getLaunchManager().generateLaunchConfigurationName(nature + " - Internal Server")); //$NON-NLS-1$
			JSLaunchConfigurationHelper.setDefaults(wc, nature);

			return wc.doSave();
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		return null;
	}

	private ILaunchConfigurationType getLaunchConfigType()
	{
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		return manager.getLaunchConfigurationType(ILaunchConfigurationConstants.ID_JS_APPLICATION);
	}

	public static String showBrowserNotFoundDialog(final boolean download)
	{
		final String[] path = new String[] { null };
		UIUtils.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				MessageDialogWithToggle md = new MessageDialogWithToggle(UIUtils.getActiveShell(),
						Messages.Startup_Notification, null, Messages.Startup_StudioRequiresFirefox,
						MessageDialog.INFORMATION, new String[] { StringUtil.ellipsify(CoreStrings.BROWSE),
								download ? Messages.Startup_Download : Messages.Startup_CheckAgain,
								IDialogConstants.CANCEL_LABEL }, 0, Messages.Startup_DontAskAgain, false);
				md.setPrefKey(com.aptana.js.debug.ui.internal.IJSDebugUIConstants.PREF_SKIP_FIREFOX_CHECK);
				md.setPrefStore(JSDebugUIPlugin.getDefault().getPreferenceStore());

				int returnCode = md.open();
				switch (returnCode)
				{
					case IDialogConstants.INTERNAL_ID:
						FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), SWT.OPEN);
						if (Platform.OS_WIN32.equals(Platform.getOS()))
						{
							fileDialog.setFilterExtensions(new String[] { "*.exe" }); //$NON-NLS-1$
							fileDialog.setFilterNames(new String[] { Messages.Startup_ExecutableFiles });
						}
						path[0] = fileDialog.open();
						break;
					case IDialogConstants.INTERNAL_ID + 1:
						if (download)
						{
							WorkbenchBrowserUtil.launchExternalBrowser("http://www.getfirefox.com"); //$NON-NLS-1$
						}
						path[0] = StringUtil.EMPTY;
						break;
					default:
				}
			}
		});
		return path[0];
	}
}
