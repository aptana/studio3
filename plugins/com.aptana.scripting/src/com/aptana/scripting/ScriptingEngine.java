/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.scripting.model.RunType;
import com.aptana.scripting.model.ScriptLoadJob;

public class ScriptingEngine
{
	// framework_file extension point
	private static final String FRAMEWORK_FILE_ID = "frameworkFiles"; //$NON-NLS-1$
	private static final String TAG_FILE = "file"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	// loadpath extension point
	private static final String LOADPATH_ID = "loadPaths"; //$NON-NLS-1$
	private static final String TAG_LOADPATH = "loadPath"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	private static ScriptingEngine instance;

	private ScriptingContainer _scriptingContainer;
	private List<String> _loadPaths;
	private List<String> _frameworkFiles;
	private RunType _runType;
	private boolean initialized;

	/**
	 * ScriptingEngine
	 */
	private ScriptingEngine()
	{
		this._runType = ScriptingActivator.getDefaultRunType();
	}

	/**
	 * createScriptingContainer
	 * 
	 * @param scope
	 * @return
	 */
	private ScriptingContainer createScriptingContainer(LocalContextScope scope)
	{
		// ScriptingContainer result = new ScriptingContainer(scope, LocalVariableBehavior.PERSISTENT);
		ScriptingContainer result = new ScriptingContainer(scope, LocalVariableBehavior.TRANSIENT);

		// Fix for: APSTUD-4508 Rubles don't appear to load correctly when Aptana Studio is in a directory with foreign
		// characters.
		// This makes the jruby posix implementation use a java-only implementation which does handle unicode characters
		// properly when on windows.
		result.getProvider().getRubyInstanceConfig().setNativeEnabled(false);

		try
		{
			File jrubyHome = null;
			Bundle jruby = Platform.getBundle("org.jruby"); //$NON-NLS-1$
			// try just exploding the jruby lib dir
			URL url = FileLocator.find(jruby, new Path("lib"), null); //$NON-NLS-1$

			if (url != null)
			{
				File lib = ResourceUtil.resourcePathToFile(url);
				// Ok, now use the parent of exploded lib dir as JRuby Home
				jrubyHome = lib.getParentFile();
			}
			else
			{
				// Ok, just assume the plugin is unpacked and pass the root of the plugin as JRuby Home
				jrubyHome = FileLocator.getBundleFile(jruby);
			}

			result.setHomeDirectory(jrubyHome.getAbsolutePath());

			// TODO Generate two containers? A global one for loading bundles, a threadsafe one for executing
			// commands/snippets/etc?
			// Pre-load 'ruble' framework files!
			List<String> loadPaths = result.getLoadPaths();
			loadPaths.addAll(0, getContributedLoadPaths());
			result.setLoadPaths(loadPaths);
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(Messages.ScriptingEngine_Error_Setting_JRuby_Home,
					new Object[] { e.getMessage() });

			IdeLog.logError(ScriptingActivator.getDefault(), message, e);
			ScriptLogger.logError(message);
		}

		return result;
	}

	/**
	 * getContributedLoadPaths
	 * 
	 * @return
	 */
	public synchronized List<String> getContributedLoadPaths()
	{
		if (this._loadPaths == null)
		{
			final List<String> paths = new ArrayList<String>();

			// @formatter:off
			EclipseUtil.processConfigurationElements(
				ScriptingActivator.PLUGIN_ID,
				LOADPATH_ID,
				new IConfigurationElementProcessor()
				{
					public void processElement(IConfigurationElement element)
					{
						String path = element.getAttribute(ATTR_PATH);

						IExtension declaring = element.getDeclaringExtension();
						String declaringPluginID = declaring.getNamespaceIdentifier();
						Bundle bundle = Platform.getBundle(declaringPluginID);
						URL url = bundle.getEntry(path);
						String urlAsPath = ResourceUtil.resourcePathToString(url);

						if (urlAsPath != null && urlAsPath.length() > 0)
						{
							paths.add(urlAsPath);
						}
						else
						{
							String message = MessageFormat.format(
									Messages.ScriptingEngine_Unable_To_Convert_Load_Path, declaringPluginID, url);

							IdeLog.logError(ScriptingActivator.getDefault(), message);
						}
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(TAG_LOADPATH);
					}
				}
			);
			// @formatter:on

			this._loadPaths = Collections.unmodifiableList(paths);
		}

		return this._loadPaths;
	}

	/**
	 * getFrameworkFiles Used by "ruble.rb" DO NOT REMOVE!
	 * 
	 * @return
	 */
	public synchronized List<String> getFrameworkFiles()
	{
		if (this._frameworkFiles == null)
		{
			final List<String> names = new ArrayList<String>();

			// @formatter:off
			EclipseUtil.processConfigurationElements(
				ScriptingActivator.PLUGIN_ID,
				FRAMEWORK_FILE_ID,
				new IConfigurationElementProcessor()
				{
					public void processElement(IConfigurationElement element)
					{
						names.add(element.getAttribute(ATTR_NAME));
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(TAG_FILE);
					}
				}
			);
			// @formatter:on

			this._frameworkFiles = Collections.unmodifiableList(names);
		}

		return this._frameworkFiles;
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized ScriptingEngine getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptingEngine();
		}

		return instance;
	}

	/**
	 * getScriptingContainer
	 * 
	 * @return
	 */
	public synchronized ScriptingContainer getScriptingContainer()
	{
		if (this._scriptingContainer == null)
		{
			this._scriptingContainer = this.createScriptingContainer(LocalContextScope.SINGLETON);
		}

		return this._scriptingContainer;
	}

	/**
	 * runScript
	 * 
	 * @param fullPath
	 */
	public Object runScript(String fullPath, List<String> loadPaths)
	{
		return this.runScript(fullPath, loadPaths, this._runType, false);
	}

	/**
	 * runScript
	 * 
	 * @param fullPath
	 * @param loadPaths
	 * @param async
	 * @return
	 */
	public Object runScript(String fullPath, List<String> loadPaths, boolean async)
	{
		return this.runScript(fullPath, loadPaths, this._runType, async);
	}

	/**
	 * runScript
	 * 
	 * @param fullPath
	 * @param loadPaths
	 * @param runType
	 * @param async
	 * @return
	 */
	public Object runScript(String fullPath, List<String> loadPaths, RunType runType, boolean async)
	{
		ScriptLoadJob job = new ScriptLoadJob(fullPath, loadPaths);

		try
		{
			job.run("Load '" + fullPath + "'", runType, async); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (InterruptedException e)
		{
			String message = MessageFormat.format(Messages.ScriptingEngine_Error_Executing_Script,
					new Object[] { fullPath });

			ScriptUtils.logErrorWithStackTrace(message, e);
		}

		return (async && this._runType != RunType.CURRENT_THREAD) ? null : job.getReturnValue();
	}

	public synchronized ScriptingContainer getInitializedScriptingContainer()
	{
		ScriptingContainer sc = getScriptingContainer();
		if (!initialized)
		{
			sc.runScriptlet("require 'ruble'"); //$NON-NLS-1$
			initialized = true;
		}
		return sc;
	}
}
