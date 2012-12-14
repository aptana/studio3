/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.buildpath.core.BuildPathEntry;
import com.aptana.buildpath.core.IBuildPathContributor;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.preferences.IPreferenceConstants;

public class NodeJSSourceContributor implements IBuildPathContributor
{

	public List<IBuildPathEntry> getBuildPathEntries()
	{
		String value = Platform.getPreferencesService().getString(JSCorePlugin.PLUGIN_ID,
				IPreferenceConstants.NODEJS_SOURCE_PATH, null, null);
		if (StringUtil.isEmpty(value))
		{
			return null;
		}
		IPath nodeSrcPath = Path.fromOSString(value);

		IPath path = nodeSrcPath.append("lib"); //$NON-NLS-1$
		List<IBuildPathEntry> entries = new ArrayList<IBuildPathEntry>(1);
		entries.add(new BuildPathEntry(Messages.NodeJSSourceContributor_Name, path.toFile().toURI()));
		return entries;
	}

}
