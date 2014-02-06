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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.buildpath.core.BuildPathEntry;
import com.aptana.buildpath.core.IBuildPathContributor;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodePackageManager;
import com.aptana.js.core.preferences.IPreferenceConstants;

public class NodeJSSourceContributor implements IBuildPathContributor
{

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

		String value = Platform.getPreferencesService().getString(JSCorePlugin.PLUGIN_ID,
				IPreferenceConstants.NODEJS_SOURCE_PATH, null, null);
		if (!StringUtil.isEmpty(value))
		{
			IPath nodeSrcPath = Path.fromOSString(value);
			IPath path = nodeSrcPath.append("lib"); //$NON-NLS-1$	
			entries.add(new BuildPathEntry(Messages.NodeJSSourceContributor_Name, path.toFile().toURI()));
		}
		return entries;
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
