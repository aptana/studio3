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
package net.contentobjects.jnotify.macosx;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

@SuppressWarnings("nls")
public class JNotifyAdapterMacOSX implements IJNotify
{
	/**
	 * A JNFile uniquely identifies a file and stores it's mtime.
	 */
	private static class JNFile implements Comparable<JNFile>
	{
		long mtime;
		int deviceid;
		long inode;

		// load the stat function
		static
		{
			System.loadLibrary("jnotify"); //$NON-NLS-1$
		}

		JNFile(File f) throws IOException
		{
			mtime = f.lastModified();
			stat(f.getAbsolutePath());
		}

		private native void stat(String absolutePath) throws IOException;

		/**
		 * Compares the deviceid and inode of two JNFiles.
		 */
		public int compareTo(JNFile o)
		{
			if (o.deviceid != deviceid)
			{
				return deviceid - o.deviceid;
			}
			if (inode < o.inode)
			{
				return -1;
			}
			if (inode == o.inode)
			{
				return 0;
			}
			return 1;
		}

		/**
		 * Returns true if o refers to the same file as this.
		 */
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof JNFile))
			{
				return false;
			}

			JNFile j = (JNFile) o;
			return j.inode == inode && j.deviceid == deviceid;
		}

		@Override
		public int hashCode()
		{
			return (inode + "," + deviceid).hashCode(); //$NON-NLS-1$
		}

		@Override
		public String toString()
		{
			return String.format("%08x.%016x - %d", deviceid, inode, mtime); //$NON-NLS-1$
		}
	}

	/**
	 * Store information about each watch ID.
	 */
	private Hashtable<Integer, WatchData> _id2Data;

	public JNotifyAdapterMacOSX()
	{
		JNotify_macosx.setNotifyListener(new FSEventListener()
		{
			public void notifyChange(int wd, String rootPath, String filePath, boolean recurse)
			{
				notifyChangeEvent(wd, rootPath, filePath, recurse);
			}

			public void batchStart(int wd)
			{
				batchStartEvent(wd);
			}

			public void batchEnd(int wd)
			{
				batchEndEvent(wd);
			}
		});
		_id2Data = new Hashtable<Integer, WatchData>();
	}

	public int addWatch(String path, int mask, boolean watchSubtree, boolean recursive, JNotifyListener listener)
			throws JNotifyException
	{
		File f;
		try
		{
			f = new File(path).getCanonicalFile();
		}
		catch (IOException e)
		{
			throw new JNotifyException_macosx("Could not resolve canonical path for " + path);
		}
		int wd = JNotify_macosx.addWatch(f.getPath());
		_id2Data.put(Integer.valueOf(wd), new WatchData(wd, mask, listener, path, f, watchSubtree, recursive));
		return wd;
	}

	public boolean removeWatch(int wd) throws JNotifyException
	{
		synchronized (_id2Data)
		{
			boolean removed = _id2Data.remove(Integer.valueOf(wd)) != null;
			if (removed)
			{
				JNotify_macosx.removeWatch(wd);
			}
			return removed;
		}
	}

	/**
	 * A path to scan and whether it needs recursed.
	 */
	private static class ScanJob
	{
		String path;
		boolean recursive;

		ScanJob(String path, boolean recursive)
		{
			this.path = path;
			this.recursive = recursive;
		}

		@Override
		public String toString()
		{
			return path + " " + recursive; //$NON-NLS-1$
		}
	}

	/**
	 * A set of changes detected in a batch.
	 */
	private static class JNEvents
	{
		TreeMap<JNFile, TreeSet<String>> created;
		TreeSet<String> modified;
		TreeMap<JNFile, TreeSet<String>> deleted;
		TreeMap<String, String> renamed;

		JNEvents(int mask)
		{
			if ((mask & FILE_MODIFIED) != 0)
			{
				modified = new TreeSet<String>();
			}
			if ((mask & (FILE_CREATED | FILE_DELETED | FILE_RENAMED)) != 0)
			{
				created = new TreeMap<JNFile, TreeSet<String>>();
				deleted = new TreeMap<JNFile, TreeSet<String>>();
				renamed = new TreeMap<String, String>();
			}
		}
	}

	/**
	 * Data associated with a watch.
	 */
	private static class WatchData
	{
		int _wd;
		int _mask;
		JNotifyListener _notifyListener;
		TreeMap<JNFile, TreeSet<String>> paths;
		TreeMap<String, JNFile> jnfiles;
		LinkedList<ScanJob> toScan;
		String path;
		String fullpath;
		boolean watchSubtree;

		WatchData(int wd, int mask, JNotifyListener listener, String path, File pathFile, boolean watchSubtree,
				boolean recursive)
		{
			_wd = wd;
			_mask = mask;
			_notifyListener = listener;
			this.path = path;
			this.fullpath = pathFile.getPath() + "/"; //$NON-NLS-1$
			this.watchSubtree = watchSubtree;
			paths = new TreeMap<JNFile, TreeSet<String>>();
			jnfiles = new TreeMap<String, JNFile>();
			scan(pathFile, recursive, null);
			toScan = new LinkedList<ScanJob>();
		}

		public String toString()
		{
			return "wd=" + _wd; //$NON-NLS-1$
		}

		/**
		 * Checks a directory for changes.
		 * 
		 * @param job
		 *            the directory to scan
		 * @param events
		 *            the set to place the changes in
		 */
		private void scan(ScanJob job, JNEvents events)
		{
			scan(new File(job.path), job.recursive, events);
		}

		/**
		 * Checks a directory for changes.
		 * 
		 * @param root
		 *            the directory to scan
		 * @param recursive
		 *            whether subdirectories should be scanned
		 * @param events
		 *            the set to place the changes in
		 */
		private void scan(File root, boolean recursive, JNEvents events)
		{
			File[] files = root.listFiles();
			Set<Map.Entry<String, JNFile>> existingfiles = jnfiles.tailMap(root.getAbsolutePath() + "\0").entrySet();
			TreeSet<String> stillAlive = null;
			String rootPath = root.getAbsolutePath();

			// check for created/modified/recreated
			if (files != null)
			{
				// store all the entries for later
				stillAlive = new TreeSet<String>();
				for (int i = 0; i < files.length; i++)
				{
					// get the path relative to rootPath
					String filePath = files[i].getAbsolutePath();
					filePath = filePath.substring(rootPath.length() + 1);
					// use only the next path component
					int slashindex = filePath.indexOf("/"); //$NON-NLS-1$
					if (slashindex >= 0)
					{
						filePath = filePath.substring(0, slashindex);
					}
					// store for later(when looking for deletions)
					stillAlive.add(filePath);

					try
					{
						JNFile jnf = new JNFile(files[i]);
						// check if this inode is already known
						Iterator<Map.Entry<JNFile, TreeSet<String>>> iter = paths.entrySet().iterator();
						Map.Entry<JNFile, TreeSet<String>> oldEntry = null, currentEntry;
						while (iter.hasNext())
						{
							currentEntry = iter.next();
							if (currentEntry.getKey().compareTo(jnf) > 0)
							{
								break;
							}
							oldEntry = currentEntry;
						}
						TreeSet<String> plist;
						if (oldEntry == null || !jnf.equals(oldEntry.getKey()))
						{
							// new inode
							plist = new TreeSet<String>();
							paths.put(jnf, plist);
						}
						else
						{
							// we've seen this inode before
							plist = oldEntry.getValue();
							JNFile oldKey = oldEntry.getKey();
							if (oldKey.mtime != jnf.mtime)
							{
								// file modified
								oldKey.mtime = jnf.mtime;
								// record the changes in events
								// don't do this with directories!
								if (events != null && events.modified != null && !files[i].isDirectory())
								{
									for (String path : plist)
									{
										events.modified.add(path);
									}
								}
							}
						}

						String path = files[i].getAbsolutePath();
						if (!plist.contains(path))
						{
							// new file
							// might not be a new inode
							// add path to inode in map
							plist.add(path);

							// record change in events
							if (events != null && events.created != null)
							{
								TreeSet<String> eplist = events.created.get(jnf);
								if (eplist == null)
								{
									eplist = new TreeSet<String>();
									events.created.put(jnf, eplist);
								}
								eplist.add(path);
							}
						}

						// update the inode and check if the inode has changed
						JNFile oldjnf = jnfiles.put(path, jnf);
						if (oldjnf != null && !jnf.equals(oldjnf))
						{
							// deleted and recreated
							// remove this path from its old inode
							TreeSet<String> oldPaths = paths.get(oldjnf);
							if (oldPaths == null)
							{
								// this shouldn't happen!
							}
							else
							{
								if (!oldPaths.remove(path))
								{
									// this shouldn't happen!
								}
								if (oldPaths.size() == 0)
								{
									// inode is gone
									paths.remove(oldjnf);
								}
							}
							// record this change in events
							// the create event is recorded earlier
							if (events != null && events.deleted != null)
							{
								TreeSet<String> eplist = events.deleted.get(oldjnf);
								if (eplist == null)
								{
									eplist = new TreeSet<String>();
									events.deleted.put(oldjnf, eplist);
								}
								eplist.add(path);
							}
						}

						if (watchSubtree && recursive && files[i].isDirectory())
						{
							scan(files[i], recursive, events);
						}
					}
					catch (IOException e)
					{
						// should be fine here
						e.printStackTrace();
					}
				}
			}

			// check for deleted files
			Iterator<Map.Entry<String, JNFile>> entryit = existingfiles.iterator();
			Iterator<String> stillAliveit = null;
			String lastAlive = null;
			if (stillAlive != null)
			{
				stillAliveit = stillAlive.iterator();
				if (stillAliveit.hasNext())
				{
					lastAlive = stillAliveit.next();
				}
			}
			Map.Entry<String, JNFile> entry;
			while (entryit.hasNext())
			{
				entry = entryit.next();
				String jnp = entry.getKey();
				// only work on the contents of this directory
				if (!jnp.startsWith(rootPath))
					break;

				if (lastAlive != null)
				{
					// get the next path component after rootPath
					String oldFilePath = jnp;
					oldFilePath = oldFilePath.substring(rootPath.length() + 1);
					int slashindex = oldFilePath.indexOf("/"); //$NON-NLS-1$
					if (slashindex >= 0)
					{
						oldFilePath = oldFilePath.substring(0, slashindex);
					}

					int compare = -1;
					while (lastAlive != null && (compare = oldFilePath.compareTo(lastAlive)) > 0)
					{
						if (stillAliveit.hasNext())
						{
							lastAlive = stillAliveit.next();
						}
						else
						{
							lastAlive = null;
						}
					}
					if (compare == 0 && lastAlive != null)
					{
						// this file still exists
						continue;
					}
				}

				// file no longer exists
				JNFile jnf = entry.getValue();
				TreeSet<String> oldPaths = paths.get(jnf);
				// remove this path from its inode
				if (oldPaths == null)
				{
					// this shouldn't happen!
				}
				else
				{
					if (!oldPaths.remove(jnp))
					{
						// this shouldn't happen!
					}
					if (oldPaths.size() == 0)
					{
						// inode is gone
						paths.remove(jnf);
					}
				}

				// remove this path from the map
				entryit.remove();

				// record this change in events
				if (events != null && events.deleted != null)
				{
					TreeSet<String> eplist = events.deleted.get(jnf);
					if (eplist == null)
					{
						eplist = new TreeSet<String>();
						events.deleted.put(jnf, eplist);
					}
					eplist.add(jnp);
				}
			}
		}
	}

	void notifyChangeEvent(int wd, String rootPath, String filePath, boolean recurse)
	{
		synchronized (_id2Data)
		{
			WatchData watchData = _id2Data.get(Integer.valueOf(wd));
			if (watchData != null)
			{
				// queue this job for when the batch ends
				watchData.toScan.add(new ScanJob(filePath, recurse));
			}
		}
	}

	void batchStartEvent(int wd)
	{
		WatchData watchData = _id2Data.get(Integer.valueOf(wd));
		if (watchData != null)
		{
			watchData.toScan.clear();
		}
	}

	void batchEndEvent(int wd)
	{
		WatchData watchData = _id2Data.get(Integer.valueOf(wd));
		if (watchData != null)
		{
			JNEvents e = new JNEvents(watchData._mask);
			// scan all jobs
			ScanJob job;
			while ((job = watchData.toScan.poll()) != null)
			{
				if (watchData.watchSubtree || watchData.fullpath.equals(job.path))
				{
					watchData.scan(job, e);
				}
			}

			// check for renames
			// these are guesses
			// we can't handle a file being renamed and hardlinked in a single batch
			// we can't detect files being renamed into our directory from outside
			if (e.created != null && e.deleted != null && e.renamed != null)
			{
				Iterator<Map.Entry<JNFile, TreeSet<String>>> createdIt = e.created.entrySet().iterator();
				Iterator<Map.Entry<JNFile, TreeSet<String>>> deletedIt = e.deleted.entrySet().iterator();

				Map.Entry<JNFile, TreeSet<String>> created = null;
				if (createdIt.hasNext())
					created = createdIt.next();
				Map.Entry<JNFile, TreeSet<String>> deleted = null;
				if (deletedIt.hasNext())
					deleted = deletedIt.next();

				while (created != null && deleted != null)
				{
					int compare = created.getKey().compareTo(deleted.getKey());
					if (compare < 0)
					{
						if (createdIt.hasNext())
						{
							created = createdIt.next();
							continue;
						}
						break;
					}
					if (compare > 0)
					{
						if (deletedIt.hasNext())
						{
							deleted = deletedIt.next();
							continue;
						}
						break;
					}

					// a deleted file and a created file have the same inode
					// merge into a rename event
					TreeSet<String> createdValue = created.getValue();
					String newpath = createdValue.first();
					createdValue.remove(newpath);
					TreeSet<String> deletedValue = deleted.getValue();
					String oldpath = deletedValue.first();
					deletedValue.remove(oldpath);
					e.renamed.put(oldpath, newpath);

					// this inode is no longer associated with anything
					if (createdValue.size() == 0)
					{
						createdIt.remove();
						created = null;
					}
					if (deletedValue.size() == 0)
					{
						deletedIt.remove();
						deleted = null;
					}

					// try to get new inodes to compare
					if (created == null)
					{
						if (createdIt.hasNext())
						{
							created = createdIt.next();
						}
						else
						{
							// impossible to find more matches
							break;
						}
					}
					if (deleted == null)
					{
						if (deletedIt.hasNext())
						{
							deleted = deletedIt.next();
						}
						else
						{
							// impossible to find more matches
							break;
						}
					}
				}
			}

			// send events
			if ((watchData._mask & FILE_CREATED) != 0)
			{
				for (TreeSet<String> cpaths : e.created.values())
				{
					for (String path : cpaths)
					{
						watchData._notifyListener.fileCreated(wd, watchData.path,
								path.substring(watchData.fullpath.length()));
					}
				}
			}
			if ((watchData._mask & FILE_DELETED) != 0)
			{
				for (TreeSet<String> cpaths : e.deleted.values())
				{
					for (String path : cpaths)
					{
						watchData._notifyListener.fileDeleted(wd, watchData.path,
								path.substring(watchData.fullpath.length()));
					}
				}
			}
			if ((watchData._mask & FILE_MODIFIED) != 0)
			{
				for (String path : e.modified)
				{
					watchData._notifyListener.fileModified(wd, watchData.path,
							path.substring(watchData.fullpath.length()));
				}
			}
			if ((watchData._mask & FILE_RENAMED) != 0)
			{
				for (Map.Entry<String, String> entry : e.renamed.entrySet())
				{
					watchData._notifyListener.fileRenamed(wd, watchData.path,
							entry.getKey().substring(watchData.fullpath.length()),
							entry.getValue().substring(watchData.fullpath.length()));
				}
			}
		}
	}
}
