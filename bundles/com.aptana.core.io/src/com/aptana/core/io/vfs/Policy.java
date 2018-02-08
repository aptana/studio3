/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.core.io.vfs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author Max Stepanov
 *
 */
public final class Policy {

	
	private Policy() {
	}
		
	public static IProgressMonitor monitorFor(IProgressMonitor monitor) {
		return (monitor == null) ? new NullProgressMonitor() : monitor;
	}

	public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null)
			return new NullProgressMonitor();
		if (monitor instanceof NullProgressMonitor)
			return monitor;
		return new SubProgressMonitor(monitor, ticks);
	}

	public static void checkCanceled(IProgressMonitor monitor) {
		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}

}
