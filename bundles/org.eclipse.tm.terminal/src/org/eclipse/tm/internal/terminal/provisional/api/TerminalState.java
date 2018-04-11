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
 * Michael Scharf (Wind River) - [262996] get rid of TerminalState.OPENED
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

/**
 * Represent the sate of a terminal connection.
 * In java 1.5 this would be an enum.
 * @author Michael Scharf
 *
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API without
 * consulting with the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 */
public class TerminalState {
	/**
	 * The terminal is not connected.
	 */
	public final static TerminalState CLOSED=new TerminalState("CLOSED"); //$NON-NLS-1$

	/**
	 * The terminal is about to connect.
	 */
	public final static TerminalState CONNECTING=new TerminalState("CONNECTING..."); //$NON-NLS-1$

	/**
	 * The terminal is connected.
	 */
	public final static TerminalState CONNECTED=new TerminalState("CONNECTED"); //$NON-NLS-1$

	private final String fState;

	public TerminalState(String state) {
		fState = state;
	}

	public String toString() {
		return fState;
	}
}
