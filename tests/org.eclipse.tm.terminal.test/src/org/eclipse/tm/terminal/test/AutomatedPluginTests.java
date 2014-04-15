/*******************************************************************************
 * Copyright (c) 2008, 2010 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Martin Oberhuber (Wind River) - initial API and implementation
 *******************************************************************************/

package org.eclipse.tm.terminal.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Master Test Suite to run all Terminal plug-in tests.
 */
@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	org.eclipse.tm.internal.terminal.connector.TerminalConnectorPluginTest.class,
	org.eclipse.tm.internal.terminal.connector.TerminalConnectorFactoryTest.class
})
//@formatter:on
public class AutomatedPluginTests
{

}
