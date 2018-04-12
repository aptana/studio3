/*******************************************************************************
 * Copyright (c) 2006, 2011 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 * Martin Oberhuber (Wind River) - [261486][api][cleanup] Mark @noimplement interfaces as @noextend
 * Uwe Stieber (Wind River) - [282996] [terminal][api] Add "hidden" attribute to terminal connector extension point
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

import java.io.OutputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

/**
 * A contributed connection type to manage a single connection.
 *
 * Implementations of this class are contributed through the
 * <code>org.eclipse.tm.terminal.terminalConnectors</code> extension point. This
 * class gives access to the static markup of a terminal connector extension as
 * well as providing the lifecycle management for the dynamically loaded
 * {@link TerminalConnectorImpl} instance, which performs the actual
 * communications. This pattern allows for lazy initialization, bundle
 * activation and class loading of the actual {@link TerminalConnectorImpl}
 * instance.
 *
 * Clients can get terminal connector instances from the
 * {@link TerminalConnectorExtension} class, or from
 * {@link ITerminalViewControl#getTerminalConnector()} when running inside an
 * active terminal widget.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 *
 * @author Michael Scharf
 *         <p>
 *         <strong>EXPERIMENTAL</strong>. This class or interface has been added
 *         as part of a work in progress. There is no guarantee that this API
 *         will work or that it will remain the same. Please do not use this API
 *         without consulting with the <a
 *         href="http://www.eclipse.org/tm/">Target Management</a> team.
 *         </p>
 */
public interface ITerminalConnector extends IAdaptable {
	/**
	 * @return an ID of this connector. The id from the plugin.xml.
	 * @since org.eclipse.tm.terminal 2.0
	 */
	String getId();

	/**
	 * @return <code>null</code> the name (as specified in the plugin.xml)
	 * @since org.eclipse.tm.terminal 2.0
	 */
	String getName();

	/**
	 * @return <code>True</code> if the connector is not visible in user
	 *         selections.
	 * @since org.eclipse.tm.terminal 3.0.1
	 */
	boolean isHidden();

	/**
	 * @return true if the {@link TerminalConnectorImpl} has been initialized.
	 * If there was an initialization error, {@link #getInitializationErrorMessage()}
	 * returns the error message.
	 * @since org.eclipse.tm.terminal 2.0
	 */
	boolean isInitialized();

	/**
	 * This method initializes the connector if it is not initialized!
	 * If the connector was initialized successfully, <code>null</code> is
	 * returned. Otherwise an error message describing the problem is returned.
	 * @return <code>null</code> or a localized error message.
	 * @since org.eclipse.tm.terminal 2.0
	 */
	String getInitializationErrorMessage();

	/**
	 * Connect using the current state of the settings.
	 * @param control Used to inform the UI about state changes and messages from the connection.
	 */
	void connect(ITerminalControl control);

	/**
	 * Disconnect if connected. Else do nothing.
	 */
	void disconnect();

	/**
	 * @return true if a local echo is needed.
	 * TODO:Michael Scharf: this should be handed within the connection....
	 */
	boolean isLocalEcho();

    /**
     * Notify the remote site that the size of the terminal has changed.
     * @param newWidth
     * @param newHeight
     */
    void setTerminalSize(int newWidth, int newHeight);

    /**
     * @return the terminal to remote stream (bytes written to this stream will
     * be sent to the remote site). For the stream in the other direction (remote to
     * terminal see {@link ITerminalControl#getRemoteToTerminalOutputStream()}
	 * @since org.eclipse.tm.terminal 2.0
     */
    OutputStream getTerminalToRemoteStream();

	/**
	 * Load the state of this connection. Is typically called before
	 * {@link #connect(ITerminalControl)}.
	 *
	 * @param store a string based data store. Short keys like "foo" can be used to
	 * store the state of the connection.
	 */
	void load(ISettingsStore store);

	/**
	 * When the view or dialog containing the terminal is closed,
	 * the state of the connection is saved into the settings store <code>store</code>
	 * @param store
	 */
	void save(ISettingsStore store);

	/**
	 * FIXME should not have UI related stuff in ITerminalConnector, since
	 * connectors may be totally programmatic and UI-less. Better make the
	 * connector adapt to an interface that controls the UI aspect.
	 *
	 * @return a new page that can be used in a dialog to setup this connection.
	 *         The dialog should persist its settings with the
	 *         {@link #load(ISettingsStore)} and {@link #save(ISettingsStore)}
	 *         methods.
	 */
	ISettingsPage makeSettingsPage();

	/**
	 * @return A string that represents the settings of the connection. This representation
	 * may be shown in the status line of the terminal view.
	 */
	String getSettingsSummary();

}
