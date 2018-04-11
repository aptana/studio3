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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import net.contentobjects.jnotify.Util;

/** TODO : added by omry at Dec 11, 2005 : Handle move events */
@SuppressWarnings("nls")
public class JNotifyAdapterLinux implements IJNotify
{
	private Hashtable<Integer, Integer> _linuxWd2Wd;
	private Hashtable<Integer, WatchData> _id2Data;

	/**
	 * A set of files which was added by registerToSubTree (auto-watches)
	 */
	private Hashtable<String, String> _autoWatchesPaths;
	private static int _watchIDCounter = 0;

	public JNotifyAdapterLinux()
	{
		JNotify_linux.setNotifyListener(new INotifyListener()
		{
			public void notify(String name, int wd, int mask, int cookie)
			{
				try
				{
					notifyChangeEvent(name, wd, mask, cookie);
				}
				catch (RuntimeException e)
				{
					e.printStackTrace(System.out);
				}
			}
		});

		_id2Data = new Hashtable<Integer, WatchData>();
		_linuxWd2Wd = new Hashtable<Integer, Integer>();
		_autoWatchesPaths = new Hashtable<String, String>();
	}

	public int addWatch(String path, int mask, boolean watchSubtree, boolean recursive, JNotifyListener listener)
			throws JNotifyException
	{
		JNotify_linux.debug("JNotifyAdapterLinux.addWatch(path=" + path + ",mask=" + Util.getMaskDesc(mask)
				+ ", watchSubtree=" + watchSubtree + ")");

		// map mask to linux inotify mask.
		int linuxMask = 0;
		if ((mask & IJNotify.FILE_CREATED) != 0)
		{
			linuxMask |= JNotify_linux.IN_CREATE;
		}
		if ((mask & IJNotify.FILE_DELETED) != 0)
		{
			linuxMask |= JNotify_linux.IN_DELETE;
			linuxMask |= JNotify_linux.IN_DELETE_SELF;
		}
		if ((mask & IJNotify.FILE_MODIFIED) != 0)
		{
			linuxMask |= JNotify_linux.IN_ATTRIB;
			linuxMask |= JNotify_linux.IN_MODIFY;
		}
		if ((mask & IJNotify.FILE_RENAMED) != 0)
		{
			linuxMask |= JNotify_linux.IN_MOVED_FROM;
			linuxMask |= JNotify_linux.IN_MOVED_TO;
		}

		// if watching subdirs, listen on create anyway.
		// to know when new sub directories are created.
		// these events should not reach the client code.
		if (watchSubtree)
		{
			linuxMask |= JNotify_linux.IN_CREATE;
		}

		WatchData watchData = createWatch(null, true, new File(path), mask, linuxMask, watchSubtree, listener);
		if (watchSubtree)
		{
			try
			{
				File file = new File(path);
				registerToSubTree(true, watchData, file, false);
			}
			catch (JNotifyException e)
			{
				// cleanup
				removeWatch(watchData._wd);
				// and throw.
				throw e;
			}
		}
		return watchData._wd;
	}

	private WatchData createWatch(WatchData parentWatchData, boolean user, File path, int mask, int linuxMask,
			boolean watchSubtree, JNotifyListener listener) throws JNotifyException
	{
		String absPath = path.getPath();
		int wd = _watchIDCounter++;
		int linuxWd = JNotify_linux.addWatch(absPath, linuxMask);
		WatchData watchData = new WatchData(parentWatchData, user, absPath, wd, linuxWd, mask, linuxMask, watchSubtree,
				listener);
		_linuxWd2Wd.put(Integer.valueOf(linuxWd), Integer.valueOf(wd));
		_id2Data.put(Integer.valueOf(wd), watchData);
		if (!user)
		{
			_autoWatchesPaths.put(absPath, absPath);
		}
		return watchData;
	}

