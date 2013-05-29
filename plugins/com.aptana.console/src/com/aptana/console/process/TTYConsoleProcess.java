/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.process;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;

import com.aptana.debug.core.ProxyProcess;

/**
 * @author Max Stepanov
 *
 */
/* package */ class TTYConsoleProcess extends RuntimeProcess {
	
	private Process proxyProcess = null;
	
	/**
	 * @param launch
	 * @param process
	 * @param name
	 * @param attributes
	 */
	@SuppressWarnings("rawtypes")
	protected TTYConsoleProcess(ILaunch launch, Process process, String name, Map attributes) {
		super(launch, process, name, attributes);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.RuntimeProcess#getSystemProcess()
	 */
	@Override
	protected Process getSystemProcess() {
		if (proxyProcess == null) {
			proxyProcess = new ProxyProcess(super.getSystemProcess()) {
				@Override
				protected InputStream createInputStream(InputStream in) {
					return new ESCSequnceFilterInputStream(in);
				}			
			};
		}
		return proxyProcess;
	}

}
