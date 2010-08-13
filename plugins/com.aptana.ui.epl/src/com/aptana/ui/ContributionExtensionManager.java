package com.aptana.ui;

import java.util.ArrayList;
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
	private static final String NATURE_ID = "natureId"; //$NON-NLS-1$

	private static final String SELECTOR_TAG = "selector"; //$NON-NLS-1$
	public static final String CLASS_TAG = "class"; //$NON-NLS-1$

	private IContributionSelector defaultSelector;

	private Map<String, List<IContributedExtension>> contentTypeToContribMap = new HashMap<String, List<IContributedExtension>>();
	private Map<String, Object> contentTypeToSelectorMap = new HashMap<String, Object>();

	protected ContributionExtensionManager()
	{
		this.defaultSelector = new PriorityContributionSelector();

		loadExtensionPoints();
	}

	public IContributedExtension[] getContributions(String contentType)
	{
		List<IContributedExtension> contributions = getContributionsByContentType(contentType);
		return (IContributedExtension[]) contributions.toArray(new IContributedExtension[contributions.size()]);
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

		return contentTypeToContribMap.get(contentType);
	}

	protected final IContributionSelector getSelector(String natureId)
	{
		return (IContributionSelector) contentTypeToSelectorMap.get(natureId);
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
			List<IContributedExtension> list = contentTypeToContribMap.get(contentType);
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
			List<IContributedExtension> list = contentTypeToContribMap.get(keys.next());

			for (Iterator<IContributedExtension> iter = list.iterator(); iter.hasNext();)
			{
				IContributedExtension contrib = iter.next();
				if (contrib.getId().equals(id))
				{
					return contrib;
				}
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

	protected final void addContribution(String natureId, IConfigurationElement element)
	{
		try
		{
			IContributedExtension object = (IContributedExtension) element.createExecutableExtension(CLASS_TAG);

			if (isValidContribution(object))
			{
				/*
				 * handle the case where the contribution is not the object that was just created.
				 */
				IContributedExtension contrib = (IContributedExtension) configureContribution(object, element);

				List<IContributedExtension> list = contentTypeToContribMap.get(natureId);
				if (list == null)
				{
					list = new ArrayList<IContributedExtension>();
					contentTypeToContribMap.put(natureId, list);
				}

				list.add(contrib);
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	protected final void addSelector(String natureId, IConfigurationElement element)
	{
		try
		{
			Object object = element.createExecutableExtension(CLASS_TAG);
			if (object instanceof IContributionSelector)
			{
				// XXX: what if multiple extensions define a selector
				contentTypeToSelectorMap.put(natureId, object);
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private void loadChildren(String natureId, IConfigurationElement[] innerElements)
	{
		for (int j = 0; j < innerElements.length; j++)
		{
			IConfigurationElement innerElement = innerElements[j];
			String name = innerElement.getName();

			if (name.equals(getContributionElementName()))
			{
				addContribution(natureId, innerElement);
			}
			else if (name.equals(SELECTOR_TAG))
			{
				addSelector(natureId, innerElement);
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
				if (isNatureContribution(main))
				{
					String natureId = main.getAttribute(NATURE_ID);
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
	protected boolean isNatureContribution(IConfigurationElement main)
	{
		return true;
	}

}
