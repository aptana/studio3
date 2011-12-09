/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.samples.ISampleListener;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.ElementVisibilityListener;
import com.aptana.scripting.model.ProjectSampleElement;

public class SamplesManager implements ISamplesManager
{

	private static final String EXTENSION_POINT = "samplespath"; //$NON-NLS-1$
	private static final String ELEMENT_CATEGORY = "category"; //$NON-NLS-1$
	private static final String ELEMENT_SAMPLESINFO = "samplesinfo"; //$NON-NLS-1$
	private static final String ELEMENT_LOCAL = "local"; //$NON-NLS-1$
	private static final String ELEMENT_REMOTE = "remote"; //$NON-NLS-1$
	private static final String ELEMENT_NATURE = "nature"; //$NON-NLS-1$
	private static final String ELEMENT_INCLUDE = "include"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATTR_LOCATION = "location"; //$NON-NLS-1$
	private static final String ATTR_INFOFILE = "infoFile"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$
	private static final String ATTR_CATEGORY = "category"; //$NON-NLS-1$

	private Map<String, SampleCategory> categories;
	private Map<String, List<SamplesReference>> sampleRefsByCategory;
	private Map<String, SamplesReference> samplesById;

	private Map<String, List<SamplesReference>> bundleSamplesByCategory;
	private Map<String, SamplesReference> bundleSamplesById;

	private List<ISampleListener> sampleListeners;

	private ElementVisibilityListener elementListener = new ElementVisibilityListener()
	{

		public void elementBecameHidden(AbstractElement element)
		{
			if (element instanceof ProjectSampleElement)
			{
				removeSample((ProjectSampleElement) element);
			}
		}

		public void elementBecameVisible(AbstractElement element)
		{
			if (element instanceof ProjectSampleElement)
			{
				addSample((ProjectSampleElement) element);
			}
		}
	};

	public SamplesManager()
	{
		categories = new HashMap<String, SampleCategory>();
		sampleRefsByCategory = new HashMap<String, List<SamplesReference>>();
		samplesById = new HashMap<String, SamplesReference>();
		bundleSamplesByCategory = new HashMap<String, List<SamplesReference>>();
		bundleSamplesById = new HashMap<String, SamplesReference>();
		sampleListeners = new ArrayList<ISampleListener>();

		readExtensionRegistry();
		loadBundleSampleElements();

		BundleManager.getInstance().addElementVisibilityListener(elementListener);
	}

	public List<SampleCategory> getCategories()
	{
		List<SampleCategory> sampleCategories = new ArrayList<SampleCategory>();
		sampleCategories.addAll(categories.values());
		return sampleCategories;
	}

	public List<SamplesReference> getSamplesForCategory(String categoryId)
	{
		List<SamplesReference> result = new ArrayList<SamplesReference>();
		List<SamplesReference> samples = sampleRefsByCategory.get(categoryId);
		if (samples != null)
		{
			result.addAll(samples);
		}
		samples = bundleSamplesByCategory.get(categoryId);
		if (samples != null)
		{
			result.addAll(samples);
		}
		return result;
	}

	public SamplesReference getSample(String id)
	{
		SamplesReference sample = samplesById.get(id);
		return (sample == null) ? bundleSamplesById.get(id) : sample;
	}

	public void addSampleListener(ISampleListener listener)
	{
		if (!sampleListeners.contains(listener))
		{
			sampleListeners.add(listener);
		}
	}

	public void removeSampleListener(ISampleListener listener)
	{
		sampleListeners.remove(listener);
	}

	private void addSample(ProjectSampleElement sampleElement)
	{
		String categoryId = sampleElement.getCategory();
		SampleCategory category = categories.get(categoryId);
		if (category != null)
		{
			String id = sampleElement.getId();
			String name = sampleElement.getDisplayName();
			String location = sampleElement.getLocation();
			boolean isRemote = !location.toLowerCase().endsWith(".zip"); //$NON-NLS-1$
			if (!isRemote)
			{
				// retrieves the absolute location
				location = (new File(sampleElement.getDirectory(), location)).getAbsolutePath();
			}
			String description = sampleElement.getDescription();
			Map<String, URL> iconUrls = sampleElement.getIconUrls();
			SamplesReference sample = new SamplesReference(category, id, name, location, isRemote, description,
					iconUrls, null);
			sample.setNatures(sampleElement.getNatures());

			List<SamplesReference> samples = bundleSamplesByCategory.get(categoryId);
			if (samples == null)
			{
				samples = new ArrayList<SamplesReference>();
				bundleSamplesByCategory.put(categoryId, samples);
			}
			samples.add(sample);
			bundleSamplesById.put(id, sample);

			fireSampleAdded(sample);
		}
	}

