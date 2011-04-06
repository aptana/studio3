/*******************************************************************************
 * Copyright (c) 2006, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 * Martin Oberhuber (Wind River) - [204796] Terminal should allow setting the encoding to use
 ******************************************************************************/
package org.eclipse.tm.internal.terminal.control;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;

/**
 * @author Michael Scharf
 *
 */
public interface ITerminalViewControl {
	/**
	 * Set the encoding that the Terminal uses to decode byte streams into
	 * characters.
	 *
	 * @see ITerminalControl#setEncoding(String)
	 * @since org.eclipse.tm.terminal 2.0
	 */
	void setEncoding(String encoding) throws UnsupportedEncodingException;

	/**
	 * Get the Terminal's current encoding.
	 *
	 * @return the current Encoding of the Terminal.
	 * @see ITerminalControl#getEncoding()
	 * @since org.eclipse.tm.terminal 2.0
	 */
	String getEncoding();

    boolean isEmpty();
	void setFont(Font font);
	void setInvertedColors(boolean invert);
	Font getFont();
	/**
	 * @return the text control
	 */
	Control getControl();
	/**
	 * @return the root of all controls
	 */
	Control getRootControl();
    boolean isDisposed();
    void selectAll();
    void clearTerminal();
    void copy();
    void paste();
    String getSelection();
    TerminalState getState();
    Clipboard getClipboard();
    void disconnectTerminal();
    void disposeTerminal();
    String getSettingsSummary();
    ITerminalConnector[] getConnectors();
    boolean setFocus();
    ITerminalConnector getTerminalConnector();
    void setConnector(ITerminalConnector connector);
    void connectTerminal();
    /**
     * @param write a single character to terminal
     */
    void sendKey(char arg0);
	/**
	 * @param string write string to terminal
	 */
	public boolean pasteString(String string);

    boolean isConnected();

    /**
     * @param inputField null means no input field is shown
     */
    void setCommandInputField(ICommandInputField inputField);
    /**
     * @return null or the current input field
     */
    ICommandInputField getCommandInputField();

	/**
	 * @return the maximum number of lines to display
	 * in the terminal view. -1 means unlimited.
	 */
	public int getBufferLineLimit();

	/**
	 * @param bufferLineLimit the maximum number of lines to show
	 * in the terminal view. -1 means unlimited.
	 */
	public void setBufferLineLimit(int bufferLineLimit);
	boolean isScrollLock();
	void setScrollLock(boolean on);
}
