/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.index.core.ui.IndexUiActivator;

/**
 * AbstractProvider
 */
public abstract class AbstractProvider<T>
{
	private List<T> processors;

	/**
	 * addProcessor
	 * 
	 * @param processor
	 */
	protected void addProcessor(T processor)
	{
		if (processors == null)
		{
			processors = new ArrayList<T>();
		}

		processors.add(processor);
	}

	/**
	 * getAttributeName
	 * 
	 * @return
	 */
	public abstract String getAttributeName();

	/**
	 * getElementName
	 * 
	 * @return
	 */
	public String getElementName()
	{
		return "contributor"; //$NON-NLS-1$
	}

	/**
	 * getExtensionPointId
	 * 
	 * @return
	 */
	public String getExtensionPointId()
	{
		return "indexViewContributors"; //$NON-NLS-1$
	}

	/**
	 * getPluginId
	 * 
	 * @return
	 */
	public String getPluginId()
	{
		return IndexUiActivator.PLUGIN_ID;
	}

	/**
	 * getProcessors
	 * 
	 * @return
	 */
	public List<T> getProcessors()
	{
		if (processors == null)
		{
			processors = new ArrayList<T>();

			// Create all the contributed class processors
			loadExtension();
		}

		return processors;
	}

	/**
	 * loadExtension
	 */
	protected void loadExtension()
	{
		// @formatter:off
		EclipseUtil.processConfigurationElements(
			getPluginId(),
			getExtensionPointId(),
			new IConfigurationElementProcessor()
			{
				@SuppressWarnings("unchecked")
				public void processElement(IConfigurationElement element)
				{
					try
					{
						Object instance = element.createExecutableExtension(getAttributeName());

						// would be nice if we could test that instance is of type T
						processors.add((T) instance);
					}
					catch (CoreException e)
					{
						String message = MessageFormat.format(
							"Unable to create executable extension while processing attribute {0} on element {1} for the {2} extension point in the {3} plugin", //$NON-NLS-1$
							getAttributeName(),
							element.getName(),
							getExtensionPointId(),
							getPluginId()
						);

						IdeLog.logError(IndexUiActivator.getDefault(), message, e);
					}
				}

				public Set<String> getSupportElementNames()
				{
					return CollectionsUtil.newSet(getElementName());
				}
			}
		);
		// @formatter:on
	}
}
