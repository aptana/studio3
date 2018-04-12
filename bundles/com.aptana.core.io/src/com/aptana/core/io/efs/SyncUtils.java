/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.core.io.efs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.util.ProgressMonitorInterrupter;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.PermissionDeniedException;

/**
 * @author Max Stepanov
 */
public final class SyncUtils
{

	/**
	 * 
	 */
	private SyncUtils()
	{
	}

	/**
	 * This method functionally duplicates EFS FileStore.copy() method with the following exceptions: 1. it transfers
	 * precise datetime 2. it transfers file permissions if requested 3. it does not delete destination file on failure
	 * (FTP implementation always uploads to a temporary file first and always cleans up afterwards) 4. it does not
	 * ignore exceptions on output stream close (FTP implementation finalizes transfer on close) 5. it ignores
	 * permission exceptions on transferring attributes 6. it always works as it EFS.SHALLOW would be set 7. EFS
	 * implementation prevent concurrent copying of FileStores due static synchronized buffer (see
	 * org.eclipse.core.filesystem.provider.FileStore.transferStreams)
	 * 
	 * @param source
	 * @param sourceInfo
	 * @param destination
	 * @param options
	 * @param monitor
	 * @throws CoreException
	 */
	public static void copy(IFileStore source, IFileInfo sourceInfo, IFileStore destination, int options,
			IProgressMonitor monitor) throws CoreException
	{
		try
		{
			monitor = (monitor == null) ? new NullProgressMonitor() : monitor;
			checkCanceled(monitor);
			monitor.beginTask(StringUtil.EMPTY, (sourceInfo == null) ? 3 : 2);
			if (sourceInfo == null)
			{
				sourceInfo = source.fetchInfo(IExtendedFileStore.DETAILED, subMonitorFor(monitor, 1));
			}
			checkCanceled(monitor);
			if (sourceInfo.isDirectory())
			{
				destination.mkdir(EFS.NONE, subMonitorFor(monitor, 2));
			}
			else
			{
				final byte[] buffer = new byte[8192];
				long length = sourceInfo.getLength();
				int totalWork = (length == -1) ? IProgressMonitor.UNKNOWN : 1 + (int) (length / buffer.length);
				InputStream in = null;
				OutputStream out = null;
				ProgressMonitorInterrupter interrupter = new ProgressMonitorInterrupter(monitor);
				try
				{
					in = source.openInputStream(EFS.NONE, subMonitorFor(monitor, 0));
					out = destination.openOutputStream(EFS.NONE, subMonitorFor(monitor, 0));
					IProgressMonitor subMonitor = subMonitorFor(monitor, 2);
					subMonitor
							.beginTask(MessageFormat.format(Messages.SyncUtils_Copying, source.toString()), totalWork);
					int bytesRead;
					while (true)
					{
						checkCanceled(monitor);
						try
						{
							bytesRead = in.read(buffer);
						}
						catch (IOException e)
						{
							bytesRead = -1;
							checkCanceled(monitor);
							error(MessageFormat.format(Messages.SyncUtils_ERR_Reading, source.toString()), e);
						}
						if (bytesRead == -1)
						{
							break;
						}
						checkCanceled(monitor);
						try
						{
							out.write(buffer, 0, bytesRead);
						}
						catch (IOException e)
						{
							checkCanceled(monitor);
							error(MessageFormat.format(Messages.SyncUtils_ERR_Writing, destination.toString()), e);
						}
						subMonitor.worked(1);
					}
					subMonitor.done();
				}
				finally
				{
					interrupter.dispose();
					safeClose(in);
					safeClose(out);
				}
			}
			try
			{
				if (destination instanceof IExtendedFileStore)
				{
					destination.putInfo(sourceInfo, EFS.SET_ATTRIBUTES | EFS.SET_LAST_MODIFIED | options,
							subMonitorFor(monitor, 1));
				}
			}
			catch (CoreException e)
			{
				// happens when ftp user is not an owner of the file, but still has read/write permissions
				if (!(e.getCause() instanceof PermissionDeniedException))
				{
					throw e;
				}
			}
		}
		finally
		{
			monitor.done();
		}
	}

	private static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks)
	{
		if (monitor == null)
		{
			return new NullProgressMonitor();
		}
		if (monitor instanceof NullProgressMonitor)
		{
			return monitor;
		}
		return new SubProgressMonitor(monitor, ticks);
	}

	private static void checkCanceled(IProgressMonitor monitor)
	{
		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}

	private static void error(String message, Exception e) throws CoreException
	{
		throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, message, e));
	}

	private static void safeClose(InputStream in)
	{
		try
		{
			if (in != null)
			{
				in.close();
			}
		}
		catch (IOException ignore)
		{
			ignore.getCause();
		}
	}

	private static void safeClose(OutputStream out) throws CoreException
	{
		try
		{
			if (out != null)
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.SyncUtils_ERR_FailToClose, e));
		}
	}
}
