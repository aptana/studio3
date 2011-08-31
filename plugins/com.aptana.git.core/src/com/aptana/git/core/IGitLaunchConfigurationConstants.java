/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import org.eclipse.debug.core.model.IProcess;

public interface IGitLaunchConfigurationConstants
{
	/**
	 * ID of the git launch configuration type.
	 */
	public static final String ID_GIT_LAUNCH_CONFIGURATION_TYPE = "com.aptana.git.core.launchConfigurationType"; //$NON-NLS-1$

	/**
	 * Path to the git executable
	 */
	public static final String ATTR_LOCATION = "ATTR_LOCATION"; //$NON-NLS-1$

	/**
	 * Arguments to pass to git
	 */
	public static final String ATTR_ARGUMENTS = "ATTR_ARGUMENTS"; //$NON-NLS-1$

	/**
	 * Should we show the console?
	 */
	public static final String ATTR_SHOW_CONSOLE = "ATTR_SHOW_CONSOLE"; //$NON-NLS-1$

	/**
	 * Working directory in which to run the git command.
	 */
	public static final String ATTR_WORKING_DIRECTORY = "ATTR_WORKING_DIRECTORY"; //$NON-NLS-1$

	/**
	 * The {@link IProcess#ATTR_PROCESS_TYPE} value assigned to git launch configurations' underlying IProcesses.
	 */
	public static final String PROCESS_TYPE = "git"; //$NON-NLS-1$
}
