/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable platformSpecificLineSeparator

package com.aptana.terminal.internal.configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.ShellExecutable;

/**
 * @author Max Stepanov
 */
public class NixBashConfiguration extends AbstractProcessConfiguration {

	private static final String EXECUTABLE = "$os$/redtty"; //$NON-NLS-1$

	@Override
	protected IPath getExecutablePath() {
		return Path.fromPortableString(EXECUTABLE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getCommandLine()
	 */
	public List<String> getCommandLine() {
		List<String> list = new ArrayList<String>();
		list.add(getExecutable().getAbsolutePath());
		list.add("/bin/bash"); //$NON-NLS-1$
		// newline is a delimiter in redtty
		list.add("bash\n--rcfile\n" + ShellExecutable.getShellRCPath().toOSString() + "\n-i"); //$NON-NLS-1$ //$NON-NLS-2$
		list.add("120x40"); //$NON-NLS-1$
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.internal.configurations.AbstractProcessConfiguration#getEnvironment()
	 */
	@Override
	public Map<String, String> getEnvironment() {
		Map<String, String> env = super.getEnvironment();
		env.put("TERM", "xterm-color"); //$NON-NLS-1$ //$NON-NLS-2$
		return env;
	}

}
