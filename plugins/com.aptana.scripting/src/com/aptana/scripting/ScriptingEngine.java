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
package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.osgi.framework.Bundle;

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

	/**
	 * ScriptingEngine
	 */
	private ScriptingEngine()
	{
		this._runType = Activator.getDefaultRunType();
	}

	/**
	 * createScriptingContainer
	 * 
	 * @param scope
	 * @return
	 */
	public ScriptingContainer createScriptingContainer(LocalContextScope scope)
	{
		// ScriptingContainer result = new ScriptingContainer(scope, LocalVariableBehavior.PERSISTENT);
		ScriptingContainer result = new ScriptingContainer(scope, LocalVariableBehavior.TRANSIENT);

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
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(Messages.ScriptingEngine_Error_Setting_JRuby_Home, new Object[] { e
					.getMessage() });

			Activator.logError(message, e);
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
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<String> paths = new ArrayList<String>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, LOADPATH_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_LOADPATH))
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
											Messages.ScriptingEngine_Unable_To_Convert_Load_Path, new Object[] {
													declaringPluginID, url });

									Activator.logError(message, null);
								}
							}
						}
					}
				}
			}

			this._loadPaths = Collections.unmodifiableList(paths);
		}

		return this._loadPaths;
	}

	/**
	 * getFrameworkFiles
	 * 
	 * @return
	 */
	public synchronized List<String> getFrameworkFiles()
	{
		if (this._frameworkFiles == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<String> names = new ArrayList<String>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, FRAMEWORK_FILE_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_FILE))
							{
								names.add(element.getAttribute(ATTR_NAME));
							}
						}
					}
				}
			}

			this._frameworkFiles = Collections.unmodifiableList(names);
		}

		return this._frameworkFiles;
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static ScriptingEngine getInstance()
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
	public ScriptingContainer getScriptingContainer()
	{
		if (this._scriptingContainer == null)
		{
			this._scriptingContainer = this.createScriptingContainer(LocalContextScope.THREADSAFE);
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
}
