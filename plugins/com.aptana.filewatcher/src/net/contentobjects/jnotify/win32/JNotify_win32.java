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
package net.contentobjects.jnotify.win32;

import net.contentobjects.jnotify.JNotifyException;


@SuppressWarnings("nls")
public class JNotify_win32
{
	static
	{
		System.loadLibrary("jnotify");
		int res = nativeInit();
		if (res != 0)
		{
			throw new RuntimeException("Error initialiing native library. (#" + res + ")");
		}
	}
	
	public static final int FILE_NOTIFY_CHANGE_FILE_NAME   = 0x00000001;   
	public static final int FILE_NOTIFY_CHANGE_DIR_NAME    = 0x00000002;   
	public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES  = 0x00000004;   
	public static final int FILE_NOTIFY_CHANGE_SIZE        = 0x00000008;   
	public static final int FILE_NOTIFY_CHANGE_LAST_WRITE  = 0x00000010;   
	public static final int FILE_NOTIFY_CHANGE_LAST_ACCESS = 0x00000020;   
	public static final int FILE_NOTIFY_CHANGE_CREATION    = 0x00000040;   
	public static final int FILE_NOTIFY_CHANGE_SECURITY    = 0x00000100;  	
	
	// Event action ids
	public static final int FILE_ACTION_ADDED = 0x00000001;
	public static final int FILE_ACTION_REMOVED = 0x00000002;
	public static final int FILE_ACTION_MODIFIED = 0x00000003;
	public static final int FILE_ACTION_RENAMED_OLD_NAME = 0x00000004;
	public static final int FILE_ACTION_RENAMED_NEW_NAME = 0x00000005;
	
	private static native int nativeInit();
	private static native int nativeAddWatch(String path, long mask, boolean watchSubtree);
	private static native String getErrorDesc(long errorCode); 
	private static native void nativeRemoveWatch(int wd);
	
	private static IWin32NotifyListener _notifyListener;
	
	public static int addWatch(String path, long mask, boolean watchSubtree) throws JNotifyException
	{
		int wd = nativeAddWatch(path, mask, watchSubtree);
		if (wd < 0)
		{
			throw new JNotifyException_win32(getErrorDesc(-wd) + " : " + path, -wd);
		}
		return wd;
	}

	
	public static void removeWatch(int wd)
	{
		nativeRemoveWatch(wd);
	}
	
	
	public static void callbackProcessEvent(int wd, int action, String rootPath, String filePath)
	{
		if (_notifyListener != null)
		{
			_notifyListener.notifyChange(wd, action, rootPath, filePath);
		}
	}
	
	public static void setNotifyListener(IWin32NotifyListener notifyListener)
	{
		if (_notifyListener == null)
		{
			_notifyListener = notifyListener;
		}
		else
		{
			throw new RuntimeException("Notify listener is already set. multiple notify listeners are not supported.");
		}
	}
}
