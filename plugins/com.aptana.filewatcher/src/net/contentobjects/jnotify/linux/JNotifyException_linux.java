/*******************************************************************************
 * JNotify - Allow java applications to register to File system events.
 * 
 * Copyright (C) 2005 - Content Objects
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 ******************************************************************************
 *
 * Content Objects, Inc., hereby disclaims all copyright interest in the
 * library `JNotify' (a Java library for file system events). 
 * 
 * Yahali Sherman, 21 November 2005
 *    Content Objects, VP R&D.
 *    
 ******************************************************************************
 * Author : Omry Yadan
 ******************************************************************************/
package net.contentobjects.jnotify.linux;

import net.contentobjects.jnotify.JNotifyException;

public class JNotifyException_linux extends JNotifyException
{
	private static final long serialVersionUID = 1L;

	private static final int LINUX_NO_SUCH_FILE_OR_DIRECTORY = 2;
	private static final int LINUX_PERMISSION_DENIED = 13;
	private static final int LINUX_NO_SPACE_LEFT_ON_DEVICE = 28;

	public JNotifyException_linux(String s, int systemErrorCode)
	{
		super(s, systemErrorCode);
	}

	public int getErrorCode()
	{
		switch (_systemErrorCode)
		{
		case LINUX_PERMISSION_DENIED:
			return ERROR_PERMISSION_DENIED;
		case LINUX_NO_SPACE_LEFT_ON_DEVICE:
			return ERROR_WATCH_LIMIT_REACHED;
		case LINUX_NO_SUCH_FILE_OR_DIRECTORY:
			return ERROR_NO_SUCH_FILE_OR_DIRECTORY;
		default:
			return ERROR_UNSPECIFIED;
		}
	}
}
