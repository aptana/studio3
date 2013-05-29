/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.process;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IProcessFactory;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.console.ConsolePlugin;
import com.aptana.debug.core.FilterConsoleProcess;
import com.aptana.debug.core.IProcessOutputFilter;

/**
 * @author Max Stepanov
 *
 */
public class ConsoleProcessFactory implements IProcessFactory {

	public static final String ID = ConsolePlugin.PLUGIN_ID + ".processFactory"; //$NON-NLS-1$
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IProcessFactory#newProcess(org.eclipse.debug.core.ILaunch, java.lang.Process, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public IProcess newProcess(ILaunch launch, Process process, String label, Map attributes) {
		return new TTYConsoleProcess(launch, process, label, attributes);
	}

	/**
	 * Returns the process which output/error streams can be filtered.
	 * @param launch
	 * @param process
	 * @param label
	 * @param attributes
	 * @param processOutputFilter
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static IProcess newFilterConsoleProcess(ILaunch launch, Process process, String label, Map attributes, IProcessOutputFilter processOutputFilter) {
		FilterConsoleProcess filterConsoleProcess = new FilterConsoleProcess(launch, process, label, attributes);
		filterConsoleProcess.setProcessOutputFilter(processOutputFilter);
		return filterConsoleProcess;
	}

}
