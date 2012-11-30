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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.PathUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.inferencing.IRequireResolver;
import com.aptana.js.core.preferences.IPreferenceConstants;

/**
 * See http://nodejs.org/api/modules.html#modules_all_together
 * 
 * @author cwilliams
 */
public class NodeModuleResolver implements IRequireResolver
{

	private static final String NODE_MODULES = "node_modules"; //$NON-NLS-1$
	private static final String MAIN = "main"; //$NON-NLS-1$
	private static final String INDEX_NODE = "index.node"; //$NON-NLS-1$
	private static final String INDEX_JS = "index.js"; //$NON-NLS-1$
	private static final String PACKAGE_JSON = "package.json"; //$NON-NLS-1$

	// This is a hack. I copy-pasted from node source's lib dir file listing to here. This is just a way of falling back
	// if we can't actually get the listing from the node src install itself.
	@SuppressWarnings("nls")
	private static final Set<String> CORE_MODULES = CollectionsUtil.newSet("_debugger", "_linklist", "assert",
			"buffer_ieee754", "buffer", "child_process", "cluster", "console", "constants", "crypto", "dgram", "dns",
			"domain", "events", "freelist", "fs", "http", "https", "module", "net", "os", "path", "punycode",
			"querystring", "readline", "repl", "stream", "string_decoder", "sys", "timers", "tls", "tty", "url",
			"util", "vm", "zlib");

	private IPath location;

	/**
	 * @param location
	 * @throws IllegalArgumentException
	 */
	public NodeModuleResolver(IPath location) throws IllegalArgumentException
	{
		if (!location.toFile().isDirectory())
		{
			throw new IllegalArgumentException("location must be a directory"); //$NON-NLS-1$
		}
		this.location = location;
	}

	public IPath resolve(String moduleId)
	{
		IPath result = null;
		// TODO handle core modules - we need node's source to do so!
		if (isCore(moduleId))
		{
			return coreModule(moduleId);
		}

		if (moduleId.startsWith("./") || moduleId.startsWith("/") || moduleId.startsWith("../")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			IPath relative = location.append(moduleId);

			result = loadAsFile(relative);
			if (result == null)
			{
				result = loadAsDirectory(relative);
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
		return node.append("lib").append(text + ".js"); //$NON-NLS-1$//$NON-NLS-2$
	}

	private IPath nodeSrcPath()
	{
		String value = Platform.getPreferencesService().getString(JSCorePlugin.PLUGIN_ID,
				IPreferenceConstants.NODEJS_SOURCE_PATH, null, null);
		if (StringUtil.isEmpty(value))
		{
			return null;
		}
		return Path.fromOSString(value);
	}

	private boolean isCore(String text)
	{
		IPath node = nodeSrcPath();
		if (node != null)
		{
			String[] files = node.append("lib").toFile().list(); //$NON-NLS-1$
			for (String file : files)
			{
				if (file.equals(text + ".js")) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return CORE_MODULES.contains(text);
	}

	private IPath loadAsFile(IPath x)
	{
		File file = x.toFile();
		if (file.isFile())
		{
			return x;
		}

		IPath js = x.addFileExtension("js"); //$NON-NLS-1$
		if (js.toFile().isFile())
		{
			return js;
		}

		IPath node = x.addFileExtension("node"); //$NON-NLS-1$
		if (node.toFile().isFile())
		{
			return node;
		}

		return null;
	}

	private IPath loadAsDirectory(IPath x)
	{
		File packageJSON = x.append(PACKAGE_JSON).toFile();
		if (packageJSON.isFile())
		{
			try
			{
				IFileStore fileStore = EFS.getStore(packageJSON.toURI());
				String rawJSON = IOUtil.read(fileStore.openInputStream(EFS.NONE, new NullProgressMonitor()));
				@SuppressWarnings("rawtypes")
				Map json = (Map) JSON.parse(rawJSON);
				String mainFile = (String) json.get(MAIN);
				IPath m = x.append(mainFile);
				IPath result = loadAsFile(m);
				if (result != null)
				{
					return result;
				}
			}
			catch (CoreException e)
			{
				IdeLog.log(JSCorePlugin.getDefault(), e.getStatus());
			}
		}
		File indexJS = x.append(INDEX_JS).toFile();
		if (indexJS.isFile())
		{
			return x.append(INDEX_JS);
		}

		File indexNode = x.append(INDEX_NODE).toFile();
		if (indexNode.isFile())
		{
			return x.append(INDEX_NODE);
		}
		return null;
	}

	private IPath loadNodeModules(String x, IPath start)
	{
		List<IPath> dirs = nodeModulesPaths(start);
		for (IPath dir : dirs)
		{
			IPath path = loadAsFile(dir.append(x));
			if (path == null)
			{
				path = loadAsDirectory(dir.append(x));
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
				continue;
			}
			IPath dir = start.removeLastSegments(start.segmentCount() - i).append(NODE_MODULES);
			dirs.add(dir);
			i = i - 1;
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
			String nodePrefixValue = JSCorePlugin.getDefault().getNodePackageManager().getConfigValue("prefix"); //$NON-NLS-1$
			IPath nodePrefix = Path.fromOSString(nodePrefixValue);
			dirs.add(nodePrefix.append("lib").append("node")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
		return dirs;
	}

}
