/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.connector;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;

/**
 * @author Max Stepanov
 *
 */
/* package */ class LocalTerminalOutputListener implements IStreamListener {

	private PrintStream printStream;
	private IOutputFilter outputFilter;

	/**
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public LocalTerminalOutputListener(ITerminalControl control, IOutputFilter outputFilter) throws UnsupportedEncodingException {
		printStream = new PrintStream(control.getRemoteToTerminalOutputStream(), true, LocalTerminalConnector.ENCODING);
		this.outputFilter = outputFilter;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IStreamListener#streamAppended(java.lang.String, org.eclipse.debug.core.model.IStreamMonitor)
	 */
	public void streamAppended(String text, IStreamMonitor monitor) {
		if (outputFilter != null) {
			printStream.print(outputFilter.filterOutput(text.toCharArray()));
		} else {
			printStream.print(text);
		}
	}

}
