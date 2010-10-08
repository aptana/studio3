/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * If redistributing this code, this entire header must remain intact.
 */
package net.contentobjects.jnotify;

@SuppressWarnings("nls")
public class Util
{
	public static String getMaskDesc(int mask)
	{
		StringBuffer s = new StringBuffer();
		if ((mask & IJNotify.FILE_CREATED) != 0)
		{
			s.append("FILE_CREATED|");
		}
		if ((mask & IJNotify.FILE_DELETED) != 0)
		{
			s.append("FILE_DELETED|");
		}
		if ((mask & IJNotify.FILE_MODIFIED) != 0)
		{
			s.append("FILE_MODIFIED|");
		}
		if ((mask & IJNotify.FILE_RENAMED) != 0)
		{
			s.append("FILE_RENAMED|");
		}
		if (s.length() > 0)
		{
			return s.substring(0, s.length() - 1);
		}
		return "UNKNOWN";
	}
}
