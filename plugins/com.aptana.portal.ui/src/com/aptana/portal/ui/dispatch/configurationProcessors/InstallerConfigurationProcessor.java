/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.InputStreamGobbler;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * Basic, abstract implementation, of a processor that deals with installing software.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class InstallerConfigurationProcessor extends AbstractConfigurationProcessor
{
	protected static final String APTANA_PROPERTIES_FILE_NAME = ".aptana"; //$NON-NLS-1$
	protected static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	protected static final String INSTALL_DIR_ATTRIBUTE = "install_dir"; //$NON-NLS-1$

	protected List<IPath> downloadedPaths;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#computeStatus(org.eclipse.core.runtime.
	 * IProgressMonitor, java.lang.Object)
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		// This one does nothing. We should compute the status in the generic VersionsConfigurationProcessor
		return configurationStatus;
	}

	/**
	 * Returns the application's name.
	 * 
	 * @return The application's name (e.g. XAMPP, Ruby)
	 */
	protected abstract String getApplicationName();

	/**
	 * Download the remote content and store it the temp directory.
	 * 
	 * @param URLs
	 * @param progressMonitor
	 */
	public IStatus download(String[] URLs, IProgressMonitor progressMonitor)
	{
		if (URLs.length == 0)
		{
			String err = Messages.InstallerConfigurationProcessor_missingDownloadTargets;
			applyErrorAttributes(err);
			IdeLog.logError(PortalUIPlugin.getDefault(),
					"We expected an array of URLs, but got an empty array.", new Exception(err)); //$NON-NLS-1$
			return new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, err);
		}
		downloadedPaths = null;
		DownloadManager downloadManager = new DownloadManager();
		List<URI> urlsList = new ArrayList<URI>(URLs.length);
		for (int i = 0; i < URLs.length; i++)
		{
			try
			{
				urlsList.add(new URI(urls[i]));
			}
			catch (URISyntaxException mue)
			{
				IdeLog.logError(PortalUIPlugin.getDefault(), mue);
			}
		}
		try
		{
			downloadManager.addURIs(urlsList);
			IStatus status = downloadManager.start(progressMonitor);
			if (status.isOK())
			{
				downloadedPaths = downloadManager.getContentsLocations();
			}
			return status;
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return Status.CANCEL_STATUS;
	}

	/**
	 * Cache the installed application location and version in the preferences.
	 * 
	 * @param installDir
	 *            - The directory the application was installed to.
	 * @param versionedFileLocation
	 *            - Can be the URL that we grabbed the installer from, or any other string that contains a version
	 *            information in a form of x.y.z.
	 * @param appName
	 *            - The application name (e.g. xampp)
	 */
	@SuppressWarnings("unchecked")
	public void cacheVersion(String installDir, String versionedFileLocation, String appName)

	{
		IPreferenceStore preferenceStore = PortalUIPlugin.getDefault().getPreferenceStore();
		String versions = preferenceStore.getString(IPortalPreferences.CACHED_VERSIONS_PROPERTY_NAME);
		Map<String, Map<String, String>> versionsMap = null;
		if (versions == null || versions.equals(StringUtil.EMPTY))
		{
			versionsMap = new HashMap<String, Map<String, String>>();
		}
		else
		{
			versionsMap = (Map<String, Map<String, String>>) JSON.parse(versions);
		}
		Map<String, String> appVersionMap = new HashMap<String, String>();
		Version version = VersionUtil.parseVersion(versionedFileLocation);
		if (version != null)
		{
			appVersionMap.put(IPortalPreferences.CACHED_VERSION_PROPERTY, version.toString());
			appVersionMap.put(IPortalPreferences.CACHED_LOCATION_PROPERTY, installDir);
			versionsMap.put(appName.toLowerCase(), appVersionMap);
			preferenceStore.setValue(IPortalPreferences.CACHED_VERSIONS_PROPERTY_NAME, JSON.toString(versionsMap));
		}
		else
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), MessageFormat.format(
					"Could not cache the location and version for {0}. Install dir: {1}, versionedFileLocation: {2}", //$NON-NLS-1$
					appName, installDir, versionedFileLocation), new Exception());
		}
	}

	/**
	 * Extract the given zip file into the target folder on a Windows machine.
	 * 
	 * @param sfxZip
	 *            Self extracting 7zip file.
	 * @param targetFolder
	 * @return The status of that extraction result.
	 */
	public static IStatus extractWin(IPath sfxZip, IPath targetFolder)
	{
		IStatus errorStatus = new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID,
				Messages.InstallerConfigurationProcessor_unableToExtractZip);
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			IdeLog.logError(
					PortalUIPlugin.getDefault(),
					"Unable to extract the Zip file. A Windows OS extractor was called for a non-Windows platform.", new Exception()); //$NON-NLS-1$
			return errorStatus;
		}
		if (sfxZip == null || targetFolder == null)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Undefined zip file or target folder", new Exception()); //$NON-NLS-1$
			return errorStatus;
		}
		File destinationFolder = targetFolder.toFile();
		if (!destinationFolder.exists() && !destinationFolder.mkdirs())
		{
			IdeLog.logError(PortalUIPlugin.getDefault(),
					"Failed to create destination directory " + destinationFolder, new Exception()); //$NON-NLS-1$
			return errorStatus;
		}
		// TODO Use ProcessUtil!
		ProcessBuilder processBuilder = new ProcessBuilder(sfxZip.toOSString(), "-o" + targetFolder.toOSString(), //$NON-NLS-1$
				"-y", //$NON-NLS-1$
				sfxZip.toOSString());
		processBuilder.directory(destinationFolder);
		processBuilder.redirectErrorStream(true);
		String output = null;
		try
		{
			Process process = processBuilder.start();
			InputStreamGobbler errorGobbler = new InputStreamGobbler(process.getErrorStream(), "\n", null); //$NON-NLS-1$
			InputStreamGobbler outputGobbler = new InputStreamGobbler(process.getInputStream(), "\n", null); //$NON-NLS-1$
			outputGobbler.start();
			errorGobbler.start();
			process.waitFor();
			outputGobbler.interrupt();
			errorGobbler.interrupt();
			outputGobbler.join();
			errorGobbler.join();
			output = outputGobbler.getResult();
			String errors = errorGobbler.getResult();
			int exitVal = process.exitValue();
			if (exitVal == 0)
			{
				return Status.OK_STATUS;
			}
			IdeLog.logError(
					PortalUIPlugin.getDefault(),
					"Zip extraction failed. The process returned " + exitVal, new Exception("Process output:\n" + errors)); //$NON-NLS-1$ //$NON-NLS-2$
			return errorStatus;
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return errorStatus;
		}
		finally
		{
			if (output != null)
			{
				IdeLog.logInfo(PortalUIPlugin.getDefault(), output);
			}
		}
	}

	/**
	 * Display a message dialog in a UI thread.
	 * 
	 * @param kind
	 *            See {@link MessageDialog} for the types allowed.
	 * @param title
	 * @param message
	 */
	public void displayMessageInUIThread(final int kind, final String title, final String message)
	{
		UIUtils.showMessageDialogFromBgThread(kind, title, message, null);
	}

	/**
	 * Finalize the installation. <br>
	 * This implementation just marks to delete on exit any downaloaded file.
	 * 
	 * @param installDir
	 */
	protected void finalizeInstallation(String installDir)
	{
		deleteDownloadedPaths();
		// Cache the version and the location of the installed app.
		// We assume here that the version of app is specified in the install URL!
		if (installDir != null)
		{
			cacheVersion(installDir, urls[0], getApplicationName());
		}
	}

	/**
	 * Mark the downloaded paths to be deleted on exit.
	 */
	protected void deleteDownloadedPaths()
	{
		if (!CollectionsUtil.isEmpty(downloadedPaths))
		{
			for (IPath f : downloadedPaths)
			{
				File toDelete = f.toFile();
				if (toDelete.exists())
				{
					toDelete.deleteOnExit();
				}
			}
		}
	}
}
