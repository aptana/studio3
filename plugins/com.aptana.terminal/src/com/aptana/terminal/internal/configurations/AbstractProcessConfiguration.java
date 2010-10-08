/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.configurations;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.terminal.Activator;
import com.aptana.terminal.IProcessConfiguration;

/* package */ abstract class AbstractProcessConfiguration implements IProcessConfiguration {

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getExecutable()
	 */
	public File getExecutable() {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), getExecutablePath(), null);
		return ResourceUtil.resourcePathToFile(url);
	}

	protected abstract IPath getExecutablePath();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getEnvironment()
	 */
	public Map<String, String> getEnvironment() {
		Map<String, String> env = new HashMap<String, String>();
		env.put("APTANA_VERSION", getVersion()); //$NON-NLS-1$
		return env;
	}

	private String getVersion() {
		// Grab RED RCP plugin version
		String version = EclipseUtil.getPluginVersion("com.aptana.radrails.rcp"); //$NON-NLS-1$
		if (version == null) {
			// Grab Product version
			version = EclipseUtil.getProductVersion();
		}
		// FIXME This doesn't work for features.
//		if (version == null)
//		{
//			// Try Red Core feature
//			version = EclipseUtil.getPluginVersion("com.aptana.red.core"); //$NON-NLS-1$
//		}
		if (version == null) {
			// Fallback to Terminal plugin version
			version = EclipseUtil.getPluginVersion(Activator.PLUGIN_ID);
		}
		return version;
	}
}
