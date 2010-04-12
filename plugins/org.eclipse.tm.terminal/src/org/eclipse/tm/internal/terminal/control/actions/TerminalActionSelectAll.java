/*******************************************************************************
 * Copyright (c) 2004, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 * The following Wind River employees contributed to the Terminal component
 * that contains this file: Chris Thew, Fran Litterio, Stephen Lamb,
 * Helmut Haigermoser and Ted Williams.
 *
 * Contributors:
 * Michael Scharf (Wind River) - split into core, view and connector plugins
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 * Anna Dushistova (MontaVista) - [227537] moved actions from terminal.view to terminal plugin
 * Uwe Stieber (Wind River) - [260372] [terminal] Certain terminal actions are enabled if no target terminal control is available
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.control.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;

public class TerminalActionSelectAll extends AbstractTerminalAction {
	public TerminalActionSelectAll() {
		super(TerminalActionSelectAll.class.getName());

		setupAction(ActionMessages.SELECTALL, ActionMessages.SELECTALL,
				(ImageDescriptor) null, null, null, false);
	}

	public TerminalActionSelectAll(ITerminalViewControl target) {
		super(target, TerminalActionSelectAll.class.getName());

		setupAction(ActionMessages.SELECTALL, ActionMessages.SELECTALL,
				(ImageDescriptor) null, null, null, false);
	}

	public void run() {
		ITerminalViewControl target = getTarget();
		if (target != null) {
			target.selectAll();
		}
	}

	public void updateAction(boolean aboutToShow) {
		ITerminalViewControl target = getTarget();
		setEnabled(target != null && !target.isEmpty());
	}
}
