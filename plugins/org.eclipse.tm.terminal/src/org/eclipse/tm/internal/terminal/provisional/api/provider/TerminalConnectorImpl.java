/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [225853][api] Provide more default functionality in TerminalConnectorImpl 
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api.provider;

import java.io.OutputStream;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.Logger;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;

/**
 * Abstract base class for all terminal connector implementations to be
 * registered via the <code>org.eclipse.tm.terminal.terminalConnectors</code>
 * extension point.
 *
 * @since org.eclipse.tm.terminal 2.0
 */
public abstract class TerminalConnectorImpl {

 	/**
	 * The TerminalControl associated with this connector.
	 * Required for advertising state changes when needed.
	 */
	protected ITerminalControl fControl;

	/**
	 * Initialize this connector. This is called once after the constructor, in
	 * order to perform any required initializations such as loading required
	 * native libraries. Any work that may lead to runtime exceptions should be
	 * done in this method rather than in the constructor.
	 *
	 * @throws Exception when the connector fails to initialize (due to missing
	 *             required libraries, for instance).
	 */
	public void initialize() throws Exception {
	}

	/**
	 * Connect using the current state of the settings.
	 * 
	 * This method is designed to be overridden by actual implementations, in
	 * order to open the streams required for communicating with the remote
	 * side. Extenders must call <code>super.connect(control)</code> as the
	 * first thing they are doing.
	 * 
	 * @param control Used to inform the UI about state changes and messages
	 *            from the connection.
	 */
	public void connect(ITerminalControl control) {
		Logger.log("entered."); //$NON-NLS-1$
		fControl = control;
	}

	/**
	 * Disconnect if connected. Else do nothing. Has to set the state of the
	 * {@link ITerminalControl} when finished disconnecting.
	 */
	public final void disconnect() {
		Logger.log("entered."); //$NON-NLS-1$
		doDisconnect();
		fControl.setState(TerminalState.CLOSED);
	}

	/**
	 * Disconnect if connected. Else do nothing. Clients should override to
	 * perform any extra work needed for disconnecting.
	 */
	protected void doDisconnect() {
		// Do nothing by default
	}

    /**
     * @return the terminal to remote stream (bytes written to this stream will
     * be sent to the remote site). For the stream in the other direction (remote to
     * terminal see {@link ITerminalControl#getRemoteToTerminalOutputStream()}
     */
	abstract public OutputStream getTerminalToRemoteStream();

	/**
	 * @return A string that represents the settings of the connection. This representation
	 * may be shown in the status line of the terminal view.
	 */
	abstract public String getSettingsSummary();

	/**
	 * Test if local echo is needed. The default implementation returns
	 * <code>false</code>. Override to modify this behavior.
	 *
	 * @return true if a local echo is needed. TODO:Michael Scharf: this should
	 *         be handed within the connection....
	 */
	public boolean isLocalEcho() {
		return false;
	}

	/**
	 * Return a settings page for configuring this connector, or
	 * <code>null</code> if it cannot be configured.
	 *
	 * The dialog should persist its settings with the
	 * {@link #load(ISettingsStore)} and {@link #save(ISettingsStore)} methods.
	 *
	 * @return a new page that can be used in a dialog to setup this connection,
	 *         or <code>null</code>.
	 */
	public ISettingsPage makeSettingsPage() {
		return null;
	}

	/**
	 * Load the state or settings of this connection. Is typically called before
	 * {@link #connect(ITerminalControl)}.
	 *
	 * Connectors that have nothing to configure do not need to implement this.
	 * Those terminals that do have configuration (which they expose via
	 * {@link #makeSettingsPage()} need to override this method to load
	 * settings.
	 *
	 * @param store a string based data store. Short keys like "foo" can be used
	 *            to store the state of the connection.
	 */
	public void load(ISettingsStore store) {
		// do nothing by default
	}

	/**
	 * When the view or dialog containing the terminal is closed, the state of
	 * the connection is saved into the settings store <code>store</code>.
	 *
	 * Connectors that have no state or settings to persist do not need to
	 * override this. Others should override to persist their settings.
	 *
	 * @param store the store for persisting settings.
	 */
	public void save(ISettingsStore store) {
		// do nothing by default
	}

    /**
	 * Notify the remote site that the size of the terminal has changed.
	 *
	 * Concrete connectors should override this if they have the possibility to
	 * inform the remote about changed terminal size.
	 *
	 * @param newWidth the new width in characters.
	 * @param newHeight the new height in characters.
	 */
	public void setTerminalSize(int newWidth, int newHeight) {
	}
}
