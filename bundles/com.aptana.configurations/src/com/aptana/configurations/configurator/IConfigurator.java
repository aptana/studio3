/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.configurator;

import com.aptana.configurations.processor.IConfigurationProcessor;

/**
 * A Studio configurator interface.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public interface IConfigurator
{
	/**
	 * Return the display name for this configurator.
	 * 
	 * @return The configurator's name.
	 */
	String getName();

	/**
	 * Returns the configurator's ID.
	 * 
	 * @return The ID of this configurator.
	 */
	String getId();

	/**
	 * Returns the {@link IConfigurationProcessor} attached to this configurator. May return <code>null</code> in case
	 * the processor was not registered.
	 * 
	 * @return The attached {@link IConfigurationProcessor}; <code>null</code> in case the processor was not registered.
	 */
	IConfigurationProcessor getProcessor();

	/**
	 * Returns <code>true</code> if this configurator is enabled. A configurator is enabled when its attached processor
	 * is registered and enabled.
	 * 
	 * @return <code>true</code> if the configurator is enabled; <code>false</code> otherwise.
	 */
	boolean isEnabled();
}
