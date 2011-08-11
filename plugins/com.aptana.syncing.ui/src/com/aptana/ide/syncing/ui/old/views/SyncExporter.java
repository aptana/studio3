/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FileUtil;
import com.aptana.ide.syncing.core.old.ISyncResource;
import com.aptana.ide.syncing.core.old.SyncState;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

public class SyncExporter
{

	/**
	 * Exports a sync state to a log file.
	 * 
	 * @param logFile
	 *            File to export the log into.
	 * @param items
	 *            Synchronized resources
	 */
	@SuppressWarnings("nls")
	public void export(File logFile, ISyncResource[] items)
	{
		DateFormat df = new SimpleDateFormat();
		String date = df.format(new Date());

		Writer writer = null;
		try
		{
			if (!logFile.exists())
			{
				logFile.createNewFile();
			}
			writer = new FileWriter(logFile, true);

			StringBuilder builder = new StringBuilder();
			builder.append("File Transfer Log: " + date + FileUtil.NEW_LINE);
			for (ISyncResource iSyncResource : items)
			{
				if (iSyncResource.isSkipped())
				{
					builder.append(" " + iSyncResource.getPath().toString() + ": Skipped");
				}
				else
				{
					builder.append(" " + iSyncResource.getPath().toString() + ": "
							+ getSyncState(iSyncResource.getSyncState()));
				}
				builder.append('\n');
			}

			writer.write(builder.toString());
		}
		catch (IOException e)
		{
			IdeLog.logError(SyncingUIPlugin.getDefault(), e);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	/**
	 * Return the string associated with the item sync state.
	 * 
	 * @param state
	 * @return
	 */
	public static String getSyncState(int state)
	{
		switch (state)
		{
			case SyncState.Ignore:
				return "Ignore"; //$NON-NLS-1$
			case SyncState.ItemsMatch:
				return "Items Match"; //$NON-NLS-1$
			case SyncState.CRCMismatch:
				return "CRC Mismatch"; //$NON-NLS-1$
			case SyncState.ClientItemIsNewer:
				return "Uploaded"; //$NON-NLS-1$
			case SyncState.ServerItemIsNewer:
				return "Downloaded"; //$NON-NLS-1$
			case SyncState.ClientItemOnly:
				return "Uploaded"; //$NON-NLS-1$
			case SyncState.ServerItemOnly:
				return "Downloaded"; //$NON-NLS-1$
			case SyncState.IncompatibleFileTypes:
				return "Incompatible File Types"; //$NON-NLS-1$
			case SyncState.ClientItemDeleted:
				return "Deleted on Client"; //$NON-NLS-1$
			case SyncState.ServerItemDeleted:
				return "Deleted on Server"; //$NON-NLS-1$
			default:
				return "Unknown"; //$NON-NLS-1$
		}
	}
}
