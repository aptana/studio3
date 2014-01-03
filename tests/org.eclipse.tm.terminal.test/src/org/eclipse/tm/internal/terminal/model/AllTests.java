/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Martin Oberhuber (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Internal Terminal Model test cases. Runs in internal model package to allow access to default visible items.
 */
@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	SnapshotChangesTest.class,
	SynchronizedTerminalTextDataTest.class,
	TerminalTextDataFastScrollTest.class,
	TerminalTextDataFastScrollTestMaxHeigth.class,
	TerminalTextDataPerformanceTest.class,
	TerminalTextDataSnapshotTest.class,
	TerminalTextDataSnapshotWindowTest.class,
	TerminalTextDataStoreTest.class,
	TerminalTextDataTest.class,
	TerminalTextDataWindowTest.class
})
//@formatter:on
public class AllTests
{

}
