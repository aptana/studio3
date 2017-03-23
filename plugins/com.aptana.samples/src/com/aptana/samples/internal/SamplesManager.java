/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.samples.IDebugScopes;
import com.aptana.samples.ISampleListener;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.ElementVisibilityListener;
import com.aptana.scripting.model.LoadCycleListener;
import com.aptana.scripting.model.ProjectSampleElement;
import com.aptana.scripting.model.filters.IModelFilter;

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

	private static final String BUNDLE_SCRIPT = "bundle.rb"; //$NON-NLS-1$
	private static final String SAMPLES_SCRIPT = "project_samples.rb"; //$NON-NLS-1$

	private Map<String, SampleCategory> categories;
	private Map<String, List<IProjectSample>> sampleRefsByCategory;
	private Map<String, IProjectSample> samplesById;

	private Map<String, List<IProjectSample>> bundleSamplesByCategory;
	private Map<String, IProjectSample> bundleSamplesById;

	private List<ProjectSampleElement> bundleSamples;

	private List<ISampleListener> sampleListeners;

	private LoadCycleListener loadCycleListener = new LoadCycleListener()
	{

		public void scriptLoaded(File script)
		{
			if (needLoadSamples(script))
			{
				loadBundleSampleElements();
			}
		}

		public void scriptReloaded(File script)
		{
			if (needLoadSamples(script))
			{
				loadBundleSampleElements();
			}
		}

		public void scriptUnloaded(File script)
		{
			if (needLoadSamples(script))
			{
				loadBundleSampleElements();
			}
		}

		private boolean needLoadSamples(File script)
		{
			String scriptPath = script.toString();
			return scriptPath.endsWith(BUNDLE_SCRIPT) || scriptPath.endsWith(SAMPLES_SCRIPT);
		}
	};

	private ElementVisibilityListener elementListener = new ElementVisibilityListener()
	{

		public void elementBecameHidden(AbstractElement element)
		{
			if (element instanceof ProjectSampleElement)
			{
				loadBundleSampleElements();
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
		sampleRefsByCategory = new HashMap<String, List<IProjectSample>>();
		samplesById = new HashMap<String, IProjectSample>();
		bundleSamplesByCategory = new HashMap<String, List<IProjectSample>>();
		bundleSamplesById = Collections.synchronizedMap(new HashMap<String, IProjectSample>());
		bundleSamples = new ArrayList<ProjectSampleElement>();
		sampleListeners = new ArrayList<ISampleListener>();

		readExtensionRegistry();
		loadBundleSampleElements();

		BundleManager.getInstance().addLoadCycleListener(loadCycleListener);
		BundleManager.getInstance().addElementVisibilityListener(elementListener);
	}

	public List<SampleCategory> getCategories()
	{
		List<SampleCategory> sampleCategories = new ArrayList<SampleCategory>();
		sampleCategories.addAll(categories.values());
		Collections.sort(sampleCategories);
		return sampleCategories;
	}

	public List<IProjectSample> getSamplesForCategory(String categoryId)
	{
		List<IProjectSample> result = new ArrayList<IProjectSample>();
		List<IProjectSample> samples = sampleRefsByCategory.get(categoryId);
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

	public IProjectSample getSample(String id)
	{
		IProjectSample sample = samplesById.get(id);
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

	public void addSample(IProjectSample sample)
	{
		String categoryId = sample.getCategory().getId();
		List<IProjectSample> samples = bundleSamplesByCategory.get(categoryId);
		if (samples == null)
		{
			samples = new ArrayList<IProjectSample>();
			bundleSamplesByCategory.put(categoryId, samples);
		}
		String id = sample.getId();
		IProjectSample existingSample = bundleSamplesById.get(id);
		if (existingSample != null)
		{
			samples.remove(existingSample);
		}
		samples.add(sample);
		bundleSamplesById.put(id, sample);

		fireSampleAdded(sample);
	}

	public SampleCategory getCategory(String categoryId)
	{
		return categories.get(categoryId);
	}

	public void addSample(ProjectSampleElement sampleElement)
	{
		String categoryId = sampleElement.getCategory();
		SampleCategory category = categories.get(categoryId);
		if (category != null)
		{
			String id = sampleElement.getId();
			String name = sampleElement.getDisplayName();
			String location = sampleElement.getLocation();
			IPath destination = sampleElement.getDestinationPath();
			File locationFile = new File(location);
			boolean isRemote = !(FileUtil.isZipFile(locationFile) || new File(location).exists()); //$NON-NLS-1$
			if (!isRemote && FileUtil.isZipFile(locationFile))
			{
				// retrieves the absolute location
				location = (new File(sampleElement.getDirectory(), location)).getAbsolutePath();
			}
			String description = sampleElement.getDescription();
			Map<String, URL> iconUrls = sampleElement.getIconUrls();
			SamplesReference sample = new SamplesReference(category, id, name, location, isRemote, description,
					iconUrls, destination, null);
			sample.setNatures(sampleElement.getNatures());

			addSample(sample);
		}
		else
		{
			IdeLog.logWarning(SamplesPlugin.getDefault(),
					MessageFormat.format("No category ''{0}'' exists", categoryId), //$NON-NLS-1$
					IDebugScopes.MANAGER);
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

			List<IProjectSample> samples = sampleRefsByCategory.get(categoryId);
			if (samples == null)
			{
				samples = new ArrayList<IProjectSample>();
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
				catch (Exception e)
				{
					IdeLog.logWarning(SamplesPlugin.getDefault(),
							MessageFormat.format("Unable to retrieve the icon at {0} for sample {1}", iconPath, name), //$NON-NLS-1$
							e);
				}
			}
			Map<String, URL> iconUrls = new HashMap<String, URL>();
			iconUrls.put(SamplesReference.DEFAULT_ICON_KEY, iconUrl);

			SamplesReference samplesRef = new SamplesReference(category, id, name, path, isRemote, description,
					iconUrls, null, element);
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
		List<ProjectSampleElement> elements = BundleManager.getInstance().getProjectSamples(new IModelFilter()
		{
			public boolean include(AbstractElement element)
			{
				return (element instanceof ProjectSampleElement);
			}
		});
		Collections.sort(elements);
		// only reloads the samples from rubles if there has been a difference
		if (elements.isEmpty() || bundleSamples.equals(elements))
		{
			return;
		}
		bundleSamples = elements;

		// removes the existing samples loaded from the rubles
		Collection<IProjectSample> bundles = bundleSamplesById.values();
		Collection<IProjectSample> samples;
		synchronized (bundleSamplesById)
		{
			samples = new ArrayList<IProjectSample>(bundles);
			bundleSamplesByCategory.clear();
			bundleSamplesById.clear();
		}
		for (IProjectSample sample : samples)
		{
			fireSampleRemoved(sample);
		}

		// adds the current list of samples loaded from the rubles
		for (ProjectSampleElement element : bundleSamples)
		{
			addSample(element);
		}
	}

	private void fireSampleAdded(IProjectSample sample)
	{
		if (IdeLog.isInfoEnabled(SamplesPlugin.getDefault(), IDebugScopes.MANAGER))
		{
			IdeLog.logInfo(
					SamplesPlugin.getDefault(),
					MessageFormat.format("Added sample: id = {0}; name = {1}", sample.getId(), sample.getName()), IDebugScopes.MANAGER); //$NON-NLS-1$
		}
		for (ISampleListener listener : sampleListeners)
		{
			listener.sampleAdded(sample);
		}
	}

	private void fireSampleRemoved(IProjectSample sample)
	{
		if (IdeLog.isInfoEnabled(SamplesPlugin.getDefault(), IDebugScopes.MANAGER))
		{
			IdeLog.logInfo(
					SamplesPlugin.getDefault(),
					MessageFormat.format("Removed sample: id = {0}; name = {1}", sample.getId(), sample.getName()), IDebugScopes.MANAGER); //$NON-NLS-1$
		}
		for (ISampleListener listener : sampleListeners)
		{
			listener.sampleRemoved(sample);
		}
	}
}
