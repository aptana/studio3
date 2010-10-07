package com.aptana.git.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.PlatformUtil;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.PortableGit;
import com.aptana.git.ui.internal.GitColors;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.IDialogConstants;
import com.aptana.ui.PopupSchedulingRule;

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

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
					{

						public void run()
						{
							IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.ui.editors"); //$NON-NLS-1$
							// Quick Diff colors
							prefs.put("changeIndicationColor", StringConverter.asString(GitColors.greenBG().getRGB())); //$NON-NLS-1$
							prefs.put("additionIndicationColor", StringConverter.asString(GitColors.greenBG().getRGB())); //$NON-NLS-1$
							prefs.put("deletionIndicationColor", StringConverter.asString(GitColors.redBG().getRGB())); //$NON-NLS-1$

							try
							{
								prefs.flush();
							}
							catch (BackingStoreException e)
							{
								GitUIPlugin.logError(e.getMessage(), e);
							}
						}
					});

				}
			}
		};
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(themeChangeListener);
		checkHasGit();
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
				new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(themeChangeListener);
			}
			themeChangeListener = null;
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}

	private void checkHasGit()
	{
		if (Platform.WS_WIN32.equals(Platform.getOS()))
		{
			if (GitExecutable.instance() == null)
			{
				UIJob job = new UIJob(Messages.GitUIPlugin_0)
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						while (true)
						{
							MessageDialog dlg = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getShell(), Messages.GitUIPlugin_1, null, Messages.GitUIPlugin_2,
									MessageDialog.WARNING, new String[] { IDialogConstants.SKIP_LABEL,
											Messages.GitUIPlugin_3, IDialogConstants.BROWSE_LABEL }, 2);
							switch (dlg.open())
							{
								case 0:
									return Status.OK_STATUS;
								case 1:
									if (installPortableGit(monitor))
									{
										return Status.OK_STATUS;
									}
									break;
								case 2:
									if (browseGitLocation())
									{
										return Status.OK_STATUS;
									}
									break;
								default:
									break;
							}
						}
					}
				};
				job.setPriority(Job.INTERACTIVE);
				job.setRule(PopupSchedulingRule.INSTANCE);
				job.schedule();
			}
		}
	}

	private boolean installPortableGit(IProgressMonitor monitor)
	{
		ProgressMonitorDialog dlg = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell());
		try
		{
			dlg.run(true, false, new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					try
					{
						monitor.beginTask(Messages.GitUIPlugin_4, IProgressMonitor.UNKNOWN);
						monitor.worked(1);
						PortableGit.install();
					}
					finally
					{
						monitor.done();
					}
				}
			});
		}
		catch (InvocationTargetException e)
		{
			logError(e);
		}
		catch (InterruptedException e)
		{
		}
		IPath path = PortableGit.getLocation();
		if (path != null)
		{
			GitExecutable.setPreferenceGitPath(path);
			return true;
		}
		else
		{
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.GitUIPlugin_5, Messages.GitUIPlugin_6);
		}
		return false;
	}

	private boolean browseGitLocation()
	{
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		while (true)
		{
			FileDialog dlg = new FileDialog(shell, SWT.APPLICATION_MODAL | SWT.OPEN);
			String gitExecutable = "git.exe"; //$NON-NLS-1$
			dlg.setFilterExtensions(new String[] { gitExecutable });
			dlg.setFilterNames(new String[] { Messages.GitUIPlugin_7 });
			dlg.setFileName(gitExecutable);
			dlg.setFilterPath(PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%\\Git\\bin")); //$NON-NLS-1$
			dlg.setText(Messages.GitUIPlugin_8);
			String result = dlg.open();
			if (result != null)
			{
				IPath path = Path.fromOSString(result);
				if (GitExecutable.acceptBinary(path))
				{
					GitExecutable.setPreferenceGitPath(path);
					return true;
				}
				else
				{
					MessageDialog.openWarning(shell, Messages.GitUIPlugin_9,
							NLS.bind(Messages.GitUIPlugin_10, GitExecutable.MIN_GIT_VERSION));
				}
			}
			else
			{
				return false;
			}
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
		if (!getDefault().isDebugging())
			return;
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

	public static void logError(Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), "", e)); //$NON-NLS-1$
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
