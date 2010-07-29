package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.core.util.InputStreamGobbler;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * Basic, abstract implementation, of a processor that deals with installing software.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class InstallerConfigurationProcessor extends AbstractConfigurationProcessor
{
	protected static final String APTANA_PROPERTIES_FILE_NAME = ".aptana"; //$NON-NLS-1$
	protected static final String WINDOWS_7ZIP_EXECUTABLE = "$os$/7za.exe"; //$NON-NLS-1$
	protected String[] downloadedPaths;
	protected String[] urls;

	/**
	 * Download the remote content and store it the temp directory.
	 * 
	 * @param URLs
	 * @param progressMonitor
	 */
	public IStatus download(Object[] URLs, IProgressMonitor progressMonitor)
	{
		downloadedPaths = null;
		DownloadManager downloadManager = new DownloadManager();
		urls = new String[URLs.length];
		List<URL> urlsList = new ArrayList<URL>(URLs.length);
		for (int i = 0; i < URLs.length; i++)
		{
			try
			{
				Object o = URLs[i];
				urls[i] = o.toString();
				urlsList.add(new URL(urls[i]));
			}
			catch (MalformedURLException mue)
			{
				PortalUIPlugin.logError(mue);
			}
		}
		try
		{
			downloadManager.addURLs(urlsList);
			IStatus status = downloadManager.start(progressMonitor);
			if (status.isOK())
			{
				downloadedPaths = downloadManager.getContentsLocations();
			}
			return status;
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError(e);
		}
		return Status.CANCEL_STATUS;
	}

	/**
	 * Extract the given zip file into the target folder on a Windows machine.
	 * 
	 * @param zipFile
	 * @param targetFolder
	 * @return The status of that extraction result.
	 */
	public static IStatus extractWin(String zipFile, String targetFolder)
	{
		IStatus errorStatus = new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID,
				Messages.InstallerConfigurationProcessor_unableToExtractZip);
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			PortalUIPlugin
					.logError(
							"Unable to extract the Zip file. A Windows OS extractor was called for a non-Windows platform.", new Exception()); //$NON-NLS-1$
			return errorStatus;
		}
		if (zipFile == null || targetFolder == null)
		{
			PortalUIPlugin.logError("Undefined zip file or target folder", new Exception()); //$NON-NLS-1$
			return errorStatus;
		}
		IPath zipExecutable = getBundlePath(WINDOWS_7ZIP_EXECUTABLE);
		File destinationFolder = new File(targetFolder);
		if (!destinationFolder.exists() && !destinationFolder.mkdirs())
		{
			PortalUIPlugin.logError("Failed to create destination directory " + destinationFolder, new Exception()); //$NON-NLS-1$
			return errorStatus;
		}
		ProcessBuilder processBuilder = new ProcessBuilder(zipExecutable.toOSString(), "x", //$NON-NLS-1$
				"-o" + targetFolder, //$NON-NLS-1$
				"-y", //$NON-NLS-1$
				zipFile);
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
			else
			{
				PortalUIPlugin
						.logError(
								"Zip extraction failed. The process returned " + exitVal, new Exception("Process output:\n" + errors)); //$NON-NLS-1$ //$NON-NLS-2$
				return errorStatus;
			}
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError(e);
			return errorStatus;
		}
		finally
		{
			if (output != null)
			{
				PortalUIPlugin.logInfo(output, null);
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
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.open(kind, null, title, message, SWT.NONE);
			}
		});
	}

	/**
	 * Finalize the installation. <br>
	 * This implementation just marks to delete on exit any downaloaded file.
	 * 
	 * @param installDir
	 */
	protected void finalizeInstallation(String installDir)
	{
		if (downloadedPaths != null)
		{
			for (String f : downloadedPaths)
			{
				File toDelete = new File(f);
				if (toDelete.exists())
				{
					toDelete.deleteOnExit();
				}
			}
		}
	}

	/*
	 * Returns an IPath from the given portable string.
	 */
	private static IPath getBundlePath(String path)
	{
		URL url = FileLocator.find(PortalUIPlugin.getDefault().getBundle(), Path.fromPortableString(path), null);
		if (url != null)
		{
			try
			{
				url = FileLocator.toFileURL(url);
				File file = new File(url.getPath());
				if (file.exists())
				{
					return Path.fromOSString(file.getAbsolutePath());
				}
			}
			catch (IOException e)
			{
				PortalUIPlugin.logError(e);
			}
		}
		return null;
	}
}