	private void registerToSubTree(boolean isRoot, WatchData parentWatch, File root, boolean fireCreatedEvents)
			throws JNotifyException
	{
		if (!parentWatch._user)
		{
			throw new RuntimeException("!parentWatch._user");
		}

		// make sure user really requested to be notified on this event.
		// (in case of recursive listening, this IN_CREATE flag is always on, even if
		// the user is not interester in creation events).
		if (fireCreatedEvents && (parentWatch._mask & IJNotify.FILE_CREATED) != 0)
		{
			String name = root.toString().substring(parentWatch._path.length() + 1);
			parentWatch.notifyFileCreated(name);
		}

		if (root.isDirectory())
		{
			// root was already registered by the calling method.
			if (!isRoot)
			{
				try
				{
					createWatch(parentWatch, false, root, parentWatch._mask, parentWatch._linuxMask,
							parentWatch._watchSubtree, parentWatch._listener);
				}
				catch (JNotifyException e)
				{
					if (e.getErrorCode() == JNotifyException.ERROR_WATCH_LIMIT_REACHED)
						JNotify_linux.warn("JNotifyAdapterLinux.registerToSubTree : warning, failed to register "
								+ root + " :" + e.getMessage());
					{
						throw e;
					}
					// else, on any other error, try subtree anyway..
				}
			}

			String files[] = root.list();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					String file = files[i];
					registerToSubTree(false, parentWatch, new File(root, file), fireCreatedEvents);
				}
			}
		}
	}

	public boolean removeWatch(int wd) throws JNotifyException
	{
		JNotify_linux.debug("JNotifyAdapterLinux.removeWatch(" + wd + ")");

		synchronized (_id2Data)
		{
			if (_id2Data.containsKey(Integer.valueOf(wd)))
			{
				WatchData watchData = _id2Data.get(Integer.valueOf(wd));
				unwatch(watchData);
				return true;
			}
			return false;
		}
	}

	private void unwatch(WatchData data) throws JNotifyException
	{
		JNotifyException ex = null;
		boolean ok = true;
		try
		{
			JNotify_linux.removeWatch(data._linuxWd);
		}
		catch (JNotifyException e)
		{
			e.printStackTrace();
			ex = e;
			ok = false;
		}

		if (data._user)
		{
			for (int i = 0; i < data._subWd.size(); i++)
			{

				int wd = data._subWd.get(i).intValue();
				try
				{
					JNotify_linux.removeWatch(wd);
				}
				catch (JNotifyException e)
				{
					e.printStackTrace();
					ex = e;
					ok = false;
				}
			}
		}
		if (!ok)
		{
			throw ex;
		}
	}

	protected void notifyChangeEvent(String name, int linuxWd, int linuxMask, int cookie)
	{

		if (JNotify_linux.DEBUG)
		{
			debugLinux(name, linuxWd, linuxMask, cookie);
		}

		Integer iwd = _linuxWd2Wd.get(Integer.valueOf(linuxWd));
		if (iwd == null)
		{
			// This happens if an exception is thrown because used too many watches.
			System.out.println("JNotifyAdapterLinux: warning, recieved event for an unregisted LinuxWD " + linuxWd
					+ " ignoring...");
			return;
		}

		WatchData watchData = _id2Data.get(iwd);
		if (watchData != null)
		{
			if ((linuxMask & JNotify_linux.IN_CREATE) != 0)
			{
				File newRootFile = new File(watchData._path, name);
				if (watchData._watchSubtree)
				{
					try
					{
						createWatch(watchData.getParentWatch(), false, newRootFile, watchData._mask,
								watchData._linuxMask, watchData._watchSubtree, watchData._listener);
						// fire events for newly found directories under the new root.
						WatchData parent = watchData.getParentWatch();
						registerToSubTree(true, parent, newRootFile, true);
					}
					catch (JNotifyException e)
					{
						System.out.println("registerToSubTree : warning, failed to register " + newRootFile + " :"
								+ e.getMessage());
					}
				}

				// make sure user really requested to be notified on this event.
				// (in case of recursive listening, this IN_CREATE flag is always on, even if
				// the user is not interester in creation events).
				if ((watchData._mask & IJNotify.FILE_CREATED) != 0)
				{
					// fire an event only if the path is not in the path2Watch,
					// meaning no watch has been created on it.
					if (!_autoWatchesPaths.contains(newRootFile.getPath()))
					{
						watchData.notifyFileCreated(name);
					}
					else
					{
						JNotify_linux.debug("Assuming already sent event for " + newRootFile.getPath());
					}
				}
			}
			else if ((linuxMask & JNotify_linux.IN_DELETE_SELF) != 0)
			{
				watchData.notifyFileDeleted(name);
			}
			else if ((linuxMask & JNotify_linux.IN_DELETE) != 0)
			{
				watchData.notifyFileDeleted(name);
			}
			else if ((linuxMask & JNotify_linux.IN_ATTRIB) != 0 || (linuxMask & JNotify_linux.IN_MODIFY) != 0)
			{
				watchData.notifyFileModified(name);
			}
			else if ((linuxMask & JNotify_linux.IN_MOVED_FROM) != 0)
			{
				watchData.renaming(cookie, name);
			}
			else if ((linuxMask & JNotify_linux.IN_MOVED_TO) != 0)
			{
				watchData.notifyFileRenamed(name, cookie);
			}
			else if ((linuxMask & JNotify_linux.IN_IGNORED) != 0)
			{
				_linuxWd2Wd.remove(Integer.valueOf(watchData._linuxWd));
				synchronized (_id2Data)
				{
					Integer watchDataId = Integer.valueOf(watchData._wd);
					if (_id2Data.contains(watchDataId))
					{
						_id2Data.remove(watchDataId);
					}
				}

				if (!watchData._user)
				{
					_autoWatchesPaths.remove(watchData._path);
					watchData.removeFromParent();
				}
			}
		}
		else
		{
			System.out.println("JNotifyAdapterLinux: warning, recieved event for an unregisted WD " + iwd
					+ ". ignoring...");
		}
	}

	private void debugLinux(String name, int linuxWd, int linuxMask, int cookie)
	{
		boolean IN_ACCESS = (linuxMask & JNotify_linux.IN_ACCESS) != 0;
		boolean IN_MODIFY = (linuxMask & JNotify_linux.IN_MODIFY) != 0;
		boolean IN_ATTRIB = (linuxMask & JNotify_linux.IN_ATTRIB) != 0;
		boolean IN_CLOSE_WRITE = (linuxMask & JNotify_linux.IN_CLOSE_WRITE) != 0;
		boolean IN_CLOSE_NOWRITE = (linuxMask & JNotify_linux.IN_CLOSE_NOWRITE) != 0;
		boolean IN_OPEN = (linuxMask & JNotify_linux.IN_OPEN) != 0;
		boolean IN_MOVED_FROM = (linuxMask & JNotify_linux.IN_MOVED_FROM) != 0;
		boolean IN_MOVED_TO = (linuxMask & JNotify_linux.IN_MOVED_TO) != 0;
		boolean IN_CREATE = (linuxMask & JNotify_linux.IN_CREATE) != 0;
		boolean IN_DELETE = (linuxMask & JNotify_linux.IN_DELETE) != 0;
		boolean IN_DELETE_SELF = (linuxMask & JNotify_linux.IN_DELETE_SELF) != 0;
		boolean IN_MOVE_SELF = (linuxMask & JNotify_linux.IN_MOVE_SELF) != 0;
		boolean IN_UNMOUNT = (linuxMask & JNotify_linux.IN_UNMOUNT) != 0;
		boolean IN_Q_OVERFLOW = (linuxMask & JNotify_linux.IN_Q_OVERFLOW) != 0;
		boolean IN_IGNORED = (linuxMask & JNotify_linux.IN_IGNORED) != 0;
		String s = "";
		if (IN_ACCESS)
			s += "IN_ACCESS, ";
		if (IN_MODIFY)
			s += "IN_MODIFY, ";
		if (IN_ATTRIB)
			s += "IN_ATTRIB, ";
		if (IN_CLOSE_WRITE)
			s += "IN_CLOSE_WRITE, ";
		if (IN_CLOSE_NOWRITE)
			s += "IN_CLOSE_NOWRITE, ";
		if (IN_OPEN)
			s += "IN_OPEN, ";
		if (IN_MOVED_FROM)
			s += "IN_MOVED_FROM, ";
		if (IN_MOVED_TO)
			s += "IN_MOVED_TO, ";
		if (IN_CREATE)
			s += "IN_CREATE, ";
		if (IN_DELETE)
			s += "IN_DELETE, ";
		if (IN_DELETE_SELF)
			s += "IN_DELETE_SELF, ";
		if (IN_MOVE_SELF)
			s += "IN_MOVE_SELF, ";
		if (IN_UNMOUNT)
			s += "IN_UNMOUNT, ";
		if (IN_Q_OVERFLOW)
			s += "IN_Q_OVERFLOW, ";
		if (IN_IGNORED)
			s += "IN_IGNORED, ";
		int wd = _linuxWd2Wd.get(Integer.valueOf(linuxWd)).intValue();
		WatchData wdata = _id2Data.get(Integer.valueOf(wd));
		String path;
		if (wdata != null)
		{
			path = wdata._path;
			if (path != null && name != "")
			{
				path += File.separator + name;
			}
		}
		else
		{
			path = name;
		}
		JNotify_linux.debug("Linux event : wd=" + linuxWd + " | " + s + " path: " + path
				+ (cookie != 0 ? ", cookie=" + cookie : ""));
	}

	private static class WatchData
	{
		boolean _user;
		int _wd;
		private int _linuxWd;
		private ArrayList<Integer> _subWd;
		int _mask;
		int _linuxMask;
		boolean _watchSubtree;
		JNotifyListener _listener;
		/*
		 * if the cookie hashtable is static every WatchData can access the cookies from other WatchData (directories).
		 * The static key word wont be a problem because: i) a cookie is deleted right after its retrival ii) cookies
		 * are only used with rename actions BUG: discovered by Fabio Bernasconi
		 */
		static Hashtable<Integer, String> _cookieToOldName = new Hashtable<Integer, String>();
		String _path;
		WatchData _parentWatchData;

		WatchData(WatchData parentWatchData, boolean user, String path, int wd, int linuxWd, int mask, int linuxMask,
				boolean watchSubtree, JNotifyListener listener)
		{
			if (listener == null)
			{
				throw new IllegalArgumentException("Null listener");
			}
			_parentWatchData = parentWatchData;
			_user = user;
			_subWd = new ArrayList<Integer>();
			_path = path;
			_wd = wd;
			_linuxMask = linuxMask;
			_linuxWd = linuxWd;
			_mask = mask;
			_watchSubtree = watchSubtree;
			_listener = listener;

			if (parentWatchData != null)
			{
				parentWatchData.addSubwatch(_linuxWd);
			}
		}

		public WatchData getParentWatch()
		{
			return _user ? this : _parentWatchData;
		}

		public void removeFromParent()
		{
			if (_parentWatchData == null)
				throw new RuntimeException("no parent");
			_parentWatchData.remveSubwatch(_linuxWd);
		}

		public void renaming(int cookie, String name)
		{
			_cookieToOldName.put(Integer.valueOf(cookie), getOutName(name));
		}

		public void notifyFileRenamed(String name, int cookie)
		{
			String oldName = _cookieToOldName.remove(Integer.valueOf(cookie));
			String outRoot = getOutRoot();
			String outNewName = getOutName(name);
			_listener.fileRenamed(getParentWatchID(), outRoot, oldName, outNewName);
		}

		public void notifyFileModified(String name)
		{
			String outRoot = getOutRoot();
			String outName = getOutName(name);
			_listener.fileModified(getParentWatchID(), outRoot, outName);
		}

		public void notifyFileDeleted(String name)
		{
			String outRoot = getOutRoot();
			String outName = getOutName(name);
			_listener.fileDeleted(getParentWatchID(), outRoot, outName);
		}

		public void notifyFileCreated(String name)
		{
			String outRoot = getOutRoot();
			String outName = getOutName(name);
			_listener.fileCreated(getParentWatchID(), outRoot, outName);
		}

		void remveSubwatch(int linuxWd)
		{
			if (!_subWd.remove(Integer.valueOf(linuxWd)))
			{
				throw new RuntimeException("Error removing " + linuxWd + " from list");
			}
		}

		void addSubwatch(int linuxWd)
		{
			_subWd.add(Integer.valueOf(linuxWd));
		}

		public String toString()
		{
			return "WatchData " + _path + ", wd=" + _wd + ", linuxWd=" + _linuxWd
					+ (_watchSubtree ? ", recursive" : "") + (_user ? ", user" : ", auto");
		}

		private String getOutRoot()
		{
			String outRoot;
			if (_user)
			{
				outRoot = _path;
			}
			else
			// auto watch.
			{
				outRoot = getParentWatch()._path;
			}
			return outRoot;

		}

		private String getOutName(String name)
		{
			String outName;
			if (_user)
			{
				outName = name;
			}
			else
			// auto watch.
			{
				outName = _path.substring(getParentWatch()._path.length() + 1);
				if (name != "")
				{
					outName += File.separatorChar + name;
				}
			}
			return outName;
		}

		public int getParentWatchID()
		{
			return _parentWatchData == null ? _wd : _parentWatchData._wd;
		}
	}

	public int unitTest_getNumWatches()
	{
		return _id2Data.size();
	}

}
