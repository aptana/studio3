/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.debug.core;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;


/**
 * @author Max Stepanov
 */
public class FilterConsoleProcess extends RuntimeProcess {

	private IProcessOutputFilter processOutputFilter;
	private ProxyProcess proxyProcess;

	/**
	 * @param launch
	 * @param process
	 * @param name
	 * @param attributes
	 */
	@SuppressWarnings("rawtypes")
	public FilterConsoleProcess(ILaunch launch, Process process, String name, Map attributes) {
		super(launch, process, name, attributes);
	}

	/**
	 * @param processOutputFilter
	 *            the processOutputFilter to set
	 */
	public void setProcessOutputFilter(IProcessOutputFilter processOutputFilter) {
		this.processOutputFilter = processOutputFilter;
		if (proxyProcess != null) {
			((FilterProxyInputStream)proxyProcess.getInputStream()).setProcessOutputFilter(processOutputFilter);
			((FilterProxyInputStream)proxyProcess.getErrorStream()).setProcessOutputFilter(processOutputFilter);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.RuntimeProcess#getSystemProcess()
	 */
	@Override
	protected Process getSystemProcess() {
		if (proxyProcess == null) {
			proxyProcess = new ProxyProcess(super.getSystemProcess()) {
				@Override
				protected InputStream createInputStream(InputStream in) {
					String encoding = getLaunch().getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING);
					return new FilterProxyInputStream(in, encoding, processOutputFilter);
				}
			};
		}
		return proxyProcess;
	}

}
