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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;

public class SamplesManager implements ISamplesManager
{

	private static final String EXTENSION_POINT = SamplesPlugin.PLUGIN_ID + ".samplespath"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ELEMENT_CATEGORY = "category"; //$NON-NLS-1$
	private static final String ELEMENT_SAMPLESINFO = "samplesinfo"; //$NON-NLS-1$
	private static final String ELEMENT_LOCAL = "local"; //$NON-NLS-1$
	private static final String ELEMENT_REMOTE = "remote"; //$NON-NLS-1$
	private static final String ELEMENT_NATURE = "nature"; //$NON-NLS-1$
	private static final String ELEMENT_INCLUDE = "include"; //$NON-NLS-1$
	private static final String ELEMENT_LOCAL_DESCRIPTION = "localDescription"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_DIRECTORY = "directory"; //$NON-NLS-1$
	private static final String ATTR_URL = "url"; //$NON-NLS-1$
	private static final String ATTR_INFOFILE = "infoFile"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$
	private static final String ATTR_CATEGORY = "category"; //$NON-NLS-1$

	private Map<String, SampleCategory> categories;
	private Map<String, List<SamplesReference>> samplesRefs;

	public SamplesManager()
	{
		categories = new HashMap<String, SampleCategory>();
		samplesRefs = new HashMap<String, List<SamplesReference>>();
		readExtensionRegistry();
	}

	public List<SampleCategory> getCategories()
	{
		List<SampleCategory> sampleCategories = new ArrayList<SampleCategory>();
		sampleCategories.addAll(categories.values());
		return sampleCategories;
	}

	public List<SamplesReference> getSamplesForCategory(String categoryId)
	{
		List<SamplesReference> samples = samplesRefs.get(categoryId);
		if (samples == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(samples);
	}

	private void readExtensionRegistry()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);

		for (IConfigurationElement element : elements)
		{
			readElement(element, ELEMENT_CATEGORY);
		}
		for (IConfigurationElement element : elements)
		{
			readElement(element, ELEMENT_SAMPLESINFO);
		}
	}

	private void readElement(IConfigurationElement element, String elementName)
	{
		if (!elementName.equals(element.getName()))
		{
			return;
		}

		if (ELEMENT_CATEGORY.equals(elementName))
		{
			String id = element.getAttribute(ATTR_ID);
			if (StringUtil.isEmpty(id))
			{
				return;
			}
			String name = element.getAttribute(ATTR_NAME);
			if (StringUtil.isEmpty(name))
			{
				return;
			}
			SampleCategory category = new SampleCategory(id, name);
			categories.put(id, category);

			String iconFile = element.getAttribute(ATTR_ICON);
			if (!StringUtil.isEmpty(iconFile))
			{
				Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
				URL url = bundle.getEntry(iconFile);
				category.setIconFile(ResourceUtil.resourcePathToString(url));
			}
		}
		else if (ELEMENT_SAMPLESINFO.equals(elementName))
		{
			// either a local path or remote git url needs to be defined
			boolean isRemote = false;
			String path = null;
			Map<String, String> descriptions = new HashMap<String, String>();
			Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
			IConfigurationElement[] localPaths = element.getChildren(ELEMENT_LOCAL);
			if (localPaths.length > 0)
			{
				IConfigurationElement localPath = localPaths[0];
				String directory = localPath.getAttribute(ATTR_DIRECTORY);
				IConfigurationElement[] toolTipElement = localPath.getChildren(ELEMENT_LOCAL_DESCRIPTION);
				URL url = bundle.getEntry(directory);
				path = ResourceUtil.resourcePathToString(url);
				for (IConfigurationElement toolTip : toolTipElement)
				{
					descriptions.put(toolTip.getAttribute(ATTR_NAME), toolTip.getAttribute(DESCRIPTION));
				}
			}
			else
			{
				IConfigurationElement[] remotePaths = element.getChildren(ELEMENT_REMOTE);
				if (remotePaths.length > 0)
				{
					IConfigurationElement remotePath = remotePaths[0];
					isRemote = true;
					path = remotePath.getAttribute(ATTR_URL);
					descriptions.put(SamplesReference.REMOTE_DESCRIPTION_KEY, remotePath.getAttribute(DESCRIPTION));
				}
			}
			if (StringUtil.isEmpty(path))
			{
				return;
			}

			String categoryId = element.getAttribute(ATTR_CATEGORY);
			SampleCategory category = categories.get(categoryId);
			if (category == null)
			{
				categoryId = "default"; //$NON-NLS-1$
				category = categories.get(categoryId);
				if (category == null)
				{
					category = new SampleCategory(categoryId, Messages.SamplesManager_DefaultCategory_Name);
				}
			}

			List<SamplesReference> samples = samplesRefs.get(categoryId);
			if (samples == null)
			{
				samples = new ArrayList<SamplesReference>();
				samplesRefs.put(categoryId, samples);
			}

			SamplesReference samplesRef = new SamplesReference(category, path, isRemote, element, descriptions);
			samples.add(samplesRef);

			String name = element.getAttribute(ATTR_NAME);
			if (!StringUtil.isEmpty(name))
			{
				samplesRef.setName(name);
			}

			String infoFile = element.getAttribute(ATTR_INFOFILE);
			if (!StringUtil.isEmpty(infoFile))
			{
				URL url = bundle.getEntry(infoFile);
				samplesRef.setInfoFile(ResourceUtil.resourcePathToString(url));
			}

			IConfigurationElement[] natures = element.getChildren(ELEMENT_NATURE);
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

			IConfigurationElement[] includes = element.getChildren(ELEMENT_INCLUDE);
			List<String> includePaths = new ArrayList<String>();
			String includePath;
			URL url;
			for (IConfigurationElement include : includes)
			{
				includePath = include.getAttribute(ATTR_PATH);
				if (!StringUtil.isEmpty(includePath))
				{
					url = bundle.getEntry(includePath);
					path = ResourceUtil.resourcePathToString(url);
					if (path != null)
					{
						includePaths.add(path);
					}
				}
			}
		}
	}
}
