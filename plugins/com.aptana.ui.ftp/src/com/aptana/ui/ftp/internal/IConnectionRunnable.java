/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.ftp.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 *
 */
public interface IConnectionRunnable {

	public void beforeConnect(IConnectionPoint connectionPoint) throws CoreException, InterruptedException;
	public void afterConnect(IConnectionPoint connectionPoint, IProgressMonitor monitor) throws CoreException, InterruptedException;
}
