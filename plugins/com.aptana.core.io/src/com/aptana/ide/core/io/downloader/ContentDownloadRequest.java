/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.osgi.util.NLS;

import com.aptana.core.epl.downloader.FileReader;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FileUtil;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * A single content download request.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class ContentDownloadRequest
{
	private URL url;
	private File saveTo;
	private IStatus result;

	public ContentDownloadRequest(URL url) throws CoreException
	{
		this(url, getTempFile(url));
	}

	public ContentDownloadRequest(URL url, File saveTo)
	{
		this.url = url;
		this.saveTo = saveTo;
	}

	public IStatus getResult()
	{
		return result;
	}

	/**
	 * Returns the absolute local-machine path of the file we are downloading.
	 * 
	 * @return The absolute path of the downloaded file; Null, in case the local save location was not resolved.
	 */
	public IPath getDownloadLocation()
	{
		if (saveTo == null)
		{
			return null;
		}
		return Path.fromOSString(saveTo.getAbsolutePath());
	}

	protected void setResult(IStatus result)
	{
		this.result = result;
	}

	public void execute(IProgressMonitor monitor)
	{
		monitor.subTask(NLS.bind(Messages.ContentDownloadRequest_downloading, url.toString()));
		IStatus status = download(monitor);
		setResult(status);
	}

	/**
	 * Do the actual downloading. Report the progress to the progress monitor.
	 * 
	 * @param monitor
	 * @return
	 */
	private IStatus download(IProgressMonitor monitor)
	{
		IStatus status = Status.OK_STATUS;
		// perform the download
		try
		{
			// Use ECF FileTransferJob implementation to get the remote file.
			FileReader reader = new FileReader(null);
			FileOutputStream anOutputStream = new FileOutputStream(this.saveTo);
			reader.readInto(this.url.toURI(), anOutputStream, 0, monitor);
			// check that job ended ok - throw exceptions otherwise
			IStatus result = reader.getResult();
			if (result != null)
			{
				if (result.getSeverity() == IStatus.CANCEL)
				{
					return Status.CANCEL_STATUS;
				}
				if (!result.isOK())
				{
					throw new CoreException(result);
				}
			}
		}
		catch (OperationCanceledException e)
		{
			return Status.CANCEL_STATUS;
		}
		catch (Throwable t)
		{
			return new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, t.getMessage(), t);
		}
		return status;
	}

	/**
	 * Returns a temporary file with a name based on the URL file name.<br>
	 * In case no URL file name exists, we try to generate a temp file with an 'aptana' prefix.
	 * 
	 * @param url
	 * @return
	 * @throws CoreException
	 */
	protected static File getTempFile(URL url) throws CoreException
	{
		String tempPath = FileUtil.getTempDirectory().toOSString();
		try
		{
			IPath path = Path.fromOSString(url.toURI().getPath());
			String name = path.lastSegment();
			if (name != null && name.length() > 0)
			{
				File f = new File(tempPath, name);
				f.deleteOnExit();
				return f;
			}
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}

		try
		{
			return File.createTempFile(Messages.ContentDownloadRequest_tempFilePrefix, null);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					"Could not create a local temporary file for the downloaded content", e));//$NON-NLS-1$
		}
	}
}
