/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.ftp.internal;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * @author Max Stepanov
 *
 */
public class NumberVerifyListener implements VerifyListener {

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
	 */
	public void verifyText(VerifyEvent e) {
		try {
			if (e.text.length() > 0) {
				Integer.parseInt(e.text);
			}
		} catch (NumberFormatException ignore) {
			e.doit = false;
		}
	}
}