	private void removeSample(ProjectSampleElement sampleElement)
	{
		String categoryId = sampleElement.getCategory();
		SampleCategory category = categories.get(categoryId);
		if (category != null)
		{
			SamplesReference sample = bundleSamplesById.remove(sampleElement.getId());
			if (sample != null)
			{
				List<SamplesReference> samples = bundleSamplesByCategory.get(categoryId);
				samples.remove(sample);

				fireSampleRemoved(sample);
			}
		}
	}

	private void readExtensionRegistry()
	{
		EclipseUtil.processConfigurationElements(SamplesPlugin.PLUGIN_ID, EXTENSION_POINT,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						readElement(element);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newInOrderSet(ELEMENT_CATEGORY, ELEMENT_SAMPLESINFO);
	}
				});
	}

	private void readElement(IConfigurationElement element)
	{
		String elementName = element.getName();

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
			SampleCategory category = new SampleCategory(id, name, element);
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
			String path = null;
			boolean isRemote = false;

			Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
			IConfigurationElement[] localPaths = element.getChildren(ELEMENT_LOCAL);
			if (localPaths.length > 0)
			{
				String location = localPaths[0].getAttribute(ATTR_LOCATION);
				URL url = bundle.getEntry(location);
				path = ResourceUtil.resourcePathToString(url);
			}
			else
			{
				IConfigurationElement[] remotePaths = element.getChildren(ELEMENT_REMOTE);
				if (remotePaths.length > 0)
				{
					path = remotePaths[0].getAttribute(ATTR_LOCATION);
					isRemote = true;
				}
			}
			if (path == null)
			{
				return;
			}

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

			String categoryId = element.getAttribute(ATTR_CATEGORY);
			SampleCategory category = categories.get(categoryId);
			if (category == null)
			{
				categoryId = "default"; //$NON-NLS-1$
				category = categories.get(categoryId);
				if (category == null)
				{
					category = new SampleCategory(categoryId, Messages.SamplesManager_DefaultCategory_Name, element);
				}
			}

			List<SamplesReference> samples = sampleRefsByCategory.get(categoryId);
			if (samples == null)
			{
				samples = new ArrayList<SamplesReference>();
				sampleRefsByCategory.put(categoryId, samples);
			}

			String description = element.getAttribute(ATTR_DESCRIPTION);

			URL iconUrl = null;
			String iconPath = element.getAttribute(ATTR_ICON);
			if (!StringUtil.isEmpty(iconPath))
			{
				URL url = bundle.getEntry(iconPath);
				try
				{
					iconUrl = FileLocator.toFileURL(url);
				}
				catch (IOException e)
				{
					IdeLog.logError(SamplesPlugin.getDefault(),
							MessageFormat.format("Unable to retrieve the icon at {0} for sample {1}", iconPath, name), //$NON-NLS-1$
							e);
				}
			}
			Map<String, URL> iconUrls = new HashMap<String, URL>();
			iconUrls.put(SamplesReference.DEFAULT_ICON_KEY, iconUrl);

			SamplesReference samplesRef = new SamplesReference(category, id, name, path, isRemote, description,
					iconUrls, element);
			samples.add(samplesRef);
			samplesById.put(id, samplesRef);

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
			samplesRef.setIncludePaths(includePaths.toArray(new String[includePaths.size()]));
		}
	}

	private void loadBundleSampleElements()
	{
		List<ProjectSampleElement> elements = BundleManager.getInstance().getProjectSamples(null);
		for (ProjectSampleElement element : elements)
		{
			addSample(element);
		}
	}

	private void fireSampleAdded(SamplesReference sample)
	{
		for (ISampleListener listener : sampleListeners)
		{
			listener.sampleAdded(sample);
		}
	}

	private void fireSampleRemoved(SamplesReference sample)
	{
		for (ISampleListener listener : sampleListeners)
		{
			listener.sampleRemoved(sample);
		}
	}
}
