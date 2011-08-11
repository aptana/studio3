/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.epl.FormatterPlugin;

/**
 * Abstract base class that can be used to manage extension point contributions that may have more then one
 * implementation.
 * <p>
 * Examples:
 * <ul>
 * <li>Source Parsers
 * <li>Debugging Engines
 * </ul>
 * </p>
 */
public abstract class ContributionExtensionManager
{
	private static final String CONTENT_TYPE = "contentType"; //$NON-NLS-1$

	private static final String SELECTOR_TAG = "selector"; //$NON-NLS-1$
	private static final String CLASS_TAG = "class"; //$NON-NLS-1$
	private static final String REQUIRED_ACTIVE_BUNDLE = "requiredActiveBundle"; //$NON-NLS-1$

	private IContributionSelector defaultSelector;

	private Map<String, List<IConfigurationElement>> contentTypeToContribMap = new HashMap<String, List<IConfigurationElement>>();
	private Map<String, IConfigurationElement> contentTypeToSelectorMap = new HashMap<String, IConfigurationElement>();
	private Map<IConfigurationElement, String> contribToContentTypeMap = new HashMap<IConfigurationElement, String>();

	protected ContributionExtensionManager()
	{
		this.defaultSelector = new PriorityContributionSelector();

		loadExtensionPoints();
	}

	public IContributedExtension[] getContributions(String contentType)
	{
		List<IContributedExtension> contributions = getContributionsByContentType(contentType);
		return contributions.toArray(new IContributedExtension[contributions.size()]);
	}

	public IContributedExtension[] getAllContributions(boolean forceBundleLoading)
	{

		Collection<List<IConfigurationElement>> values = contentTypeToContribMap.values();
		List<IContributedExtension> contributions = new ArrayList<IContributedExtension>();
		for (List<IConfigurationElement> contribution : values)
		{
			contributions.addAll(createContributors(contribution, forceBundleLoading));
		}
		return contributions.toArray(new IContributedExtension[contributions.size()]);
	}

	private List<IContributedExtension> createContributors(List<IConfigurationElement> contributions,
			boolean forceBundleLoading)
	{
		List<IContributedExtension> list = new ArrayList<IContributedExtension>();
		for (IConfigurationElement element : contributions)
		{
			IContributedExtension contrib = createContributor(element, forceBundleLoading);
			if (contrib != null)
			{
				list.add(contrib);
			}
		}
		return list;
	}

