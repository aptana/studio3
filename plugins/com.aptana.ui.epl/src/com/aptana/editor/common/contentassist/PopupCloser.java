/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.editor.common.contentassist;

/***********************************************************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: IBM Corporation - initial API and implementation
 **********************************************************************************************************************/

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * A generic closer class used to monitor various interface events in order to determine whether a code assistant should
 * be terminated and all associated windows be closed.
 */
class PopupCloser extends ShellAdapter implements FocusListener, SelectionListener
{

	/** The code assistant to be monitored. */
	private ContentAssistant fContentAssistant;
	/** The table of a selector popup opened by the code assistant. */
	private Table fTable;
	/** The scroll bar of the table for the selector popup. */
	private ScrollBar fScrollbar;
	/** Indicates whether the scroll bar thumb has been grabbed. */
	private boolean fScrollbarClicked = false;
	/**
	 * The shell on which some listeners are registered.
	 * 
	 * @since 3.1
	 */
	private Shell fShell;

	/**
	 * Installs this closer on the given table opened by the given code assistant.
	 * 
	 * @param contentAssistant
	 *            the code assistant
	 * @param table
	 *            the table to be tracked
	 */
	public void install(ContentAssistant contentAssistant, Table table)
	{
		fContentAssistant = contentAssistant;
		fTable = table;
		if (Helper.okToUse(fTable))
		{
			Shell shell = fTable.getShell();
			if (Helper.okToUse(shell))
			{
				fShell = shell;
				fShell.addShellListener(this);
			}

			fTable.addFocusListener(this);
			fScrollbar = fTable.getVerticalBar();
			if (fScrollbar != null)
			{
				fScrollbar.addSelectionListener(this);
			}
		}
	}

	/**
	 * Uninstalls this closer if previously installed.
	 */
	public void uninstall()
	{
		fContentAssistant = null;
		if (Helper.okToUse(fShell))
		{
			fShell.removeShellListener(this);
		}
		fShell = null;
		if (Helper.okToUse(fScrollbar))
		{
			fScrollbar.removeSelectionListener(this);
		}
		if (Helper.okToUse(fTable))
		{
			fTable.removeFocusListener(this);
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
		fScrollbarClicked = true;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
		fScrollbarClicked = true;
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusGained(FocusEvent e)
	{
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusLost(final FocusEvent e)
	{
		fScrollbarClicked = false;
		Display d = fTable.getDisplay();
		d.asyncExec(new Runnable()
		{
			public void run()
			{
				if (Helper.okToUse(fTable) && !fTable.isFocusControl() && !fScrollbarClicked
						&& fContentAssistant != null)
				{
					fContentAssistant.popupFocusLost(e);
				}
			}
		});
	}

	/**
	 * @see org.eclipse.swt.events.ShellListener#shellDeactivated(org.eclipse.swt.events.ShellEvent)
	 */
	public void shellDeactivated(ShellEvent e)
	{
		if (fContentAssistant != null)
		{
			fContentAssistant.hide();
		}
	}

	/**
	 * @see org.eclipse.swt.events.ShellListener#shellClosed(org.eclipse.swt.events.ShellEvent)
	 */
	public void shellClosed(ShellEvent e)
	{
		if (fContentAssistant != null)
		{
			fContentAssistant.hide();
		}
	}
}
