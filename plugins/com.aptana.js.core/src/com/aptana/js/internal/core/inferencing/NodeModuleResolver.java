/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.PathUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.inferencing.AbstractRequireResolver;

/**
 * See http://nodejs.org/api/modules.html#modules_all_together
 * 
 * @author cwilliams
 */
public class NodeModuleResolver extends AbstractRequireResolver
{

	private static final String LIB = "lib"; //$NON-NLS-1$
	private static final String NODE_MODULES = "node_modules"; //$NON-NLS-1$
	private static final String NODE = "node"; //$NON-NLS-1$

	// This is a hack. I copy-pasted from node source's lib dir file listing to here. This is just a way of falling back
	// if we can't actually get the listing from the node src install itself.
	@SuppressWarnings("nls")
	private static final Set<String> CORE_MODULES = CollectionsUtil.newSet("_debugger", "_linklist", "assert",
			"buffer_ieee754", "buffer", "child_process", "cluster", "console", "constants", "crypto", "dgram", "dns",
			"domain", "events", "freelist", "fs", "http", "https", "module", "net", "os", "path", "punycode",
			"querystring", "readline", "repl", "stream", "string_decoder", "sys", "timers", "tls", "tty", "url",
			"util", "vm", "zlib");

	private IPath location;

	public IPath resolve(String moduleId, IProject project, IPath location, IPath indexRoot)
	{
		if (!location.toFile().isDirectory())
		{
			throw new IllegalArgumentException("location must be a directory"); //$NON-NLS-1$
		}
		this.location = location;

		IPath result = null;
		if (isCore(moduleId))
		{
			return coreModule(moduleId);
		}

		if (moduleId.startsWith("./") || moduleId.startsWith("/") || moduleId.startsWith("../")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			IPath relative = location.append(moduleId);

			result = loadAsFile(relative, NODE);
			if (result == null)
			{
				result = loadAsDirectory(relative, NODE);
			}
		}

		if (result == null)
		{
			result = loadNodeModules(moduleId, location);
		}

		// 4. THROW "not found"
		return result;
	}

	private IPath coreModule(String text)
	{
		IPath node = nodeSrcPath();
		if (node == null)
		{
			return null;
		}
		return node.append(LIB).append(text).addFileExtension(JS);
	}

	protected synchronized IPath nodeSrcPath()
	{
		return JSCorePlugin.getDefault().getNodeJSService().getValidExecutable().getSourcePath();
	}

	private boolean isCore(String text)
	{
		IPath node = nodeSrcPath();
		if (node != null)
		{
			String[] files = node.append(LIB).toFile().list();
			if (!ArrayUtil.isEmpty(files))
			{
				for (String file : files)
				{
					if (file.equals(text + ".js")) //$NON-NLS-1$
					{
						return true;
					}
				}
			}
		}
		return CORE_MODULES.contains(text);
	}

	private IPath loadNodeModules(String x, IPath start)
	{
		List<IPath> dirs = nodeModulesPaths(start);
		for (IPath dir : dirs)
		{
			IPath path = loadAsFile(dir.append(x), NODE);
			if (path == null)
			{
				path = loadAsDirectory(dir.append(x), NODE);
			}
			if (path != null)
			{
				return path;
			}
		}
		return null;
	}

	private List<IPath> nodeModulesPaths(IPath start)
	{
		String[] parts = start.segments();
		int root = 0;
		for (int x = 0; x < parts.length; x++)
		{
			if (NODE_MODULES.equals(parts[x]))
			{
				root = x;
				break;
			}
		}
		int i = parts.length - 1;
		List<IPath> dirs = new ArrayList<IPath>();
		while (i > root)
		{
			if (NODE_MODULES.equals(parts[i]))
			{
				i--;
				continue;
			}
			IPath dir = start.removeLastSegments(start.segmentCount() - i).append(NODE_MODULES);
			dirs.add(dir);
			i--;
		}

		// Search global folders, see http://nodejs.org/api/modules.html#modules_loading_from_the_global_folders
		dirs.addAll(globalFolders());
		return dirs;
	}

	private Collection<? extends IPath> globalFolders()
	{
		List<IPath> dirs = new ArrayList<IPath>();
		// FIXME Handle properly on Windows...
		Map<String, String> env = ShellExecutable.getEnvironment(location);
		String nodePath = env.get("NODE_PATH"); //$NON-NLS-1$
		if (nodePath != null)
		{
			// Split like PATH and add to dirs
			String pathENV = PathUtil.convertPATH(nodePath);
			String[] paths = pathENV.split(File.pathSeparator);
			for (String path : paths)
			{
				dirs.add(Path.fromOSString(path));
			}
		}
		String home = env.get("HOME"); //$NON-NLS-1$
		if (home != null)
		{
			IPath homePath = Path.fromOSString(home);
			dirs.add(homePath.append(".node_modules")); //$NON-NLS-1$
			dirs.add(homePath.append(".node_libraries")); //$NON-NLS-1$
		}

		// Grab node_prefix setting!
		try
		{
			IPath modulesPath = getModulesPath();
			if (modulesPath != null)
			{
				dirs.add(modulesPath);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
		return dirs;
	}

	protected synchronized IPath getModulesPath() throws CoreException
	{
		return JSCorePlugin.getDefault().getNodePackageManager().getModulesPath();
	}

	public boolean applies(IProject project, IPath currentDirectory, IPath indexRoot)
	{
		// TODO This applies to projects that use node modules. What are those? Alloy? Node.ACS? Regular Web projects?
		// Is there some way to tell if a project is hooked up to node?
		return true;
	}

	public List<String> getPossibleModuleIds(IProject project, IPath currentDirectory, IPath indexRoot)
	{
		// Suggest core modules
		Set<String> moduleIds = new HashSet<String>();
		IPath node = nodeSrcPath();
		if (node != null)
		{
			String[] files = node.append(LIB).toFile().list();
			if (!ArrayUtil.isEmpty(files))
			{
				for (String file : files)
				{
					if (file.endsWith(".js"))
					{
						file = file.substring(0, file.length() - 3);
					}
					moduleIds.add(file);
				}
			}
		}
		moduleIds.addAll(CORE_MODULES);
		// TODO Handle suggesting relative paths?
		// TODO Suggest modules found in node_modules paths up the directory hierarchy!
		return new ArrayList<String>(moduleIds);
	}
}
