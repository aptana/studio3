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
package com.aptana.syncing.core.internal;

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

import com.aptana.ide.core.io.vfs.IExtendedFileStore;
import com.aptana.ide.syncing.core.SyncingPlugin;

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

	public static void copy(IFileStore source, IFileInfo sourceInfo, IFileStore destination, int options,
			IProgressMonitor monitor) throws CoreException
	{
		try
		{
			monitor = (monitor == null) ? new NullProgressMonitor() : monitor;
			checkCanceled(monitor);
			monitor.beginTask("", sourceInfo == null ? 3 : 2); //$NON-NLS-1$
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
				try
				{
					in = source.openInputStream(EFS.NONE, subMonitorFor(monitor, 0));
					out = destination.openOutputStream(EFS.NONE, subMonitorFor(monitor, 0));
					IProgressMonitor subMonitor = subMonitorFor(monitor, 2);
					subMonitor.beginTask(MessageFormat.format(Messages.SyncUtils_Copying, source.toString()), totalWork);
					while (true)
					{
						int bytesRead = -1;
						try
						{
							bytesRead = in.read(buffer);
						}
						catch (IOException e)
						{
							error(MessageFormat.format(Messages.SyncUtils_ERR_Reading, source.toString()), e);
						}
						if (bytesRead == -1)
							break;
						try
						{
							out.write(buffer, 0, bytesRead);
						}
						catch (IOException e)
						{
							error(MessageFormat.format(Messages.SyncUtils_ERR_Writing, destination.toString()), e);
						}
						subMonitor.worked(1);
					}
					subMonitor.done();
				}
				finally
				{
					safeClose(in);
					safeClose(out);
				}
			}
			destination.putInfo(sourceInfo, EFS.SET_ATTRIBUTES | EFS.SET_LAST_MODIFIED | options, subMonitorFor(
					monitor, 1));
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
		throw new CoreException(new Status(IStatus.ERROR, SyncingPlugin.PLUGIN_ID, message, e));
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
		}
	}

	private static void safeClose(OutputStream out)
	{
		try
		{
			if (out != null)
			{
				out.close();
			}
		}
		catch (IOException ignore)
		{
		}
	}

}
