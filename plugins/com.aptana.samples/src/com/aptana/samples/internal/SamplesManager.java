/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.SamplesReference;

public class SamplesManager implements ISamplesManager
{

	private static final String EXTENSION_POINT = SamplesPlugin.PLUGIN_ID + ".samplespath"; //$NON-NLS-1$
	private static final String SAMPLES_INFO = "samplesinfo"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_DIRECTORY = "directory"; //$NON-NLS-1$
	private static final String ATTR_INFOFILE = "infoFile"; //$NON-NLS-1$
	private static final String ATTR_ICONFILE = "iconFile"; //$NON-NLS-1$
	private static final String ATTR_NATURE = "nature"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_INCLUDE = "include"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	private List<SamplesReference> samplesRefs;

	public SamplesManager()
	{
		samplesRefs = new ArrayList<SamplesReference>();
		readExtensionRegistry();
	}

	public SamplesReference[] getSamples()
	{
		return samplesRefs.toArray(new SamplesReference[samplesRefs.size()]);
	}

	private void readExtensionRegistry()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);

		for (IConfigurationElement element : elements)
		{
			readElement(element);
		}
	}

	private void readElement(IConfigurationElement element)
	{
		if (SAMPLES_INFO.equals(element.getName()))
		{
			// name and directory are required
			String name = element.getAttribute(ATTR_NAME);
			if (StringUtil.isEmpty(name))
			{
				return;
			}

			String directory = element.getAttribute(ATTR_DIRECTORY);
			if (StringUtil.isEmpty(directory))
			{
				return;
			}
			Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
			URL url = bundle.getEntry(directory);
			String directoryPath = ResourceUtil.resourcePathToString(url);
			if (directoryPath == null)
			{
				return;
			}

			SamplesReference samplesRef = new SamplesReference(name, directoryPath, element);

			// the rest are optional
			String infoFile = element.getAttribute(ATTR_INFOFILE);
			if (!StringUtil.isEmpty(infoFile))
			{
				url = bundle.getEntry(infoFile);
				samplesRef.setInfoFile(ResourceUtil.resourcePathToString(url));
			}

			String iconFile = element.getAttribute(ATTR_ICONFILE);
			if (!StringUtil.isEmpty(iconFile))
			{
				url = bundle.getEntry(iconFile);
				samplesRef.setIconFile(ResourceUtil.resourcePathToString(url));
			}

			IConfigurationElement[] natures = element.getChildren(ATTR_NATURE);
			List<String> natureIds = new ArrayList<String>();
			String natureId;
			for (IConfigurationElement nature : natures)
			{
				natureId = nature.getAttribute(ATTR_ID);
				if (!StringUtil.isEmpty(natureId))
				{
					natureIds.add(natureId);
				}
			}
			samplesRef.setNatures(natureIds.toArray(new String[natureIds.size()]));

			IConfigurationElement[] includes = element.getChildren(ATTR_INCLUDE);
			List<String> includePaths = new ArrayList<String>();
			String includePath;
			for (IConfigurationElement include : includes)
			{
				includePath = include.getAttribute(ATTR_PATH);
				if (!StringUtil.isEmpty(includePath))
				{
					url = bundle.getEntry(includePath);
					String path = ResourceUtil.resourcePathToString(url);
					if (path != null)
					{
						includePaths.add(path);
					}
				}
			}
			samplesRefs.add(samplesRef);
		}
	}
}
