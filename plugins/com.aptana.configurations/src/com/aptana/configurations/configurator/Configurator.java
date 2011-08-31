/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.configurator;

import com.aptana.configurations.processor.ConfigurationProcessorsRegistry;
import com.aptana.configurations.processor.IConfigurationProcessor;

/**
 * A configurator implementation.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class Configurator implements IConfigurator
{

	private String name;
	private String id;
	private String processorId;

	/**
	 * Constructs a new configurator.
	 * 
	 * @param name
	 * @param id
	 * @param processorId
	 */
	public Configurator(String name, String id, String processorId)
	{
		this.name = name;
		this.id = id;
		this.processorId = processorId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.configurator.IConfigurator#getName()
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.configurator.IConfigurator#getId()
	 */
	public String getId()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.configurator.IConfigurator#getProcessor()
	 */
	public IConfigurationProcessor getProcessor()
	{
		return ConfigurationProcessorsRegistry.getInstance().getConfigurationProcessor(processorId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.configurator.IConfigurator#isEnabled()
	 */
	public boolean isEnabled()
	{
		IConfigurationProcessor processor = getProcessor();
		return processor != null && processor.isEnabled();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 * name.hashCode();
		hash = hash * 17 * id.hashCode();
		hash = hash * 11 * processorId.hashCode();
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj.getClass() == Configurator.class)
		{
			Configurator other = (Configurator) obj;
			return name.equals(other.name) && id.equals(other.id) && processorId.equals(other.processorId);
		}
		return false;
	}

}
