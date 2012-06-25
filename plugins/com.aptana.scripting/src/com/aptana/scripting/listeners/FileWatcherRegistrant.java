/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.listeners;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import com.aptana.core.logging.IdeLog;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ElementVisibilityListener;
import com.aptana.scripting.model.TriggerType;

/**
 * FileWatcherRegistrant
 */
public class FileWatcherRegistrant implements ElementVisibilityListener, JNotifyListener
{
	private static FileWatcherRegistrant INSTANCE;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized FileWatcherRegistrant getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new FileWatcherRegistrant();
			INSTANCE.setup();
		}

		return INSTANCE;
	}

	/**
	 * shutdown
	 */
	public static synchronized void shutdown()
	{
		if (INSTANCE != null)
		{
			INSTANCE.tearDown();

			// loose ref to this instance
			INSTANCE = null;
		}
	}

	private Map<File, Set<CommandElement>> _fileToCommandMap = new HashMap<File, Set<CommandElement>>();
	private Map<CommandElement, Set<File>> _commandToFilesMap = new HashMap<CommandElement, Set<File>>();
	private Map<File, Integer> _fileWatcherId = new HashMap<File, Integer>();
	private Map<Integer, File> _watcherIdFile = new HashMap<Integer, File>();

	/**
	 * FileWatcherRegistrant
	 */
	private FileWatcherRegistrant()
	{
	}

	/**
	 * addCommand
	 * 
	 * @param file
	 * @param element
	 */
	private void addCommand(File file, CommandElement element)
	{
		Set<File> files = this._commandToFilesMap.get(element);

		if (files == null)
		{
			files = new HashSet<File>();

			this._commandToFilesMap.put(element, files);
		}

		files.add(file);
	}

	/**
	 * addFile
	 * 
	 * @param file
	 * @param element
	 */
	private void addFile(File file, CommandElement element)
	{
		Set<CommandElement> commands = this._fileToCommandMap.get(file);

		if (commands == null)
		{
			commands = new HashSet<CommandElement>();

			this._fileToCommandMap.put(file, commands);
		}

		commands.add(element);
	}

	/**
	 * addWatcher
	 * 
	 * @param command
	 */
	protected void addWatcher(CommandElement command)
	{
		// grab the list of files that this command wants to track
		String[] filenames = command.getTriggerTypeValues(TriggerType.FILE_WATCHER);

		if (filenames != null && filenames.length > 0)
		{
			// create a copy of all files we're tracking already
			Set<File> beforeFiles = new HashSet<File>(this._fileToCommandMap.keySet());
			// update our file/command graph

			for (String filename : filenames)
			{
				File file = new File(filename).getAbsoluteFile();

				addFile(file, command);
				addCommand(file, command);
			}

			// create a copy of the new list of files we're tracking
			Set<File> afterFiles = new HashSet<File>(this._fileToCommandMap.keySet());

			// remove the original list from the new list so we know what was added
			afterFiles.removeAll(beforeFiles);

			// create file watchers for each new file we're supposed to track
			for (File file : afterFiles)
			{
				try
				{
					int id = FileWatcher.addWatch(file.getAbsolutePath(), IJNotify.FILE_ANY, true, this);

					this._fileWatcherId.put(file, id);
					this._watcherIdFile.put(id, file);
				}
				catch (JNotifyException e)
				{
					IdeLog.logError(ScriptingActivator.getDefault(), e.getMessage(), e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.scripting.model.ElementChangeListener#elementBecameHidden(com.aptana.scripting.model.AbstractElement)
	 */
	public void elementBecameHidden(AbstractElement element)
	{
		if (element instanceof CommandElement)
		{
			this.removeWatcher((CommandElement) element);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.scripting.model.ElementChangeListener#elementBecameVisible(com.aptana.scripting.model.AbstractElement)
	 */
	public void elementBecameVisible(AbstractElement element)
	{
		if (element instanceof CommandElement)
		{
			this.addWatcher((CommandElement) element);
		}
	}

	/**
	 * execute
	 * 
	 * @param wd
	 * @param type
	 * @param properties
	 */
	private void execute(int wd, String type, String... properties)
	{
		// create property map
		Map<String, String> propertyMap = new HashMap<String, String>();

		// add type
		propertyMap.put("type", type); //$NON-NLS-1$

		// add optional key/values
		int length = properties.length & ~0x01;

		for (int i = 0; i < length; i += 2)
		{
			String name = properties[i];
			String value = properties[i + 1];

			propertyMap.put(name, value);
		}

		// get commands for this watch id
		Set<CommandElement> commands = this.getCommandsByWatchId(wd);

		// and execute each
		for (CommandElement command : commands)
		{
			CommandContext context = command.createCommandContext();

			context.put(TriggerType.FILE_WATCHER.getName(), propertyMap);
			command.execute(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.contentobjects.jnotify.JNotifyListener#fileCreated(int, java.lang.String, java.lang.String)
	 */
	public void fileCreated(int wd, String rootPath, String name)
	{
		this.execute( //
			wd, //
			"created", // //$NON-NLS-1$
			"rootPath", rootPath, // //$NON-NLS-1$
			"name", name // //$NON-NLS-1$
		);
	}

	/*
	 * (non-Javadoc)
	 * @see net.contentobjects.jnotify.JNotifyListener#fileDeleted(int, java.lang.String, java.lang.String)
	 */
	public void fileDeleted(int wd, String rootPath, String name)
	{
		this.execute( //
			wd, //
			"deleted", // //$NON-NLS-1$
			"rootPath", rootPath, // //$NON-NLS-1$
			"name", name // //$NON-NLS-1$
		);
	}

	/*
	 * (non-Javadoc)
	 * @see net.contentobjects.jnotify.JNotifyListener#fileModified(int, java.lang.String, java.lang.String)
	 */
	public void fileModified(int wd, String rootPath, String name)
	{
		this.execute( //
			wd, //
			"modified", // //$NON-NLS-1$
			"rootPath", rootPath, // //$NON-NLS-1$
			"name", name // //$NON-NLS-1$
		);
	}

	/*
	 * (non-Javadoc)
	 * @see net.contentobjects.jnotify.JNotifyListener#fileRenamed(int, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public void fileRenamed(int wd, String rootPath, String oldName, String newName)
	{
		this.execute( //
			wd, //
			"deleted", // //$NON-NLS-1$
			"rootPath", rootPath, // //$NON-NLS-1$
			"oldName", oldName, // //$NON-NLS-1$
			"newName", newName // //$NON-NLS-1$
		);
	}

	/**
	 * getCommandsByWatchId
	 * 
	 * @param wd
	 * @return
	 */
	private Set<CommandElement> getCommandsByWatchId(int wd)
	{
		File file = this._watcherIdFile.get(wd);
		Set<CommandElement> result;

		if (file != null)
		{
			result = this._fileToCommandMap.get(file);
		}
		else
		{
			result = Collections.emptySet();
		}

		return result;
	}

	/**
	 * removeWatcher
	 * 
	 * @param command
	 */
	protected void removeWatcher(CommandElement command)
	{
		if (this._commandToFilesMap.containsKey(command))
		{
			for (File file : this._commandToFilesMap.get(command))
			{
				if (this._fileToCommandMap.containsKey(file))
				{
					Set<CommandElement> commands = this._fileToCommandMap.get(file);

					commands.remove(command);

					if (commands.size() == 0)
					{
						// no more commands watching this file, so remove the watch
						if (this._fileWatcherId.containsKey(file))
						{
							int id = this._fileWatcherId.remove(file);
							this._watcherIdFile.remove(id);

							try
							{
								FileWatcher.removeWatch(id);
							}
							catch (JNotifyException e)
							{
								IdeLog.logError(ScriptingActivator.getDefault(), e.getMessage(), e);
							}
						}

						// remove hash entry as well
						this._fileToCommandMap.remove(file);
					}
				}
			}

			// remove hash entry for this command
			this._commandToFilesMap.remove(command);
		}
	}

	private void setup()
	{
		BundleManager manager = BundleManager.getInstance();

		manager.addElementVisibilityListener(this);
	}

	/**
	 * tearDown
	 */
	private void tearDown()
	{
		BundleManager manager = BundleManager.getInstance();

		manager.removeElementVisibilityListener(this);

		// remove all watches
		for (int id : this._watcherIdFile.keySet())
		{
			try
			{
				FileWatcher.removeWatch(id);
			}
			catch (JNotifyException e)
			{
				IdeLog.logError(ScriptingActivator.getDefault(), e.getMessage(), e);
			}
		}

		// drop all references
		this._commandToFilesMap.clear();
		this._fileToCommandMap.clear();
		this._fileWatcherId.clear();
		this._watcherIdFile.clear();
	}
}
