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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.osgi.util.NLS;

import com.aptana.core.epl.downloader.FileReader;
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
	public String getDownloadLocation()
	{
		if (saveTo == null)
		{
			return null;
		}
		return saveTo.getAbsolutePath();
	}

	protected void setResult(IStatus result)
	{
		this.result = result;
	}

	public void execute(IProgressMonitor monitor)
	{
		monitor.subTask(NLS.bind("Downloading {0}", url.toString()));
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
			FileReader reader = new FileReader(null);
			reader.readInto(this.url.toURI(), new FileOutputStream(this.saveTo), 0, monitor);

			// check that job ended ok - throw exceptions otherwise
			IStatus result = reader.getResult();
			if (result.getSeverity() == IStatus.CANCEL)
			{
				throw new UserCancelledException();
			}
			if (!result.isOK())
			{
				throw new CoreException(result);
			}
			// try
			// {
			// DataInputStream dataInput = new DataInputStream(this.url.openStream());
			// FileOutputStream dataOut = new FileOutputStream(this.saveTo);
			// byte[] buffer = new byte[BUFFER_SIZE];
			// int readCount = -1;
			// while ((readCount = dataInput.read(buffer, 0, BUFFER_SIZE)) != -1)
			// {
			// if (monitor.isCanceled())
			// {
			// dataInput.close();
			// dataOut.flush();
			// dataOut.close();
			// monitor.done();
			// return Status.CANCEL_STATUS;
			// }
			// dataOut.write(buffer, 0, readCount);
			// }
			// dataInput.close();
			// dataOut.flush();
			// dataOut.close();
			// monitor.done();
			// }
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
		String tempPath = System.getProperty("java.io.tmpdir");//$NON-NLS-1$
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
			CoreIOPlugin.log(e);
		}

		try
		{
			return File.createTempFile("aptana", null);
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					"Could not create a local temporary file for the downloaded content", e));//$NON-NLS-1$
		}
	}
}
