/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.internal.ui;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * @author Max Stepanov
 *
 */
public class ConsolePageParticipant extends PlatformObject implements IConsolePageParticipant {

	private ConsoleStyledTextListener listener;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#init(org.eclipse.ui.part.IPageBookViewPage, org.eclipse.ui.console.IConsole)
	 */
	public void init(IPageBookViewPage page, IConsole console) {
		Control control = page.getControl();
		if (control instanceof StyledText) {
			listener = new ConsoleStyledTextListener((StyledText) control);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#dispose()
	 */
	public void dispose() {
		if (listener != null) {
			listener.dispose();
			listener = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#activated()
	 */
	public void activated() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#deactivated()
	 */
	public void deactivated() {
	}

}
