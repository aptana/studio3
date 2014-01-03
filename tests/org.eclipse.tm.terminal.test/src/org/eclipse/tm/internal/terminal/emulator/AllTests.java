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
package org.eclipse.tm.internal.terminal.emulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Terminal emulator test cases. Runs in emulator package to allow access to default visible items.
 */
@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	VT100EmulatorBackendTest.class
})
//@formatter:on
public class AllTests
{

}
