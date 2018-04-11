/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.ui.io.Utils;

/**
 * @author Max Stepanov
 */
public final class SyncPresentationUtils
{

	/**
	 * 
	 */
	private SyncPresentationUtils()
	{
	}

	/**
	 * @param element
	 * @return
	 */
	public static String getFileSize(Object element)
	{
		long rawSize = -1;
		if (element instanceof IAdaptable)
		{
			IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
			if (resource != null)
			{
				rawSize = resource.getLocation().toFile().length();
			}
			else
			{
				IFileInfo fileInfo = Utils.getDetailedFileInfo((IAdaptable) element);
				if (fileInfo != null)
				{
					rawSize = fileInfo.getLength();
				}
			}
		}

		if (rawSize >= 0)
		{
			long leftover = 0;
			String string = Long.toString(rawSize) + " B"; //$NON-NLS-1$
			if (rawSize > 1024)
			{
				rawSize = rawSize / 1024;
				leftover = rawSize % 1024;
				long num = rawSize;
				if (leftover >= 512)
				{
					num++;
				}
				string = num + " KB"; //$NON-NLS-1$
			}
			if (rawSize > 1024)
			{
				rawSize = rawSize / 1024;
				leftover = rawSize % 1024;
				long num = rawSize;
				if (leftover >= 512)
				{
					num++;
				}
				string = num + " MB"; //$NON-NLS-1$
			}
			if (rawSize > 1024)
			{
				rawSize = rawSize / 1024;
				leftover = rawSize % 1024;
				long num = rawSize;
				if (leftover >= 512)
				{
					num++;
				}
				string = num + " GB"; //$NON-NLS-1$
			}
			if (rawSize > 1024)
			{
				rawSize = rawSize / 1024;
				leftover = rawSize % 1024;
				long num = rawSize;
				if (leftover >= 512)
				{
					num++;
				}
				string = num + " TB"; //$NON-NLS-1$
			}
			return string;
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @param element
	 * @return
	 */
	public static String getLastModified(Object element)
	{
		long timestamp = -1;
		if (element instanceof IAdaptable)
		{
			IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
			if (resource != null)
			{
				timestamp = resource.getLocalTimeStamp();
			}
			else
			{
				IFileInfo fileInfo = Utils.getDetailedFileInfo((IAdaptable) element);
				if (fileInfo != null)
				{
					timestamp = fileInfo.getLastModified();
				}
			}
		}
		if (timestamp >= 0)
		{
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a"); //$NON-NLS-1$
			return formatter.format(new Date(timestamp));
		}
		return ""; //$NON-NLS-1$
	}

}
