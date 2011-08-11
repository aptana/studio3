/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.configurations;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.CorePlugin;
import com.aptana.core.util.ResourceUtil;
import com.aptana.terminal.TerminalPlugin;
import com.aptana.terminal.IProcessConfiguration;

/* package */abstract class AbstractProcessConfiguration implements IProcessConfiguration {

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getExecutable()
	 */
	public File getExecutable() {
		URL url = FileLocator.find(TerminalPlugin.getDefault().getBundle(), getExecutablePath(), null);
		return ResourceUtil.resourcePathToFile(url);
	}

	protected abstract IPath getExecutablePath();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getEnvironment()
	 */
	public Map<String, String> getEnvironment() {
		Map<String, String> env = new HashMap<String, String>();
		env.put("APTANA_VERSION", CorePlugin.getAptanaStudioVersion()); //$NON-NLS-1$
		return env;
	}
}
