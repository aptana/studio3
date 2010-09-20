/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
