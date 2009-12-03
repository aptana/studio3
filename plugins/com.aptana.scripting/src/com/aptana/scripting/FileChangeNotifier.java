package com.aptana.scripting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import com.aptana.filewatcher.FileWatcher;

/**
 * A class meant to simplify adding and removing file change listeners solely for the scripting API.
 * 
 * @author cwilliams
 */
public class FileChangeNotifier
{

	/**
	 * The interface that a listener conforms to. In Ruby this listener is justa block that JRuby magically proxies to
	 * when this method is called.
	 * 
	 * @author cwilliams
	 */
	public interface IFileChangeListener
	{
		public void fileModified(FileModificationEvent e);
	}

	/**
	 * The event class returned on the callback to the listener.
	 * 
	 * @author cwilliams
	 */
	public static class FileModificationEvent
	{
		public static final int CREATED = 0;
		public static final int DELETED = 1;
		public static final int MODIFIED = 2;
		public static final int RENAMED = 3;

		public String oldName;
		public String name;
		public int type;
	}

	private static Map<IFileChangeListener, Integer> listeners = new HashMap<IFileChangeListener, Integer>();

	/**
	 * Adds a listener for a given filepath.
	 * 
	 * @param filepath
	 * @param watchSubtree
	 * @param listener
	 * @return
	 */
	public static boolean addListener(String filepath, boolean watchSubtree, final IFileChangeListener listener)
	{
		try
		{
			int watchId = FileWatcher.addWatch(filepath, IJNotify.FILE_ANY, watchSubtree, new JNotifyListener()
			{

				public void fileRenamed(int wd, String rootPath, String oldName, String newName)
				{
					if (oldName != null && newName != null && oldName.equals(newName))
					{
						fileModified(wd, rootPath, newName);
						return;
					}
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.RENAMED;
					e.oldName = rootPath + File.separator + oldName;
					e.name = rootPath + File.separator + newName;
					listener.fileModified(e);
				}

				public void fileModified(int wd, String rootPath, String name)
				{
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.MODIFIED;
					e.name = rootPath + File.separator + name;
					e.oldName = e.name;
					listener.fileModified(e);
				}

				public void fileDeleted(int wd, String rootPath, String name)
				{
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.DELETED;
					e.name = rootPath + File.separator + name;
					e.oldName = e.name;
					listener.fileModified(e);
				}

				public void fileCreated(int wd, String rootPath, String name)
				{
					FileModificationEvent e = new FileModificationEvent();
					e.type = FileModificationEvent.CREATED;
					e.name = rootPath + File.separator + name;
					listener.fileModified(e);
				}
			});
			listeners.put(listener, watchId);
			return true;
		}
		catch (JNotifyException e)
		{
			Activator.logError("Error adding file change listener", e);
			return false;
		}

	}

	public static boolean removeListener(IFileChangeListener listener)
	{
		if (listener == null || !listeners.containsKey(listener))
			return false;
		int watchId = listeners.remove(listener);
		try
		{
			FileWatcher.removeWatch(watchId);
			return true;
		}
		catch (JNotifyException e)
		{
			Activator.logError("Error removing file change listener", e);
			return false;
		}
	}

}
