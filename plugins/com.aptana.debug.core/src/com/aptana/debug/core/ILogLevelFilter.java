/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.debug.core.model.IProcess;

/**
 * Log-Level filter interface. Implementations of this interface can be added to the logLevelFilters extension point in
 * order to filter out console output that arrives from an IProcess.
 * 
 * @author sgibly@appcelerator.com
 */
public interface ILogLevelFilter extends IExecutableExtension, IProcessOutputFilter
{

	/**
	 * The {@link IProcess} property name that should be passed to the
	 * {@link #setInitializationData(org.eclipse.core.runtime.IConfigurationElement, String, Object)} in order to set
	 * the {@link IProcess} for this filter.
	 */
	public static final String PROCESS_PROPERTY_NAME = "process"; //$NON-NLS-1$

	/**
	 * Returns a filtered string out of a given String.
	 * 
	 * @param str
	 * @return A filtered string.
	 */
	String filter(String str);
}
