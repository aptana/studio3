/*******************************************************************************
 * Copyright (c) 2008, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Martin Oberhuber (Wind River) - initial API and implementation
 * Uwe Stieber (Wind River) - [282996] [terminal][api] Add "hidden" attribute to terminal connector extension point
 *******************************************************************************/

package org.eclipse.tm.internal.terminal.connector;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.tm.internal.terminal.connector.TerminalConnectorTest.ConnectorMock;
import org.eclipse.tm.internal.terminal.connector.TerminalConnectorTest.SimpleFactory;
import org.eclipse.tm.internal.terminal.connector.TerminalConnectorTest.TerminalControlMock;

/**
 * Testcase for TerminalConnector that must run as a JUnit plug-in test.
 */
public class TerminalConnectorPluginTest extends TestCase {

	public void testIsInitialized() {
		if (!Platform.isRunning())
			return;
		TerminalConnector c = new TerminalConnector(new SimpleFactory(new ConnectorMock()), "xID", "xName", false);
		assertFalse(c.isInitialized());
		c.getId();
		assertFalse(c.isInitialized());
		c.getName();
		assertFalse(c.isInitialized());
		c.isHidden();
		assertFalse(c.isInitialized());
		c.getSettingsSummary();
		assertFalse(c.isInitialized());
		c.setTerminalSize(10, 10);
		assertFalse(c.isInitialized());
		c.load(null);
		assertFalse(c.isInitialized());
		c.save(null);
		assertFalse(c.isInitialized());
		c.getAdapter(ConnectorMock.class);
		assertFalse(c.isInitialized());
	}

	public void testGetAdapter() {
		if (!Platform.isRunning())
			return;
		ConnectorMock mock = new ConnectorMock();
		TerminalConnector c = new TerminalConnector(new SimpleFactory(mock), "xID", "xName", false);
		assertNull(c.getAdapter(ConnectorMock.class));
		// the load is called after the connect...
		c.connect(new TerminalControlMock());

		assertSame(mock, c.getAdapter(ConnectorMock.class));
	}

}
