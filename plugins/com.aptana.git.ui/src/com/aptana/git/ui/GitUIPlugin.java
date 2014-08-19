/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.PortableGit;
import com.aptana.git.ui.internal.CachedImageDescriptor;
import com.aptana.git.ui.internal.GitColors;
import com.aptana.git.ui.internal.GitLightweightDecorator;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.IDialogConstants;
import com.aptana.ui.PopupSchedulingRule;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class GitUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	private static final String PLUGIN_ID = "com.aptana.git.ui"; //$NON-NLS-1$

	// The shared instance
	private static GitUIPlugin plugin;

	private QuickDiffColorer themeChangeListener;

	/**
	 * The constructor
	 */
	public GitUIPlugin() // $codepro.audit.disable
							// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		super.start(context);
		plugin = this;

		// Listen for theme changes and force the quick diff colors to match our git diff colors.
		Job job = new Job("Run any work in a delayed job to avoid slowing startup!") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (themeChangeListener == null)
				{
					themeChangeListener = new QuickDiffColorer();
				}
				InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(themeChangeListener);
				themeChangeListener.forceColors();

				checkHasGit();
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		try
		{
			if (themeChangeListener != null)
			{
				InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(
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

	private void checkHasGit()
	{
		if (getPreferenceStore().getBoolean(IPreferenceConstants.IGNORE_NO_GIT))
		{
			return;
		}
		if (Platform.OS_WIN32.equals(Platform.getOS()) && GitExecutable.instance() == null)
		{
			UIJob job = new GitInstallationValidatorJob(Messages.GitUIPlugin_GitInstallationValidator);
			job.setPriority(Job.INTERACTIVE);
			job.setRule(PopupSchedulingRule.INSTANCE);
			job.schedule();
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

	public static String getPluginId()
	{
		return PLUGIN_ID;
	}

	public static Image getImage(String string)
	{
		ImageRegistry imageRegistry = getDefault().getImageRegistry();
		if (imageRegistry.get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(getPluginId(), string);
			if (id != null)
			{
				imageRegistry.put(string, id);
			}
		}
		return imageRegistry.get(string);
	}

	private final class GitInstallationValidatorJob extends UIJob
	{
		private GitInstallationValidatorJob(String name)
		{
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor)
		{

			while (true)
			{
				MessageDialogWithToggle dlg = new MessageDialogWithToggle(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), Messages.GitUIPlugin_ConfiguringGitSupportTitle, null,
						Messages.GitUIPlugin_ConfigureGitPluginMessage, MessageDialog.INFORMATION,
						new String[] { IDialogConstants.OK_LABEL }, IDialogConstants.OK_ID,
						Messages.GitUIPlugin_ToggleMessage, false);
				int code = dlg.open();

				boolean toggleState = dlg.getToggleState();
				if (toggleState)
				{
					getPreferenceStore().setValue(IPreferenceConstants.IGNORE_NO_GIT, dlg.getToggleState());
				}

				switch (code)
				{
					case IDialogConstants.OK_ID:

						if (installPortableGit(monitor))
						{
							return Status.OK_STATUS;
						}

						if (toggleState)
						{
							return Status.OK_STATUS;
						}

						// assuming install failed and we didn't toggle out, we'll go through the loop
						// again.
						break;
					default:
						return Status.OK_STATUS;
				}
			}
		}

		private boolean installPortableGit(IProgressMonitor monitor)
		{
			ProgressMonitorDialog dlg = new ProgressMonitorDialog(UIUtils.getActiveShell());
			try
			{
				dlg.run(true, false, new IRunnableWithProgress()
				{
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
					{
						try
						{
							monitor.beginTask(Messages.GitUIPlugin_ConfiguringGitSupport, IProgressMonitor.UNKNOWN);
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
				IdeLog.logError(getDefault(), e, IDebugScopes.DEBUG);
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

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.GitUIPlugin_GitInstallError, Messages.GitUIPlugin_GitConfigurationIncomplete);
			return false;
		}
	}

	/**
	 * Sets the preference values for quick diff coloring to be same as our git stage/unstaged colors (for our editors).
	 * If invasive theming is turned on we set it for all editors.
	 * 
	 * @author cwilliams
	 */
	private final class QuickDiffColorer implements IPreferenceChangeListener
	{
		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (!event.getKey().equals(IThemeManager.THEME_CHANGED))
			{
				return;
			}

			forceColors();
		}

		protected void forceColors()
		{

			UIUtils.runInUIThread(new Runnable()
			{

				String greenBGString;
				String redBGString;

				public void run()
				{
					RGB greenBG = GitColors.greenBG().getRGB();
					greenBGString = StringConverter.asString(greenBG);
					RGB redBG = GitColors.redBG().getRGB();
					redBGString = StringConverter.asString(redBG);

					JFaceResources.getColorRegistry().put("CONFLICTING_COLOR", redBG); //$NON-NLS-1$
					JFaceResources.getColorRegistry().put("RESOLVED_COLOR", greenBG); //$NON-NLS-1$
					setQuickDiffColors(InstanceScope.INSTANCE.getNode("com.aptana.editor.common")); //$NON-NLS-1$
					if (ThemePlugin.applyToAllEditors())
					{
						setQuickDiffColors(InstanceScope.INSTANCE.getNode("org.eclipse.ui.editors")); //$NON-NLS-1$
					}
				}

				protected void setQuickDiffColors(IEclipsePreferences prefs)
				{
					prefs.put("changeIndicationColor", greenBGString); //$NON-NLS-1$
					prefs.put("additionIndicationColor", greenBGString); //$NON-NLS-1$
					prefs.put("deletionIndicationColor", redBGString); //$NON-NLS-1$
					// APSTUD-4152
					// See ThemeManager#setCurrentTheme() for outgoing/incoming diff/compare colors
					prefs.put("CONFLICTING_COLOR", redBGString); //$NON-NLS-1$
					prefs.put("RESOLVED_COLOR", greenBGString); //$NON-NLS-1$

					try
					{
						prefs.flush();
					}
					catch (BackingStoreException e)
					{
						IdeLog.logError(getDefault(), e, IDebugScopes.DEBUG);
					}
				}
			});
		}
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		super.initializeImageRegistry(reg);

		// Set up the images we use for git decorations
		reg.put(GitLightweightDecorator.STAGED_ADDED_IMAGE,
				new CachedImageDescriptor(imageDescriptorFromPlugin(PLUGIN_ID,
						GitLightweightDecorator.STAGED_ADDED_IMAGE)));
		reg.put(GitLightweightDecorator.STAGED_REMOVED_IMAGE,
				new CachedImageDescriptor(imageDescriptorFromPlugin(PLUGIN_ID,
						GitLightweightDecorator.STAGED_REMOVED_IMAGE)));
		reg.put(GitLightweightDecorator.UNTRACKED_IMAGE,
				new CachedImageDescriptor(imageDescriptorFromPlugin(PLUGIN_ID, GitLightweightDecorator.UNTRACKED_IMAGE)));
	}
}
