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

import java.util.Hashtable;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import net.contentobjects.jnotify.Util;

public class JNotifyAdapterWin32 implements IJNotify
{
	private Hashtable<Integer, WatchData> _id2Data;

	public JNotifyAdapterWin32()
	{
		JNotify_win32.setNotifyListener(new IWin32NotifyListener()
		{
			public void notifyChange(int wd, int action, String rootPath, String filePath)
			{
				notifyChangeEvent(wd, action, rootPath, filePath);
			}
		});
		_id2Data = new Hashtable<Integer, WatchData>();
	}

	public int addWatch(String path, int mask, boolean watchSubtree, boolean recursive, JNotifyListener listener)
			throws JNotifyException
	{
		// register to everything on system level.
		int wd = JNotify_win32.addWatch(path, JNotify_win32.FILE_NOTIFY_CHANGE_SECURITY
				| JNotify_win32.FILE_NOTIFY_CHANGE_CREATION | JNotify_win32.FILE_NOTIFY_CHANGE_LAST_ACCESS
				| JNotify_win32.FILE_NOTIFY_CHANGE_LAST_WRITE | JNotify_win32.FILE_NOTIFY_CHANGE_SIZE
				| JNotify_win32.FILE_NOTIFY_CHANGE_ATTRIBUTES | JNotify_win32.FILE_NOTIFY_CHANGE_DIR_NAME
				| JNotify_win32.FILE_NOTIFY_CHANGE_FILE_NAME, watchSubtree);
		_id2Data.put(Integer.valueOf(wd), new WatchData(wd, mask, listener));
		return wd;
	}

	public boolean removeWatch(int wd) throws JNotifyException
	{
		synchronized (_id2Data)
		{
			if (_id2Data.containsKey(Integer.valueOf(wd)))
			{
				_id2Data.remove(Integer.valueOf(wd));
				JNotify_win32.removeWatch(wd);
				return true;
			}
			return false;
		}
	}

	private static class WatchData
	{
		int _wd;
		int _mask;
		JNotifyListener _notifyListener;
		public String renameOldName;

		WatchData(int wd, int mask, JNotifyListener listener)
		{
			_wd = wd;
			_mask = mask;
			_notifyListener = listener;
		}

		public String toString()
		{
			return "wd=" + _wd + ", action " + Util.getMaskDesc(_mask); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	void notifyChangeEvent(int wd, int action, String rootPath, String filePath)
	{
		synchronized (_id2Data)
		{
			WatchData watchData = _id2Data.get(Integer.valueOf(wd));
			if (watchData != null)
			{
				int mask = watchData._mask;
				int mapped = mapAction(action);
				if (action == JNotify_win32.FILE_ACTION_ADDED && (mask & mapped) != 0)
				{
					watchData._notifyListener.fileCreated(wd, rootPath, filePath);
				}
				else if (action == JNotify_win32.FILE_ACTION_MODIFIED && (mask & mapped) != 0)
				{
					watchData._notifyListener.fileModified(wd, rootPath, filePath);
				}
				else if (action == JNotify_win32.FILE_ACTION_REMOVED && (mask & mapped) != 0)
				{
					watchData._notifyListener.fileDeleted(wd, rootPath, filePath);
				}
				else if (action == JNotify_win32.FILE_ACTION_RENAMED_OLD_NAME && (mask & mapped) != 0)
				{
					watchData.renameOldName = filePath;
				}
				else if (action == JNotify_win32.FILE_ACTION_RENAMED_NEW_NAME && (mask & mapped) != 0)
				{
					watchData._notifyListener.fileRenamed(wd, rootPath, watchData.renameOldName, filePath);
					watchData.renameOldName = null;
				}
			}
			// else
			// {
			// System.out.println("JNotifyAdapterWin32: IGNORED Unregistered : " + wd + " . " +getDebugWinAction(action)
			// + " root=" + rootPath + " , path=" + filePath);
			// }
		}
	}

	// private static String getDebugWinAction(int action)
	// {
	// switch (action)
	// {
	// case JNotify_win32.FILE_ACTION_ADDED:
	// return "FILE_ACTION_ADDED";
	// case JNotify_win32.FILE_ACTION_MODIFIED:
	// return "FILE_ACTION_MODIFIED";
	// case JNotify_win32.FILE_ACTION_REMOVED:
	// return "FILE_ACTION_REMOVED";
	// case JNotify_win32.FILE_ACTION_RENAMED_NEW_NAME:
	// return "FILE_ACTION_RENAMED_NEW_NAME";
	// case JNotify_win32.FILE_ACTION_RENAMED_OLD_NAME:
	// return "FILE_ACTION_RENAMED_OLD_NAME";
	// default:
	// return "UNKNOWN " + action;
	// }
	// }

	private int mapAction(int action)
	{
		switch (action)
		{
			case JNotify_win32.FILE_ACTION_ADDED:
				return IJNotify.FILE_CREATED;
			case JNotify_win32.FILE_ACTION_MODIFIED:
				return IJNotify.FILE_MODIFIED;
			case JNotify_win32.FILE_ACTION_REMOVED:
				return IJNotify.FILE_DELETED;
			case JNotify_win32.FILE_ACTION_RENAMED_NEW_NAME:
				return IJNotify.FILE_RENAMED;
			case JNotify_win32.FILE_ACTION_RENAMED_OLD_NAME:
				return IJNotify.FILE_RENAMED;
			default:
				return -1; // silently fail, in case future windows versions will add more actions.
		}
	}
}
