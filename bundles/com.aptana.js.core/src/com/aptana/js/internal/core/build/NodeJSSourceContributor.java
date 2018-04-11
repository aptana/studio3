/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.buildpath.core.BuildPathEntry;
import com.aptana.buildpath.core.IBuildPathContributor;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.logging.IdeLog;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodePackageManager;

public class NodeJSSourceContributor implements IBuildPathContributor
{

	private static final String LIB = "lib"; //$NON-NLS-1$	

	public List<IBuildPathEntry> getBuildPathEntries()
	{
		List<IBuildPathEntry> entries = new ArrayList<IBuildPathEntry>(2);

		// Add paths for NPM packages
		INodePackageManager npm = getNodePackageManager();
		if (npm != null && npm.exists())
		{
			try
			{
				IPath path = npm.getModulesPath();
				entries.add(new BuildPathEntry(MessageFormat.format("NPM Packages: {0}", path), path.toFile().toURI()));
			}
			catch (CoreException e)
			{
				IdeLog.logError(JSCorePlugin.getDefault(), e);
			}
		}

		INodeJS node = getNode();
		if (node != null)
		{
			IPath sourcePath = node.getSourcePath();
			if (sourcePath != null)
			{
				IPath path = sourcePath.append(LIB);
				entries.add(new BuildPathEntry(Messages.NodeJSSourceContributor_Name, path.toFile().toURI()));
			}
		}
		return entries;
	}

	protected INodeJS getNode()
	{
		return JSCorePlugin.getDefault().getNodeJSService().getValidExecutable();
	}

	protected INodePackageManager getNodePackageManager()
	{
		return JSCorePlugin.getDefault().getNodePackageManager();
	}

	public List<IBuildPathEntry> getBuildPathEntries(IProject project)
	{
		return Collections.emptyList();
	}

}
