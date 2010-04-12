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

import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class TerminalActionCopy extends AbstractTerminalAction {
	public TerminalActionCopy() {
		super(TerminalActionCopy.class.getName());
		ISharedImages si = PlatformUI.getWorkbench().getSharedImages();
		setupAction(ActionMessages.COPY, ActionMessages.COPY, si
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY), si
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY), si
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED), true);
	}

	public TerminalActionCopy(ITerminalViewControl target) {
		super(target, TerminalActionCopy.class.getName());
		ISharedImages si = PlatformUI.getWorkbench().getSharedImages();
		setupAction(ActionMessages.COPY, ActionMessages.COPY, si
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY), si
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY), si
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED), true);
	}

	public void run() {
		ITerminalViewControl target = getTarget();
		if (target != null) {
			String selection = target.getSelection();

			if (!selection.equals("")) {//$NON-NLS-1$
				target.copy();
			} else {
				target.sendKey('\u0003');
			}
		}
	}

	public void updateAction(boolean aboutToShow) {
		ITerminalViewControl target = getTarget();
		boolean bEnabled = target != null;
		if (aboutToShow && bEnabled) {
			bEnabled = target.getSelection().length() > 0;
		}
		setEnabled(bEnabled);
	}
}
