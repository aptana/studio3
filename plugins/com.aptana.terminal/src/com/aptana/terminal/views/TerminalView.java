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

package com.aptana.terminal.views;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;
import org.eclipse.tm.internal.terminal.control.TerminalViewControlFactory;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.connector.LocalTerminalConnector;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class TerminalView extends ViewPart implements ITerminalListener {

	public static final String ID = "com.aptana.terminal.views.terminal"; //$NON-NLS-1$

	private static final String PROP_TITLE = "title"; //$NON-NLS-1$
	private static final String PROP_WORKING_DIRECTORY = "workingDirectory"; //$NON-NLS-1$

	private ITerminalViewControl fCtlTerminal;
	private IMemento savedState = null;

	/**
	 * @param id
	 *            The secondary id of the view. Used to uniquely identify and address a specific instance of this view.
	 * @param title
	 *            the title used in the UI tab for the instance of the view.
	 * @param workingDirectory
	 *            The directory in which to set the view initially.
	 * @return
	 */
	public static TerminalView openView(String secondaryId, String title, IPath workingDirectory) {
		TerminalView view = null;
		secondaryId  = secondaryId != null ? secondaryId : Long.toHexString(System.currentTimeMillis());
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			view = (TerminalView) page.showView(TerminalView.ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
			view.initialize(title, workingDirectory);
		} catch (PartInitException e) {
			Activator.logError("Terminal view creation failed.", e);
		}
		return view;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		savedState = memento;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		fCtlTerminal = TerminalViewControlFactory.makeControl(this, parent, getTerminalConnectors());
		fCtlTerminal.setConnector(fCtlTerminal.getConnectors()[0]);
		if (getViewSite().getSecondaryId() == null || savedState != null) {
			if (savedState != null) {
				loadState(savedState);
			}
			fCtlTerminal.connectTerminal();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		IMemento child = memento.createChild(PROP_TITLE);
		child.putTextData(getPartName());
		child = memento.createChild(PROP_WORKING_DIRECTORY);
		IPath workingDirectory = getWorkingDirectory();
		if (workingDirectory != null) {
			child.putTextData(workingDirectory.toOSString());
		}
	}

	private void loadState(IMemento memento) {
		IMemento child = memento.getChild(PROP_TITLE);
		if (child != null) {
			setPartName(child.getTextData());
		}
		child = memento.getChild(PROP_WORKING_DIRECTORY);
		if (child != null) {
			String value = child.getTextData();
			if (value != null) {
				setWorkingDirectory(Path.fromOSString(value));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		fCtlTerminal.setFocus();
	}
	
	private ITerminalConnector[] getTerminalConnectors() {
		return new ITerminalConnector[] { TerminalConnectorExtension.makeTerminalConnector(LocalTerminalConnector.ID) };
	}

	@Override
	public void setState(TerminalState state) {
	}

	@Override
	public void setTerminalTitle(String title) {
		setContentDescription(title);
	}
	
	protected void initialize(String title, IPath workingDirectory) {
		setPartName(title);
		setWorkingDirectory(workingDirectory);
		fCtlTerminal.connectTerminal();		
	}
	
	protected void setWorkingDirectory(IPath workingDirectory) {
		if (workingDirectory != null) {
			LocalTerminalConnector localTerminalConnector = (LocalTerminalConnector) fCtlTerminal.getTerminalConnector().getAdapter(LocalTerminalConnector.class);
			if (localTerminalConnector != null) {
				localTerminalConnector.setWorkingDirectory(workingDirectory);
			}		
		}
	}
	
	protected IPath getWorkingDirectory() {
		LocalTerminalConnector localTerminalConnector = (LocalTerminalConnector) fCtlTerminal.getTerminalConnector().getAdapter(LocalTerminalConnector.class);
		if (localTerminalConnector != null) {
			return localTerminalConnector.getWorkingDirectory();
		}
		return null;
	}
	
	public void sendInput(String text) {
		fCtlTerminal.pasteString(text);
	}
	
}
