/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;

public abstract class AbstractScriptRunner extends Job implements Runnable
{
	private List<String> _loadPaths;
	private RubyArray _originalLoadPaths;

	/**
	 * AbstractScriptJob
	 * 
	 * @param name
	 */
	public AbstractScriptRunner(String name)
	{
		this(name, null);
	}

	/**
	 * AbstractScriptJob
	 * 
	 * @param name
	 * @param loadPaths
	 */
	public AbstractScriptRunner(String name, List<String> loadPaths)
	{
		super(name);

		this._loadPaths = loadPaths;
	}

	/**
	 * applyLoadPaths
	 * 
	 * @param container
	 */
	protected void applyLoadPaths(Ruby runtime)
	{
		if (this._loadPaths != null && this._loadPaths.size() > 0)
		{
			IRubyObject object = runtime.getLoadService().getLoadPath();

			if (object instanceof RubyArray)
			{
				RubyArray loadPathArray = (RubyArray) object;

				// save copy for later
				this._originalLoadPaths = (RubyArray) loadPathArray.dup();

				// Add our custom load paths
				for (String loadPath : this._loadPaths)
				{
					RubyString toAdd = runtime.newString(loadPath.replace('\\', '/'));

					loadPathArray.append(toAdd);
				}
			}
		}
	}

	/**
	 * register
	 * 
	 * @param runtime
	 */
	protected void registerLibraries(Ruby runtime, String filename)
	{
		if (this._loadPaths != null && this._loadPaths.size() > 0)
		{
			// TODO: only use bundle lib paths
			// build list of load paths that are bundle libs only
			List<File> paths = new LinkedList<File>();

			for (String loadPath : this._loadPaths)
			{
				File path = new File(loadPath);

				if (path.isDirectory() && path.getName().equals(BundleUtils.LIB_DIRECTORY_NAME))
				{
					paths.add(path);
				}
			}

			if (paths.size() > 0)
			{
				IRubyObject object = runtime.getLoadService().getLoadedFeatures();
				LibraryCrossReference xref = LibraryCrossReference.getInstance();

				if (object instanceof RubyArray)
				{
					RubyArray loadedFeaturesArray = (RubyArray) object;

					for (Object featureObject : loadedFeaturesArray)
					{
						if (featureObject instanceof String)
						{
							String feature = (String) featureObject;
							
							// Windows-ify, if necessary
							if (File.separatorChar == '\\')
							{
								feature = feature.replace('/', '\\');
							}

							for (File path : paths)
							{
								File lib = new File(path, feature);

								if (lib.isFile())
								{
									xref.registerLibraryReference(filename, lib.getAbsolutePath());
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * run
	 */
	public void run()
	{
		this.run(new NullProgressMonitor());
	}

	/**
	 * run
	 * 
	 * @param name
	 * @param runType
	 * @throws InterruptedException
	 */
	public void run(String name, RunType runType, boolean async) throws InterruptedException
	{
		switch (runType)
		{
			case JOB:
				this.setPriority(async ? Job.SHORT : Job.INTERACTIVE);
				this.schedule();

				if (async == false)
				{
					this.join();
				}
				break;

			case THREAD:
				Thread thread = new Thread(this, name);
				thread.start();

				if (async == false)
				{
					thread.join();
				}
				break;

			case CURRENT_THREAD:
			default:
				this.run();
		}
	}

	/**
	 * unapplyLoadPaths
	 * 
	 * @param runtime
	 */
	protected void unapplyLoadPaths(Ruby runtime)
	{
		if (this._loadPaths != null && this._loadPaths.size() > 0)
		{
			IRubyObject object = runtime.getLoadService().getLoadPath();

			if (object != null && object instanceof RubyArray)
			{
				RubyArray loadPathArray = (RubyArray) object;

				// Restore original content
				loadPathArray.replace(this._originalLoadPaths);

				// lose reference
				this._originalLoadPaths = null;
			}
		}
	}
}
