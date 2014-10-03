/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.configurationProcessors.installer.JavaScriptImporterOptionsDialog;

/**
 * An installer (import) processor for JavaScript libraries, such as jQuery and Prototype.<br>
 * This processor download and place the JS library under a custom javascript folder in the selected (or active)
 * project. It also allows the use to select the location manually.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JavaScriptLibraryInstallProcessor extends InstallerConfigurationProcessor
{
	private static final String JS_LIBRARY = "JS Library"; //$NON-NLS-1$
	private static boolean installationInProgress;
	private String libraryName;
	private IProject targetProject;

	/**
	 * Returns the JS Library name.
	 */
	@Override
	protected String getApplicationName()
	{
		return libraryName;
	}

	/**
	 * Install a JavaScript library into a user-specified project.<br>
	 * The configuration will grab the name and the location of the library from the given attributes. <br>
	 * We expect an array of attributes with the same structure described at {@link #loadAttributes(Object)}.
	 * 
	 * @param attributes
	 *            A non-empty string array, which contains the URLs for the JS library file(s) and an optional Map of
	 *            additional attributes.
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object)
	 * @see #loadAttributes(Object)
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// Get a Class lock to avoid multiple installations at the same time even with multiple instances of this
		// RubyInstallProcessor
		synchronized (this.getClass())
		{
			if (installationInProgress)
			{
				return configurationStatus;
			}
			installationInProgress = true;
		}
		try
		{
			configurationStatus.removeAttribute(CONFIG_ATTR);
			clearErrorAttributes();

			// Load the installer's attributes
			IStatus loadingStatus = loadAttributes(attributes);
			if (!loadingStatus.isOK())
			{
				String message = loadingStatus.getMessage();
				applyErrorAttributes(message);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
				return configurationStatus;
			}

			// Check that we got the expected single install URL

			if (urls.length == 0)
			{
				// structure error
				String err = NLS.bind(Messages.InstallProcessor_wrongNumberOfInstallLinks, new Object[] { JS_LIBRARY,
						1, urls.length });
				applyErrorAttributes(err);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(err));
				return configurationStatus;
			}
			// Try to get the library name from the optional attributes. If it's not there, we log a warning and use a
			// default one.
			libraryName = attributesMap.get(NAME_ATTRIBUTE);
			if (libraryName == null)
			{
				// just in case
				libraryName = JS_LIBRARY;
				IdeLog.logWarning(PortalUIPlugin.getDefault(),
						"Expected a name attribute for the JS library, but got null."); //$NON-NLS-1$
			}
			// Start the installation...
			configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
			IStatus status = download(urls, progressMonitor);
			if (status.isOK())
			{
				status = install(progressMonitor);
			}
			switch (status.getSeverity())
			{
				case IStatus.OK:
				case IStatus.INFO:
				case IStatus.WARNING:
					displayMessageInUIThread(MessageDialog.INFORMATION,
							NLS.bind(Messages.InstallProcessor_installerTitle, libraryName),
							NLS.bind(Messages.InstallProcessor_installationSuccessful, libraryName));
					configurationStatus.setStatus(ConfigurationStatus.OK);
					break;
				case IStatus.ERROR:
					configurationStatus.setStatus(ConfigurationStatus.ERROR);
					break;
				case IStatus.CANCEL:
					configurationStatus.setStatus(ConfigurationStatus.INCOMPLETE);
					break;
				default:
					configurationStatus.setStatus(ConfigurationStatus.UNKNOWN);
			}
			return configurationStatus;
		}
		finally
		{
			synchronized (this.getClass())
			{
				installationInProgress = false;
			}
		}
	}

	/**
	 * Install the library.<br>
	 * The installation will display a selection dialog, displaying the projects in the workspace, and selecting the
	 * active project by default. It also takes into account the type of the project (nature) when suggesting the
	 * location to save the JS libraries.
	 * 
	 * @param progressMonitor
	 * @return A status indication of the process success or failure.
	 */
	protected IStatus install(IProgressMonitor progressMonitor)
	{
		Job job = new UIJob(Messages.JSLibraryInstallProcessor_directorySelection)
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				JavaScriptImporterOptionsDialog dialog = new JavaScriptImporterOptionsDialog(Display.getDefault()
						.getActiveShell(), libraryName);
				if (dialog.open() == Window.OK)
				{
					String selectedLocation = dialog.getSelectedLocation();
					IPath path = Path.fromOSString(selectedLocation);
					targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
					// Making sure that the project is not null, although this should never happen
					if (targetProject != null)
					{
						String fullPath = targetProject.getLocation().append(path.removeFirstSegments(1)).toOSString();
						File targetFolder = new File(fullPath);
						if (!targetFolder.exists() && !targetFolder.mkdirs())
						{
							// could not create the directories needed!
							IdeLog.logError(
									PortalUIPlugin.getDefault(),
									"Failed to create directories when importing JS slibrary!", new Exception("Failed to create '" + fullPath + '\'')); //$NON-NLS-1$ //$NON-NLS-2$
							return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID,
									Messages.JSLibraryInstallProcessor_directoriesCreationFailed);
						}
						// Copy the downloaded content into the created directory
						List<IStatus> errors = new ArrayList<IStatus>();
						for (IPath f : downloadedPaths)
						{
							try
							{
								File sourceLocation = f.toFile();
								File targetLocation = new File(targetFolder, sourceLocation.getName());
								if (targetLocation.exists())
								{
									if (!MessageDialog.openQuestion(
											Display.getDefault().getActiveShell(),
											Messages.JSLibraryInstallProcessor_fileConflictTitle,
											Messages.JSLibraryInstallProcessor_fileConflictMessage
													+ sourceLocation.getName()
													+ Messages.JSLibraryInstallProcessor_overwriteQuestion))
									{
										continue;
									}
								}
								IOUtil.copyFile(sourceLocation, targetLocation);
							}
							catch (IOException e)
							{
								errors.add(new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, e.getMessage(), e));
							}
						}
						if (!errors.isEmpty())
						{
							return new MultiStatus(PortalUIPlugin.PLUGIN_ID, 0, errors.toArray(new IStatus[errors
									.size()]), Messages.JSLibraryInstallProcessor_multipleErrorsWhileImportingJS, null);
						}
						// Since we don't cache the installed location for javascript libraries, we pass null here. This
						// will only mark for deletion the downloaded content.
						finalizeInstallation(null);
					}
					else
					{
						IdeLog.logError(PortalUIPlugin.getDefault(),
								"Unexpected null project when importing a JS library!", new Exception()); //$NON-NLS-1$
						return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID,
								Messages.JSLibraryInstallProcessor_unexpectedNull);
					}
					try
					{
						targetProject.refreshLocal(IResource.DEPTH_INFINITE, SubMonitor.convert(monitor));
					}
					catch (CoreException e)
					{
						IdeLog.logError(PortalUIPlugin.getDefault(), "Error while refreshing the project.", e); //$NON-NLS-1$
					}
					return Status.OK_STATUS;
				}
				else
				{
					return Status.CANCEL_STATUS;
				}
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
		try
		{
			job.join();
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return job.getResult();
	}
}
