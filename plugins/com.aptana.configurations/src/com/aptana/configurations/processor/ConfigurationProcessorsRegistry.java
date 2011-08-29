/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.ElementHandler;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.configurations.ConfigurationsPlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;

/**
 * A registry class for the configuration processors that were loaded to the configurationProcessors extension point.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class ConfigurationProcessorsRegistry
{
	private static final String EXTENSION_POINT_ID = ConfigurationsPlugin.PLUGIN_ID + ".configurationProcessors"; //$NON-NLS-1$
	private static final String TAG_PROCESSOR = "processor"; //$NON-NLS-1$
	private static final String TAG_DELEGATE = "delegate"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_TARGET_ID = "targetID"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$
	private static final String ATT_CATEGORY = "category"; //$NON-NLS-1$

	private static ConfigurationProcessorsRegistry instance = null;
	private Map<String, ProcessorFactory> processors = new HashMap<String, ProcessorFactory>();
	// Maps between the processor and it's delegates. Each processor can be a target at other processors delegates.
	private Map<String, Set<ProcessorFactory>> delegators = new HashMap<String, Set<ProcessorFactory>>();

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
		return processors.get(id).createProcessor();
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

	/**
	 * Returns a set of {@link IConfigurationProcessorDelegate}s that were assigned to the processor with the given id.
	 * 
	 * @param processorID
	 * @return A Set of ConfigurationProcessorDelegate, or an empty set if none were assigned.
	 */
	@SuppressWarnings("unchecked")
	public Set<IConfigurationProcessorDelegate> getProcessorDelegators(String processorID)
	{
		Set<ProcessorFactory> factories = delegators.get(processorID);
		if (factories != null)
		{
			Set<IConfigurationProcessorDelegate> delegatorsSet = new HashSet<IConfigurationProcessorDelegate>(
					factories.size());
			for (ProcessorFactory factory : factories)
			{
				IConfigurationProcessorDelegate delegate = factory.createProcessorDelegate();
				if (delegate != null)
				{
					delegatorsSet.add(delegate);
				}
			}
			return delegatorsSet;
		}
		return Collections.EMPTY_SET;
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
		boolean isProcessor = TAG_PROCESSOR.equals(element.getName());
		boolean isDelegate = TAG_DELEGATE.equals(element.getName());
		if (isProcessor || isDelegate)
		{
			String id = element.getAttribute(ATT_ID);
			if (StringUtil.isEmpty(id))
			{
				return;
			}
			String name = element.getAttribute(ATT_NAME);
			if (StringUtil.isEmpty(name))
			{
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (StringUtil.isEmpty(clazz))
			{
				return;
			}

			if (isProcessor)
			{
				try
				{
					// Don't actually create an instance. Create a factory for that instance.
					ProcessorFactory factory = new ProcessorFactory(element);
					processors.put(id, factory);
				}
				catch (Throwable e)
				{
					IdeLog.logError(ConfigurationsPlugin.getDefault(),
							"Failed creating a configuration processor extension", e); //$NON-NLS-1$
				}
			}
			else
			{
				// It's a delegate
				try
				{
					// Don't actually create an instance. Create a factory for that instance.
					String targetID = element.getAttribute(ATT_TARGET_ID);
					Set<ProcessorFactory> otherDelegates = delegators.get(targetID);
					if (otherDelegates == null)
					{
						otherDelegates = new HashSet<ProcessorFactory>(6);
						delegators.put(targetID, otherDelegates);
					}
					otherDelegates.add(new ProcessorFactory(element));
				}
				catch (Throwable e)
				{
					IdeLog.logError(ConfigurationsPlugin.getDefault(),
							"Failed creating a configuration processor extension", e); //$NON-NLS-1$
				}
			}
		}
	}

	/*
	 * A factory for creating instances of processors and processor delegators from a configuration element.
	 */
	private class ProcessorFactory
	{
		private final IConfigurationElement element;

		public ProcessorFactory(IConfigurationElement element)
		{
			this.element = element;
		}

		/**
		 * Instantiate and return an IConfigurationProcessorDelegate instance.
		 * 
		 * @return IConfigurationProcessorDelegate instance; Null, in case of an error.
		 */
		public IConfigurationProcessorDelegate createProcessorDelegate()
		{
			try
			{
				IConfigurationProcessorDelegate delegate = (IConfigurationProcessorDelegate) element
						.createExecutableExtension(ATT_CLASS);
				delegate.setEnablement(getEnablementExpression());
				return delegate;
			}
			catch (Throwable t)
			{
				IdeLog.logError(ConfigurationsPlugin.getDefault(),
						"Failed creating a configuration processor delegate extension", t); //$NON-NLS-1$
			}
			return null;
		}

		/**
		 * Instantiate and return an AbstractConfigurationProcessor instance.
		 * 
		 * @return AbstractConfigurationProcessor instance; Null, in case of an error.
		 */
		public AbstractConfigurationProcessor createProcessor()
		{
			try
			{
				AbstractConfigurationProcessor processor = (AbstractConfigurationProcessor) element
						.createExecutableExtension(ATT_CLASS);
				processor.setName(element.getAttribute(ATT_NAME));
				processor.setID(element.getAttribute(ATT_ID));
				String categoryStr = element.getAttribute(ATT_CATEGORY);
				if (categoryStr != null && categoryStr.trim().length() > 0)
				{
					processor.setCategories(categoryStr.split(", ")); //$NON-NLS-1$
				}
				processor.setEnablement(getEnablementExpression());
				return processor;
			}
			catch (Throwable t)
			{
				IdeLog.logError(ConfigurationsPlugin.getDefault(),
						"Failed creating a configuration processor extension", t); //$NON-NLS-1$
			}
			return null;
		}

		/**
		 * Returns an enablement expression that is attached to the processor/delegate.
		 * 
		 * @return An enablement {@link Expression}; <code>null</code> if none was attached.
		 */
		private Expression getEnablementExpression()
		{
			IConfigurationElement[] enablement = element.getChildren(ExpressionTagNames.ENABLEMENT);
			if (enablement != null && enablement.length > 0)
			{
				try
				{
					return ElementHandler.getDefault().create(ExpressionConverter.getDefault(), enablement[0]);
				}
				catch (CoreException e)
				{
					IdeLog.logError(ConfigurationsPlugin.getDefault(),
							"Error while creating the configuration enablement expression.", e); //$NON-NLS-1$
					return null;
				}
			}
			// In case there is no 'enablement' element, return null (the processor/delegator is considered enabled).
			return null;
		}
	}
}
