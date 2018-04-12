/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import org.eclipse.core.internal.filesystem.local.InfiniteProgress;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class InfiniteProgressMonitor extends InfiniteProgress {

	/**
	 * @param monitor
	 */
	public InfiniteProgressMonitor(IProgressMonitor monitor) {
		super(monitor);
	}
}
