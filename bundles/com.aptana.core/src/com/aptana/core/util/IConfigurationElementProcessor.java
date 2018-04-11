/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * IConfigurationElementProcessor
 */
public interface IConfigurationElementProcessor
{
	/**
	 * Do something with the specified configuration element. Typically, this method is called after some sort of
	 * querying or filtering has taken place on an extension point
	 * 
	 * @param element
	 */
	void processElement(IConfigurationElement element);

	/**
	 * Return a collection of element names that this processor processes.
	 * 
	 * @return A collection of strings
	 */
	Set<String> getSupportElementNames();
}
