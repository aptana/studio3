/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
