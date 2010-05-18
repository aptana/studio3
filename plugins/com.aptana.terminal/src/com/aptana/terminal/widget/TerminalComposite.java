/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.progress.UIJob;

import com.aptana.terminal.connector.LocalTerminalConnector;
import com.aptana.terminal.internal.emulator.VT100TerminalControl;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class TerminalComposite extends Composite {

	private static final ITerminalListener EMPTY_LISTENER = new ITerminalListener() {
		@Override
		public void setState(TerminalState state) {
		}

		@Override
		public void setTerminalTitle(String title) {
		}
	};
	
	private VT100TerminalControl fCtlTerminal;
	private List<String> inputs = new ArrayList<String>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public TerminalComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(GridLayoutFactory.fillDefaults().create());
		fCtlTerminal = new VT100TerminalControl(EMPTY_LISTENER, parent, getTerminalConnectors());
		fCtlTerminal.setConnector(fCtlTerminal.getConnectors()[0]);
	}
	
	public void connect() {
		Job job = new UIJob("Terminal connect") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				fCtlTerminal.connectTerminal();
				sendInputs();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule(100);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		fCtlTerminal.disposeTerminal();
		super.dispose();
	}

	private ITerminalConnector[] getTerminalConnectors() {
		ITerminalConnector connector = TerminalConnectorExtension.makeTerminalConnector(LocalTerminalConnector.ID);
		if (connector != null) {
			connector.getInitializationErrorMessage();
		}
		return new ITerminalConnector[] { connector };
	}

	public void setWorkingDirectory(IPath workingDirectory) {
		if (workingDirectory != null) {
			LocalTerminalConnector localTerminalConnector = (LocalTerminalConnector) fCtlTerminal.getTerminalConnector().getAdapter(LocalTerminalConnector.class);
			if (localTerminalConnector != null) {
				localTerminalConnector.setWorkingDirectory(workingDirectory);
			}		
		}
	}

	public void sendInput(String text) {
		synchronized (inputs) {
			inputs.add(text);
			sendInputs();
		}
	}

	private void sendInputs() {
		synchronized (inputs) {
			if (!fCtlTerminal.isConnected()) {
				return;
			}
			while (!inputs.isEmpty()) {
				String text = inputs.remove(0);
				fCtlTerminal.pasteString(text);
			}
		}
	}

	/**
	 * 
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#clearTerminal()
	 */
	public void clear() {
		fCtlTerminal.clearTerminal();
	}

	/**
	 * @return
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#isEmpty()
	 */
	public boolean isEmpty() {
		return fCtlTerminal.isEmpty();
	}

}
