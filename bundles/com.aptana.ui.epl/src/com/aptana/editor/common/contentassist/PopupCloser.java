/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.internal.text.DelayedInputChangeListener;
import org.eclipse.jface.internal.text.InformationControlReplacer;
import org.eclipse.jface.text.IDelayedInputChangeProvider;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension5;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * A generic closer class used to monitor various interface events in order to determine whether a content assistant
 * should be terminated and all associated windows be closed.
 */
@SuppressWarnings("restriction")
class PopupCloser extends ShellAdapter implements FocusListener, SelectionListener, Listener
{

	/** The content assistant to be monitored. */
	private ContentAssistant fContentAssistant;
	/** The table of a selector popup opened by the content assistant. */
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
	 * The display on which some filters are registered.
	 * 
	 * @since 3.4
	 */
	private Display fDisplay;
	/**
	 * The additional info controller, or <code>null</code>.
	 * 
	 * @since 3.4
	 */
	private AdditionalInfoController fAdditionalInfoController;

	/**
	 * Installs this closer on the given table opened by the given content assistant.
	 * 
	 * @param contentAssistant
	 *            the content assistant
	 * @param table
	 *            the table to be tracked
	 */
	public void install(ContentAssistant contentAssistant, Table table)
	{
		install(contentAssistant, table, null);
	}

