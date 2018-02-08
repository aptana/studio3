/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal.configurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.ShellExecutable;

/**
 * @author Max Stepanov
 */
public class WindowsMingwConfiguration extends AbstractProcessConfiguration {

	private static final String EXECUTABLE = "$os$/redttyw.exe"; //$NON-NLS-1$

	@Override
	protected IPath getExecutablePath() {
		return new Path(EXECUTABLE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getCommandLine()
	 */
	public List<String> getCommandLine() throws CoreException {
		List<String> list = new ArrayList<String>();
		list.add(getExecutable().getAbsolutePath());
		list.add("\"\\\"" + ShellExecutable.getPath().toOSString() + "\\\" --login -i\""); //$NON-NLS-1$ //$NON-NLS-2$
		list.add("120x40"); //$NON-NLS-1$
		if (Platform.inDevelopmentMode() || Platform.inDebugMode()) {
			list.add("-show"); //$NON-NLS-1$
		}
		return list;
	}

}
