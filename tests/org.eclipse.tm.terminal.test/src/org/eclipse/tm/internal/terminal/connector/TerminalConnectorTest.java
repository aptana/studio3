/*******************************************************************************
 * Copyright (c) 2007, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [225853][api] Provide more default functionality in TerminalConnectorImpl
 * Martin Oberhuber (Wind River) - [204796] Terminal should allow setting the encoding to use
 * Uwe Stieber (Wind River) - [282996] [terminal][api] Add "hidden" attribute to terminal connector extension point
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.connector;

import java.io.OutputStream;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.tm.internal.terminal.connector.TerminalConnector.Factory;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

public class TerminalConnectorTest extends TestCase {
	public class SettingsMock implements ISettingsStore {

		public String get(String key) {
			return null;
		}

		public String get(String key, String defaultValue) {
			return null;
		}

		public void put(String key, String value) {
		}

	}
	public static class TerminalControlMock implements ITerminalControl {

		public void setEncoding(String encoding) {
		}

		public String getEncoding() {
			return "ISO-8859-1"; //$NON-NLS-1$
		}

		public void displayTextInTerminal(String text) {
		}

		public OutputStream getRemoteToTerminalOutputStream() {
			return null;
		}

		public Shell getShell() {
			return null;
		}

		public TerminalState getState() {
			return null;
		}

		public void setMsg(String msg) {
		}

		public void setState(TerminalState state) {
		}

		public void setTerminalTitle(String title) {
		}

	}
	static class ConnectorMock extends TerminalConnectorImpl {

		public boolean fEcho;
		public int fWidth;
		public int fHeight;
		public ITerminalControl fControl;
		public ISettingsStore fSaveStore;
		public ISettingsStore fLoadStore;
		public boolean fDisconnect;

		public boolean isLocalEcho() {
			return fEcho;
		}
		public void setTerminalSize(int newWidth, int newHeight) {
			fWidth=newWidth;
			fHeight=newHeight;
		}
		public void connect(ITerminalControl control) {
			super.connect(control);
			fControl = control;
		}
		public void doDisconnect() {
			fDisconnect=true;
		}

		public OutputStream getTerminalToRemoteStream() {
			return null;
		}

		public String getSettingsSummary() {
			return "Summary";
		}

		public void load(ISettingsStore store) {
			fLoadStore=store;
		}

		public ISettingsPage makeSettingsPage() {
			return new ISettingsPage(){
				public void createControl(Composite parent) {
				}
				public void loadSettings() {
				}
				public void saveSettings() {
				}
				public boolean validateSettings() {
					return false;
				}};
		}

		public void save(ISettingsStore store) {
			fSaveStore=store;
		}
	}
	static class SimpleFactory implements Factory {
		final TerminalConnectorImpl fConnector;
		public SimpleFactory(TerminalConnectorImpl connector) {
			fConnector = connector;
		}
		public TerminalConnectorImpl makeConnector() throws Exception {
			// TODO Auto-generated method stub
			return fConnector;
		}
	}
	public void testGetInitializationErrorMessage() {
		TerminalConnector c=new TerminalConnector(new SimpleFactory(new ConnectorMock()),"xID","xName", false);
		c.connect(new TerminalControlMock());
		assertNull(c.getInitializationErrorMessage());

		c=new TerminalConnector(new SimpleFactory(new ConnectorMock(){
			public void initialize() throws Exception {
				throw new Exception("FAILED");
			}}),"xID","xName", false);
		c.connect(new TerminalControlMock());
		assertEquals("FAILED",c.getInitializationErrorMessage());

	}

	public void testGetIdAndName() {
		TerminalConnector c=new TerminalConnector(new SimpleFactory(new ConnectorMock()),"xID","xName", false);
		assertEquals("xID", c.getId());
		assertEquals("xName", c.getName());
	}

	public void testConnect() {
		TerminalConnector c=new TerminalConnector(new SimpleFactory(new ConnectorMock()),"xID","xName", false);
		assertFalse(c.isInitialized());
		c.connect(new TerminalControlMock());
		assertTrue(c.isInitialized());

	}

	public void testDisconnect() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		TerminalControlMock control=new TerminalControlMock();
		c.connect(control);
		c.disconnect();
		assertTrue(mock.fDisconnect);
	}

	public void testGetTerminalToRemoteStream() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		TerminalControlMock control=new TerminalControlMock();
		c.connect(control);
		assertSame(mock.fControl,control);
	}

	public void testGetSettingsSummary() {
		TerminalConnector c=new TerminalConnector(new SimpleFactory(new ConnectorMock()),"xID","xName", false);
		assertEquals("Not Initialized", c.getSettingsSummary());
		c.connect(new TerminalControlMock());
		assertEquals("Summary", c.getSettingsSummary());
	}

	public void testIsLocalEcho() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		assertFalse(c.isLocalEcho());
		mock.fEcho=true;
		assertTrue(c.isLocalEcho());
	}

	public void testLoad() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		ISettingsStore s=new SettingsMock();
		c.load(s);
		// the load is called after the connect...
		assertNull(mock.fLoadStore);
		c.connect(new TerminalControlMock());
		assertSame(s,mock.fLoadStore);
	}

	public void testSave() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		ISettingsStore s=new SettingsMock();
		c.save(s);
		assertNull(mock.fSaveStore);
		c.connect(new TerminalControlMock());
		c.save(s);
		assertSame(s,mock.fSaveStore);
	}

	public void testMakeSettingsPage() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		assertNotNull(c.makeSettingsPage());
	}

	public void testSetTerminalSize() {
		ConnectorMock mock=new ConnectorMock();
		TerminalConnector c=new TerminalConnector(new SimpleFactory(mock),"xID","xName", false);
		c.setTerminalSize(100, 200);

	}

}