	/**
	 * Installs this closer on the given table opened by the given content assistant.
	 * 
	 * @param contentAssistant
	 *            the content assistant
	 * @param table
	 *            the table to be tracked
	 * @param additionalInfoController
	 *            the additional info controller, or <code>null</code>
	 * @since 3.4
	 */
	public void install(ContentAssistant contentAssistant, Table table,
			AdditionalInfoController additionalInfoController)
	{
		fContentAssistant = contentAssistant;
		fTable = table;
		fAdditionalInfoController = additionalInfoController;

		if (Helper.okToUse(fTable))
		{
			fShell = fTable.getShell();
			fDisplay = fShell.getDisplay();

			fShell.addShellListener(this);
			fTable.addFocusListener(this);
			fScrollbar = fTable.getVerticalBar();
			if (fScrollbar != null)
			{
				fScrollbar.addSelectionListener(this);
			}

			fDisplay.addFilter(SWT.Activate, this);
			fDisplay.addFilter(SWT.MouseWheel, this);

			fDisplay.addFilter(SWT.Deactivate, this);

			fDisplay.addFilter(SWT.MouseUp, this);
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
		if (fDisplay != null && !fDisplay.isDisposed())
		{
			fDisplay.removeFilter(SWT.Activate, this);
			fDisplay.removeFilter(SWT.MouseWheel, this);

			fDisplay.removeFilter(SWT.Deactivate, this);

			fDisplay.removeFilter(SWT.MouseUp, this);
		}
	}

	/*
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
		fScrollbarClicked = true;
	}

	/*
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
		fScrollbarClicked = true;
	}

	/*
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	public void focusGained(FocusEvent e)
	{
	}

	/*
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

	/*
	 * @see org.eclipse.swt.events.ShellAdapter#shellDeactivated(org.eclipse.swt.events.ShellEvent)
	 * @since 3.1
	 */
	public void shellDeactivated(ShellEvent e)
	{
		if (fContentAssistant != null && fDisplay != null)
		{
			fDisplay.asyncExec(new Runnable()
			{
				public void run()
				{
					/*
					 * The asyncExec is a workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=235556 :
					 * fContentAssistant.hasProposalPopupFocus() is still true during the shellDeactivated(..) event.
					 */
					if (fContentAssistant != null && !fContentAssistant.hasProposalPopupFocus()
							&& !isAdditionalInfoInFocus())
					{
						fContentAssistant.hide();
					}
				}
			});
		}
	}

	public boolean isAdditionalInfoInFocus()
	{
		if (fAdditionalInfoController != null)
		{
			IInformationControl control2 = fAdditionalInfoController.getCurrentInformationControl2();
			return control2 != null && control2.isFocusControl();
		}
		return false;
	}

	/*
	 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
	 * @since 3.1
	 */
	public void shellClosed(ShellEvent e)
	{
		if (fContentAssistant != null)
		{
			fContentAssistant.hide();
		}
	}

	/*
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 * @since 3.4
	 */
	public void handleEvent(Event event)
	{
		switch (event.type)
		{
			case SWT.Activate:
			case SWT.MouseWheel:
				if (fAdditionalInfoController == null)
				{
					return;
				}
				if (event.widget == fShell || event.widget == fTable || event.widget == fScrollbar)
				{
					return;
				}

				if (fAdditionalInfoController.getInternalAccessor().getInformationControlReplacer() == null)
				{
					fAdditionalInfoController.hideInformationControl();
				}
				else if (!fAdditionalInfoController.getInternalAccessor().isReplaceInProgress())
				{
					IInformationControl infoControl = fAdditionalInfoController.getCurrentInformationControl2();
					// During isReplaceInProgress(), events can come from the replacing information control
					if (event.widget instanceof Control && infoControl instanceof IInformationControlExtension5)
					{
						Control control = (Control) event.widget;
						IInformationControlExtension5 iControl5 = (IInformationControlExtension5) infoControl;
						if (!(iControl5.containsControl(control)))
						{
							fAdditionalInfoController.hideInformationControl();
						}
						else if (event.type == SWT.MouseWheel)
						{
							fAdditionalInfoController.getInternalAccessor().replaceInformationControl(false);
						}
					}
					else if (infoControl != null && infoControl.isFocusControl())
					{
						// TODO: SG - Once the control have a replacer, we can enable this.
						// fAdditionalInfoController.getInternalAccessor().replaceInformationControl(true);
					}
				}
				break;

			case SWT.MouseUp:
				if (fAdditionalInfoController == null
						|| fAdditionalInfoController.getInternalAccessor().isReplaceInProgress())
				{
					break;
				}
				if (event.widget instanceof Control)
				{
					Control control = (Control) event.widget;
					IInformationControl infoControl = fAdditionalInfoController.getCurrentInformationControl2();
					if (infoControl instanceof IInformationControlExtension5)
					{
						final IInformationControlExtension5 iControl5 = (IInformationControlExtension5) infoControl;
						if (iControl5.containsControl(control))
						{
							if (infoControl instanceof IDelayedInputChangeProvider)
							{
								final IDelayedInputChangeProvider delayedICP = (IDelayedInputChangeProvider) infoControl;
								final IInputChangedListener inputChangeListener = new DelayedInputChangeListener(
										delayedICP, fAdditionalInfoController.getInternalAccessor()
												.getInformationControlReplacer());
								delayedICP.setDelayedInputChangeListener(inputChangeListener);
								// cancel automatic input updating after a small timeout:
								control.getShell().getDisplay().timerExec(1000, new Runnable()
								{
									public void run()
									{
										delayedICP.setDelayedInputChangeListener(null);
									}
								});
							}

							// XXX: workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=212392 :
							control.getShell().getDisplay().asyncExec(new Runnable()
							{
								public void run()
								{
									fAdditionalInfoController.getInternalAccessor().replaceInformationControl(true);
								}
							});
						}
					}
				}
				break;

			case SWT.Deactivate:
				if (fAdditionalInfoController == null)
				{
					break;
				}
				InformationControlReplacer replacer = fAdditionalInfoController.getInternalAccessor()
						.getInformationControlReplacer();
				if (replacer != null && fContentAssistant != null)
				{
					IInformationControl iControl = replacer.getCurrentInformationControl2();
					if (event.widget instanceof Control && iControl instanceof IInformationControlExtension5)
					{
						Control control = (Control) event.widget;
						IInformationControlExtension5 iControl5 = (IInformationControlExtension5) iControl;
						if (iControl5.containsControl(control))
						{
							control.getDisplay().asyncExec(new Runnable()
							{
								public void run()
								{
									if (fContentAssistant != null && !fContentAssistant.hasProposalPopupFocus())
										fContentAssistant.hide();
								}
							});
						}
					}
				}
				break;
		}
	}
}
