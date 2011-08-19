/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.debug.core;

import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * @author Max Stepanov
 */
public interface IExtendedStreamsProxy extends IStreamsProxy {

	/**
	 * Returns a monitor for the stream of this proxy's process identified by streamIdentifer, or <code>null</code> if
	 * not exists. The monitor is connected to the corresponding stream of the associated process.
	 * 
	 * @param streamIdentifier
	 * @return an stream monitor, or <code>null</code> if none
	 */
	public IStreamMonitor getStreamMonitor(String streamIdentifer);

	/**
	 * Return list of stream identifiers handled by this streams proxy.
	 * 
	 * @return
	 */
	public String[] getStreamIdentifers();
}
