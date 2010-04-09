/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;
import com.aptana.terminal.IProcessConfiguration;
import com.aptana.util.ExecutableUtil;
import com.aptana.util.ResourceUtils;

/**
 * @author Max Stepanov
 *
 */
public class WindowsMingwConfiguration implements IProcessConfiguration {

	private static final String EXECUTABLE = "$os$/redttyw.exe"; //$NON-NLS-1$
	
	private IPath shellExecutable = null;

	
	/* (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getExecutable()
	 */
	@Override
	public File getExecutable() {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(EXECUTABLE), null);
		return ResourceUtils.resourcePathToFile(url);
	}

	/* (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getCommandLine()
	 */
	@Override
	public List<String> getCommandLine() {
		List<String> list = new ArrayList<String>();
		list.add(getExecutable().getAbsolutePath());
		IPath shell = findShellExecutable();
		String command = shell != null ? shell.toOSString() : "sh"; //$NON-NLS-1$
		list.add("\"\\\"" + command + "\\\"  --login -i\""); //$NON-NLS-1$ //$NON-NLS-2$
		list.add("120x40"); //$NON-NLS-1$
		if (Platform.inDevelopmentMode() || Platform.inDebugMode()) {
			list.add("-show"); //$NON-NLS-1$
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getEnvironment()
	 */
	@Override
	public Map<String, String> getEnvironment() {
		Map<String, String> env = new HashMap<String, String>();
		env.put("TERM", "xterm-color"); //$NON-NLS-1$ //$NON-NLS-2$
		return env;
	}
	
	private IPath findShellExecutable() {
		if (shellExecutable != null) {
			return shellExecutable;
		}
		return shellExecutable = ExecutableUtil.find("sh.exe", false, null, getPossibleShellLocations());
	}
	
	private static List<IPath> getPossibleShellLocations() {
		List<IPath> list = new ArrayList<IPath>();
		for (File root : File.listRoots()) {
			list.add(Path.fromOSString(root.getAbsolutePath()).append(Path.fromOSString("Program Files\\Git\\bin"))); //$NON-NLS-1$
			list.add(Path.fromOSString(root.getAbsolutePath()).append(Path.fromOSString("Program Files (x86)\\Git\\bin"))); //$NON-NLS-1$
		}
		return list;
	}

}
