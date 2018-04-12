/*******************************************************************************
 * Copyright (c) 2006, 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Michael Scharf
 *
 * TODO: Michael Scharf: provide a mechanism to set an error string
 * TODO: Michael Scharf: provide a long description of a wizard
 * TODO: Michael Scharf: allow multiple pages to be generated
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API without
 * consulting with the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 */
public interface ISettingsPage {
	/**
	 * Create a page to be shown in a dialog or wizard to setup the connection.
	 * @param parent
	 */
	void createControl(Composite parent);

	/**
	 * Called before the page is shown. Loads the state from the {@link ITerminalConnector}.
	 */
	void loadSettings();

	/**
	 * Called when the OK button is pressed.
	 */
	void saveSettings();

	/**
	 * @return true if the 
	 */
	boolean validateSettings();
	
}
