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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.ResourceUtil;
import com.aptana.terminal.Activator;

/**
 * @author Max Stepanov
 *
 */
public class NixBashConfiguration extends AbstractProcessConfiguration {

	private static final String EXECUTABLE = "$os$/redtty"; //$NON-NLS-1$
	private static final String RCFILE = "$os$/.aptanarc"; //$NON-NLS-1$
	
	@Override
	protected IPath getExecutablePath() {
		return Path.fromPortableString(EXECUTABLE);
	}

	/* (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getCommandLine()
	 */
	public List<String> getCommandLine() {
		List<String> list = new ArrayList<String>();
		list.add(getExecutable().getAbsolutePath());
		list.add("/bin/bash"); //$NON-NLS-1$
		// newline is a delimiter in redtty
		list.add("bash\n--rcfile\n"+getRCFile().getAbsolutePath()+"\n-i"); //$NON-NLS-1$ //$NON-NLS-2$
		list.add("120x40"); //$NON-NLS-1$
		return list;
	}

	/* (non-Javadoc)
	 * @see com.aptana.terminal.internal.configurations.AbstractProcessConfiguration#getEnvironment()
	 */
	@Override
	public Map<String, String> getEnvironment() {
		Map<String, String> env = super.getEnvironment();
		env.put("TERM", "xterm-color"); //$NON-NLS-1$ //$NON-NLS-2$
		return env;
	}

	private File getRCFile() {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), Path.fromPortableString(RCFILE), null);
		return ResourceUtil.resourcePathToFile(url);
	}

}
