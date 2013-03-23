/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.js.core.JSCorePlugin;

public abstract class AbstractRequireResolver implements IRequireResolver
{

	private static final String INDEX = "index"; //$NON-NLS-1$
	private static final String PACKAGE_JSON = "package.json"; //$NON-NLS-1$
	private static final String MAIN = "main"; //$NON-NLS-1$
	protected static final String JS = "js"; //$NON-NLS-1$

	protected IPath loadAsFile(IPath x, String... extensions)
	{
		File file = x.toFile();
		if (file.isFile())
		{
			return x;
		}

		List<String> ext = CollectionsUtil.newList(extensions);
		ext.add(0, JS);

		for (String extension : ext)
		{
			IPath js = x.addFileExtension(extension);
			if (js.toFile().isFile())
			{
				return js;
			}
		}

		return null;
	}

	protected IPath loadAsDirectory(IPath x, String... extensions)
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
				if (!StringUtil.isEmpty(mainFile))
				{
					// package.json may not have a 'main' property set
					IPath m = x.append(mainFile);
					IPath result = loadAsFile(m);
					if (result != null)
					{
						return result;
					}
				}
			}
			catch (CoreException e)
			{
				IdeLog.log(JSCorePlugin.getDefault(), e.getStatus());
			}
		}

		// If package.json doesn't point to a main file, fall back to index.js
		List<String> ext = CollectionsUtil.newList(extensions);
		ext.add(0, JS);

		IPath index = x.append(INDEX);
		for (String extension : ext)
		{
			IPath potential = index.addFileExtension(extension);
			if (potential.toFile().isFile())
			{
				return potential;
			}
		}
		return null;
	}

}
