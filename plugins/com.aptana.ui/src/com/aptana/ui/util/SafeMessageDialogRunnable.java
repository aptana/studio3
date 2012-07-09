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
 * @author Nam Le <nle@appcelerator.com>
 *
 */
public abstract class SafeMessageDialogRunnable extends SafeRunnable
{
	public abstract int openMessageDialog();
}
