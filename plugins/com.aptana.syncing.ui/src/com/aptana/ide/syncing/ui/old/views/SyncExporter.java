package com.aptana.ide.syncing.ui.old.views;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				builder.append("\n");
			}

			writer.write(builder.toString());
		}
		catch (IOException e)
		{
			SyncingUIPlugin.logError(e.getLocalizedMessage(), e);
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
				return "Ignore";
			case SyncState.ItemsMatch:
				return "Items Match";
			case SyncState.CRCMismatch:
				return "CRC Mismatch";
			case SyncState.ClientItemIsNewer:
				return "Uploaded";
			case SyncState.ServerItemIsNewer:
				return "Downloaded";
			case SyncState.ClientItemOnly:
				return "Uploaded";
			case SyncState.ServerItemOnly:
				return "Downloaded";
			case SyncState.IncompatibleFileTypes:
				return "Incompatible File Types";
			case SyncState.ClientItemDeleted:
				return "Deleted on Client";
			case SyncState.ServerItemDeleted:
				return "Deleted on Server";
			default:
				return "Unknown";
		}
	}
}
