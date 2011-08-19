/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.logging.IdeLog;

/**
 * A utilities class which provides file-locking related functions.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class LockUtils
{
	/**
	 * Try to obtain a lock on the given file and release it at the end.<br>
	 * Example of use: This method should be called before trying to create a process executing this file. It verifies
	 * that the file is ready to be executed and no other JVM process is holding into it. <br>
	 * This method will try to obtain a lock repeatedly during the timeToWait period. If not successful, an error status
	 * is returned.
	 * 
	 * @param fileName
	 * @param timeToWait
	 *            The maximum time (in milliseconds) to wait for a lock before giving up.
	 * @return The status for the locking procedure (OK or Error)
	 */
	public static IStatus waitForLockRelease(String fileName, long timeToWait)
	{
		RandomAccessFile randomAccessFile = null;
		try
		{
			int retries = (int) (timeToWait / 500L) + 1;
			Throwable lastException = null;
			boolean isLocked = true;
			FileLock fileLock = null;
			randomAccessFile = new RandomAccessFile(fileName, "rw"); //$NON-NLS-1$
			FileChannel channel = randomAccessFile.getChannel();
			while (isLocked)
			{
				try
				{
					// fileLock = channel.lock(0L, Long.MAX_VALUE, true);
					fileLock = channel.tryLock(0L, Long.MAX_VALUE, true);
					lastException = null;
				}
				catch (Exception e)
				{
					lastException = e;
				}
				if (lastException != null || fileLock == null)
				{
					retries--;
					if (retries == 0)
					{
						// give up
						break;
					}
					Thread.sleep(500L);
				}
				else
				{
					isLocked = false;
				}
			}
			if (lastException != null || fileLock == null)
			{
				IdeLog.logError(CoreIOPlugin.getDefault(),
						MessageFormat.format("Failed to lock {0}", fileName), lastException); //$NON-NLS-1$
				return new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, Messages.LockUtils_failedToLock + fileName
						+ Messages.LockUtils_seeErrorLog, lastException);
			}
			if (fileLock != null)
			{
				fileLock.release();
				randomAccessFile.close();
				fileLock = null;
			}
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, e.getMessage(), e);
		}
		finally
		{
			if (randomAccessFile != null)
			{
				try
				{
					randomAccessFile.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * This method tries to access the given folder and write a test file into it before it can return an OK status.<br>
	 * The check is done repeatedly for the duration of the timeToWait, and if not successful, an Error status is
	 * returned.
	 * 
	 * @param dir
	 * @param timeToWait
	 * @return The status for the check procedure (OK or Error)
	 */
	public static IStatus waitForFolderAccess(String dir, long timeToWait)
	{
		int retries = (int) (timeToWait / 500L) + 1;
		Throwable lastException = null;
		boolean isLocked = true;
		File tempFile = new File(dir, ".tmp"); //$NON-NLS-1$
		while (isLocked)
		{
			try
			{
				// fileLock = channel.lock(0L, Long.MAX_VALUE, true);
				isLocked = !tempFile.createNewFile();
				lastException = null;
			}
			catch (Exception e)
			{
				lastException = e;
			}
			if (isLocked)
			{
				retries--;
				if (retries == 0)
				{
					// give up
					break;
				}
				try
				{
					Thread.sleep(500L);
				}
				catch (InterruptedException e)
				{
					// ignore
				}
			}
		}
		if (isLocked)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(),
					MessageFormat.format("Failed to write to {0}", dir), lastException); //$NON-NLS-1$
			return new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, Messages.LockUtils_failedToWrite + dir
					+ Messages.LockUtils_seeErrorLog, lastException);
		}
		if (tempFile != null)
		{
			tempFile.delete();
		}
		return Status.OK_STATUS;
	}
}
