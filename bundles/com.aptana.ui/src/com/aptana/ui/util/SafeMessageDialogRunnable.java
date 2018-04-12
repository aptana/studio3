/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.util;

import org.eclipse.jface.util.SafeRunnable;

/**
 * A Runnable that opens a MessageDialog and performs an operation
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public abstract class SafeMessageDialogRunnable extends SafeRunnable
{
	/**
	 * Opens a MessageDialog and returns the return code
	 * 
	 * @return
	 */
	public abstract int openMessageDialog();

	/**
	 * No-op
	 */
	public void run() throws Exception
	{
		// No-op
	}
}
