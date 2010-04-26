package com.aptana.configurations.processor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.configurations.ConfigurationsPlugin;

/**
 * A registry class for the configuration processors that were loaded to the configurationProcessors extension point.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class ConfigurationProcessorsRegistry
{
	private static final String EXTENSION_POINT_ID = ConfigurationsPlugin.PLUGIN_ID + ".configurationProcessors"; //$NON-NLS-1$
	private static final String TAG_PROCESSOR = "processor"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$
	private static final String ATT_CATEGORY = "category"; //$NON-NLS-1$

	private static ConfigurationProcessorsRegistry instance = null;
	private Map<String, AbstractConfigurationProcessor> processors = new HashMap<String, AbstractConfigurationProcessor>();

	// Initialize the registry
	private ConfigurationProcessorsRegistry()
	{
		readExtensionRegistry();
	}

	/**
	 * Returns an instance of this registry.
	 * 
	 * @return a ConfigurationProcessorsRegistry instance
	 */
	public static ConfigurationProcessorsRegistry getInstance()
	{
		if (instance == null)
		{
			instance = new ConfigurationProcessorsRegistry();
		}
		return instance;
	}

	/**
	 * Returns all the registered configuration processors.
	 * 
	 * @return An array of all loaded processors.
	 */
	public AbstractConfigurationProcessor[] getConfigurationProcessors()
	{
		return processors.values().toArray(new AbstractConfigurationProcessor[processors.size()]);
	}

	/**
	 * Returns the AbstractConfigurationProcessor with the given id.
	 * 
	 * @param id
	 *            The id of the AbstractConfigurationProcessor, as was registered in the extension.
	 * @return An instance of AbstractConfigurationProcessor, or null.
	 */
	public AbstractConfigurationProcessor getConfigurationProcessor(String id)
	{
		return processors.get(id);
	}

	/**
	 * Returns an array of all the configuration processors ids that were registered.
	 * 
	 * @return An array of ID's.
	 */
	public String[] getConfigurationProcessorIDs()
	{
		return processors.keySet().toArray(new String[processors.size()]);
	}

	// Load the extensions
	private void readExtensionRegistry()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i)
		{
			readElement(elements[i]);
		}
	}

	private void readElement(IConfigurationElement element)
	{
		if (TAG_PROCESSOR.equals(element.getName()))
		{
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0)
			{
				return;
			}
			String name = element.getAttribute(ATT_NAME);
			if (name == null || name.length() == 0)
			{
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0)
			{
				return;
			}
			try
			{
				AbstractConfigurationProcessor processor = (AbstractConfigurationProcessor) element
						.createExecutableExtension(ATT_CLASS);
				processors.put(id, processor);
				processor.setName(name);
				processor.setID(id);
				String categoryStr = element.getAttribute(ATT_CATEGORY);
				if (categoryStr != null && categoryStr.trim().length() > 0)
				{
					processor.setCategories(categoryStr.split(", ")); //$NON-NLS-1$
				}
			}
			catch (Exception e)
			{
				ConfigurationsPlugin.logError("Failed creating a configuration processor extension", e); //$NON-NLS-1$
			}
		}
	}
}
