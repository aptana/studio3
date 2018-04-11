/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * Status utility class for dealing with {@link IStatus} instances.
 * 
 * @author sgibly@appcelerator.com
 */
public class StatusUtil
{

	/**
	 * Returns a string representation of a given status by collecting all the messages and put each one in a line of
	 * its own. Note that this is most useful when dealing with {@link MultiStatus} instances.<br>
	 * The class recursively drill down and collect all messages. Even from a nested {@link MultiStatus} children of the
	 * given status.
	 * 
	 * @param status
	 * @return A string representation containing all the statuses messages.
	 */
	public static String toString(IStatus status)
	{
		StringBuilder builder = new StringBuilder(status.getMessage());
		IStatus[] children = status.getChildren();
		if (!ArrayUtil.isEmpty(children))
		{
			builder.append(FileUtil.NEW_LINE);
			for (IStatus child : children)
			{
				if (child.isMultiStatus())
				{
					// make a recursive call
					builder.append(toString(child));
				}
				else
				{
					builder.append(child.getMessage());
				}
				builder.append(FileUtil.NEW_LINE);
			}
		}
		return builder.toString();
	}
}
