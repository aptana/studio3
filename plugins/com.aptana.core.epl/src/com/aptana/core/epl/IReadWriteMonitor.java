/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl;

public interface IReadWriteMonitor
{
	/**
	 * Concurrent reading is allowed Blocking only when already writing.
	 */
	void enterRead();

	/**
	 * Only one writer at a time is allowed to perform Blocking only when already writing or reading.
	 */
	void enterWrite();

	/**
	 * Only notify waiting writer(s) if last reader
	 */
	void exitRead();

	/**
	 * When writing is over, all readers and possible writers are granted permission to restart concurrently
	 */
	void exitWrite();

	/**
	 * Atomic exitRead/enterWrite: Allows to keep monitor in between exit read and next enter write. Use when writing
	 * changes is optional, otherwise call the individual methods. Returns false if multiple readers are accessing the
	 * index.
	 */
	boolean exitReadEnterWrite();

	/**
	 * Atomic exitWrite/enterRead: Allows to keep monitor in between exit write and next enter read. When writing is
	 * over, all readers are granted permissing to restart concurrently. This is the same as:
	 * 
	 * <pre>
	 * synchronized (monitor)
	 * {
	 * 	monitor.exitWrite();
	 * 	monitor.enterRead();
	 * }
	 * </pre>
	 */
	void exitWriteEnterRead();
}