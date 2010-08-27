/*******************************************************************************
 * Copyright (c) 2006, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 * Martin Oberhuber (Wind River) - [204796] Terminal should allow setting the encoding to use
 * Martin Oberhuber (Wind River) - [261486][api][cleanup] Mark @noimplement interfaces as @noextend
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.widgets.Shell;

/**
 * Represents the terminal view as seen by a terminal connection.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as part
 * of a work in progress. There is no guarantee that this API will work or that
 * it will remain the same. Please do not use this API without consulting with
 * the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 *
 * @author Michael Scharf
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITerminalControl {

	/**
	 * @return the current state of the connection
	 */
	TerminalState getState();

	/**
	 * @param state
	 */
	void setState(TerminalState state);

	/**
	 * A shell to show dialogs.
	 * @return the shell in which the terminal is shown.
	 */
	Shell getShell();

	/**
	 * Set the encoding that the Terminal uses to decode bytes from the
	 * Terminal-to-remote-Stream into Unicode Characters used in Java; or, to
	 * encode Characters typed by the user into bytes sent over the wire to the
	 * remote.
	 *
	 * By default, the local Platform Default Encoding is used. Also note that
	 * the encoding must not be applied in case the terminal stream is processed
	 * by some data transfer protocol which requires binary data.
	 *
	 * Validity of the encoding set here is not checked. Since some encodings do
	 * not cover the entire range of Unicode characters, it can happen that a
	 * particular Unicode String typed in by the user can not be encoded into a
	 * byte Stream with the encoding specified. and UnsupportedEncodingException
	 * will be thrown in this case at the time the String is about to be
	 * processed.
	 *
	 * The concrete encoding to use can either be specified manually by a user,
	 * by means of a dialog, or a connector can try to obtain it automatically
	 * from the remote side e.g. by evaluating an environment variable such as
	 * LANG on UNIX systems.
	 *
	 * @since org.eclipse.tm.terminal 2.0
	 */
	void setEncoding(String encoding) throws UnsupportedEncodingException;

	/**
	 * Return the current encoding. That's interesting when the previous
	 * setEncoding() call failed and the fallback default encoding should be
	 * queried, such that e.g. a combobox with encodings to choose can be
	 * properly initialized.
	 *
	 * @return the current Encoding of the Terminal.
	 * @since org.eclipse.tm.terminal 2.0
	 */
	String getEncoding();

	/**
	 * Show a text in the terminal. If puts newlines at the beginning and the
	 * end.
	 *
	 * @param text TODO: Michael Scharf: Is this really needed?
	 */
	void displayTextInTerminal(String text);

	/**
	 * @return a stream used to write to the terminal. Any bytes written to this
	 * stream appear in the terminal or are interpreted by the emulator as
	 * control sequences. The stream in the opposite direction, terminal
	 * to remote is in {@link ITerminalConnector#getTerminalToRemoteStream()}.
	 */
	OutputStream getRemoteToTerminalOutputStream();

	/**
	 * Set the title of the terminal view.
	 * @param title
	 */
	void setTerminalTitle(String title);

	/**
	 * Show an error message during connect.
	 * @param msg
	 * TODO: Michael Scharf: Should be replaced by a better error notification mechanism!
	 */
	void setMsg(String msg);

}
