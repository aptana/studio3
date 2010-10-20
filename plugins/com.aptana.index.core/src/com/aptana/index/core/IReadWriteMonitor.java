/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.index.core;

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