	private IContributedExtension createContributor(IConfigurationElement element, boolean forceBundleLoading)
	{
		try
		{
			String requiredBundle = element.getAttribute(REQUIRED_ACTIVE_BUNDLE);
			if (forceBundleLoading || requiredBundle == null || isLoadedBundle(requiredBundle))
			{
				IContributedExtension object = (IContributedExtension) element.createExecutableExtension(CLASS_TAG);
				if (isValidContribution(object))
				{
					/*
					 * handle the case where the contribution is not the object that was just created.
					 */
					IContributedExtension contrib = (IContributedExtension) configureContribution(object, element);
					return contrib;
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns true if the given configuration element is in a bundle that was already loaded.
	 */
	public static boolean isLoadedBundle(String bundle)
	{
		Bundle b = Platform.getBundle(bundle);
		return b != null && b.getState() == Bundle.ACTIVE;
	}

	public IContributedExtension getSelectedContribution(String contentType)
	{
		IContributedExtension[] contributions = getContributions(contentType);
		if (contributions.length > 0)
		{
			IContributionSelector selector = getSelector(contentType);
			if (selector == null)
			{
				selector = defaultSelector;
			}
			return selector.select(contributions, null);
		}
		return null;
	}

	/**
	 * Retrieves a registered contribution based upon its priority.
	 * 
	 * @param project
	 *            project
	 * @param natureId
	 *            nature id
	 */
	public IContributedExtension getPriorityContribution(IProject project, String natureId)
	{
		IContributedExtension[] contributions = getContributions(natureId);
		return defaultSelector.select(contributions, project);
	}

	/**
	 * Get the contributions registered for the given nature id
	 * 
	 * @param contentType
	 *            nature id
	 * @return list of avaiable contributions or <code>Collections.EMPTY_LIST</code> if no contributions have been
	 *         registered by the plugin
	 */
	protected final List<IContributedExtension> getContributionsByContentType(String contentType)
	{
		if (!hasContributions(contentType))
		{
			return Collections.emptyList();
		}

		return createContributors(contentTypeToContribMap.get(contentType), false);
	}

	protected final IContributionSelector getSelector(String natureId)
	{
		IConfigurationElement ice = contentTypeToSelectorMap.get(natureId);
		if (ice == null)
		{
			return null;
		}
		try
		{
			Object object = ice.createExecutableExtension(CLASS_TAG);
			if (object instanceof IContributionSelector)
			{
				return (IContributionSelector) object;
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(FormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
		return null;
	}

	/**
	 * Checks if any contributions have been created for the given nature id
	 * 
	 * @param contentType
	 *            nature id
	 * @return true if there are contributions, false otherwise
	 */
	protected final boolean hasContributions(String contentType)
	{
		if (contentTypeToContribMap.containsKey(contentType))
		{
			List<IConfigurationElement> list = contentTypeToContribMap.get(contentType);
			return !list.isEmpty();
		}

		return false;
	}

	/**
	 * Has a selector been configured for the contribution
	 * 
	 * @param contentType
	 *            the content type
	 * @return true if a selector has been configured, false otherwise
	 */
	public final boolean hasSelector(String contentType)
	{
		return contentTypeToSelectorMap.containsKey(contentType);
	}

	/**
	 * Returns a contributed extension implementation based on id.
	 * 
	 * @param id
	 *            contribution id
	 * @return contribution implementation
	 */
	public final IContributedExtension getContributionById(String id)
	{
		Iterator<String> keys = contentTypeToContribMap.keySet().iterator();
		while (keys.hasNext())
		{
			List<IConfigurationElement> list = contentTypeToContribMap.get(keys.next());

			for (Iterator<IConfigurationElement> iter = list.iterator(); iter.hasNext();)
			{
				IConfigurationElement contrib = iter.next();
				if (contrib.getAttribute(IContributedExtension.ID).equals(id))
				{
					return createContributor(contrib, false);
				}
			}
		}

		return null;
	}

	/**
	 * Returns the content-type that was declared for the given contribution extension.
	 * 
	 * @param contribution
	 * @return The content-type that the given contribution was set for.
	 */
	protected String getContentTypeByContribution(IContributedExtension contribution)
	{
		for (IConfigurationElement ice : contribToContentTypeMap.keySet())
		{
			String className = ice.getAttribute(CLASS_TAG);
			if (className.equals(contribution.getClass().getName()))
			{
				return contribToContentTypeMap.get(ice);
			}
		}
		return null;
	}

	/**
	 * Returns the name of the contribution xml element
	 */
	protected abstract String getContributionElementName();

	/**
	 * Returns the name of the extension point to load
	 */
	protected abstract String getExtensionPoint();

	/**
	 * Checks if the passed object is valid for the given contribution.
	 * <p>
	 * The passed object will have been created via a call to
	 * {@link IConfigurationElement#createExecutableExtension(String)}
	 * </p>
	 * 
	 * @param object
	 *            contribution implementation class
	 * @return true if valid, false otherwise
	 */
	protected abstract boolean isValidContribution(Object object);

	/**
	 * Configure the object being contributed with any configuration data it may need.
	 * <p>
	 * Sub-classes should override this method if the input object was not configured using
	 * {@link org.eclipse.core.runtime.IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)}
	 * </p>
	 */
	protected Object configureContribution(Object object, IConfigurationElement config)
	{
		return object;
	}

	protected final void addContribution(String contentType, IConfigurationElement element)
	{
		List<IConfigurationElement> list = contentTypeToContribMap.get(contentType);
		if (list == null)
		{
			list = new ArrayList<IConfigurationElement>();
		}
		list.add(element);
		contentTypeToContribMap.put(contentType, list);
		contribToContentTypeMap.put(element, contentType);
	}

	protected final void addSelector(String natureId, IConfigurationElement element)
	{
		// XXX: what if multiple extensions define a selector
		contentTypeToSelectorMap.put(natureId, element);
	}

	private void loadChildren(String contentType, IConfigurationElement[] innerElements)
	{

		for (IConfigurationElement innerElement : innerElements)
		{
			String name = innerElement.getName();

			if (name.equals(getContributionElementName()))
			{
				addContribution(contentType, innerElement);
			}
			else if (name.equals(SELECTOR_TAG))
			{
				addSelector(contentType, innerElement);
			}
		}
	}

	private void loadExtensionPoints()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtension[] extensions = registry.getExtensionPoint(getExtensionPoint()).getExtensions();

		for (int i = 0; i < extensions.length; i++)
		{
			IExtension extension = extensions[i];
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement main : elements)
			{
				if (isContentTypeContribution(main))
				{
					String natureId = main.getAttribute(CONTENT_TYPE);
					IConfigurationElement[] innerElements = main.getChildren();
					loadChildren(natureId, innerElements);
				}
			}
		}
	}

	/**
	 * @param main
	 * @return
	 * @since 2.0
	 */
	protected boolean isContentTypeContribution(IConfigurationElement main)
	{
		return true;
	}

}
