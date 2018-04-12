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
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.control;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;

public class TerminalViewControlFactory {
	public static ITerminalViewControl makeControl(ITerminalListener target, Composite wndParent, ITerminalConnector[] connectors) {
		return new VT100TerminalControl(target, wndParent, connectors);
	}
}
