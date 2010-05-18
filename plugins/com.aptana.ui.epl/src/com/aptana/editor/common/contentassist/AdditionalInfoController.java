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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Displays the additional information available for a completion proposal.
 * 
 * @since 2.0
 */
class AdditionalInfoController extends AbstractInformationControlManager implements Runnable
{

	/**
	 * Internal table selection listener.
	 */
	private class TableSelectionListener implements SelectionListener
	{
		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			handleTableSelectionChanged();
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
	}

	/** The proposal table. */
	private Table fProposalTable;
	/** The thread controlling the delayed display of the additional info. */
	private Thread fThread;
	/** Indicates whether the display delay has been reset. */
	private boolean fIsReset = false;
	/** Object to synchronize display thread and table selection changes. */
	private final Object fMutex = new Object();
	/**
	 * Thread access lock. since 3.0
	 */
	private final Object fThreadAccess = new Object();
	/** Object to synchronize initial display of additional info */
	private Object fStartSignal;
	/** The table selection listener */
	private SelectionListener fSelectionListener = new TableSelectionListener();
	/** The delay after which additional information is displayed */
	private int fDelay;

	/**
	 * Creates a new additional information controller.
	 * 
	 * @param creator
	 *            the information control creator to be used by this controller
	 * @param delay
	 *            time in milliseconds after which additional info should be displayed
	 */
	AdditionalInfoController(IInformationControlCreator creator, int delay)
	{
		super(creator);
		fDelay = delay;
		setAnchor(ANCHOR_RIGHT);
		setFallbackAnchors(new Anchor[] { ANCHOR_RIGHT, ANCHOR_LEFT });
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.AbstractInformationControlManager#install(org.eclipse.swt.widgets.Control)
	 */
	public void install(Control control)
	{

		if (fProposalTable == control)
		{
			// already installed
			return;
		}

		super.install(control);

		//Assert.isTrue(control instanceof Table);
		fProposalTable = (Table) control;
		fProposalTable.addSelectionListener(fSelectionListener);
		synchronized (fThreadAccess)
		{
			if (fThread != null)
			{
				fThread.interrupt();
			}
			fThread = new Thread(this, "Aptana: InfoPopup.info_delay_timer_name"); //$NON-NLS-1$

			fStartSignal = new Object();
			synchronized (fStartSignal)
			{
				fThread.start();
				try
				{
					// wait until thread is ready
					fStartSignal.wait();
				}
				catch (InterruptedException x)
				{
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.AbstractInformationControlManager#disposeInformationControl()
	 */
	public void disposeInformationControl()
	{

		synchronized (fThreadAccess)
		{
			if (fThread != null)
			{
				fThread.interrupt();
				fThread = null;
			}
		}

		if (fProposalTable != null && !fProposalTable.isDisposed())
		{
			fProposalTable.removeSelectionListener(fSelectionListener);
			fProposalTable = null;
		}

		super.disposeInformationControl();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try
		{
			while (true)
			{

				synchronized (fMutex)
				{

					if (fStartSignal != null)
					{
						synchronized (fStartSignal)
						{
							fStartSignal.notifyAll();
							fStartSignal = null;
						}
					}

					// Wait for a selection event to occur.
					fMutex.wait();

					while (true)
					{
						fIsReset = false;
						// Delay before showing the popup.
						fMutex.wait(fDelay);
						if (!fIsReset)
						{
							break;
						}
					}
				}

				if (fProposalTable != null && !fProposalTable.isDisposed())
				{
					fProposalTable.getDisplay().asyncExec(new Runnable()
					{
						public void run()
						{
							if (!fIsReset)
							{
								showInformation();
							}
						}
					});
				}

			}
		}
		catch (InterruptedException e)
		{
		}

		synchronized (fThreadAccess)
		{
			// only null fThread if it is us!
			if (Thread.currentThread() == fThread)
			{
				fThread = null;
			}
		}
	}

	/**
	 * Handles a change of the line selected in the associated selector.
	 */
	public void handleTableSelectionChanged()
	{

		if (fProposalTable != null && !fProposalTable.isDisposed() && fProposalTable.isVisible())
		{
			synchronized (fMutex)
			{
				fIsReset = true;
				fMutex.notifyAll();
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.AbstractInformationControlManager#computeInformation()
	 */
	protected void computeInformation()
	{

		if (fProposalTable == null || fProposalTable.isDisposed())
		{
			return;
		}

		TableItem[] selection = fProposalTable.getSelection();
		if (selection != null && selection.length > 0)
		{

			TableItem item = selection[0];

			// compute information
			String information = null;
			Object d = item.getData();

			if (d instanceof ICompletionProposal)
			{
				ICompletionProposal p = (ICompletionProposal) d;
				information = p.getAdditionalProposalInfo();
			}

			if (d instanceof ICompletionProposalExtension3)
			{
				setCustomInformationControlCreator(((ICompletionProposalExtension3) d).getInformationControlCreator());
			}
			else
			{
				setCustomInformationControlCreator(null);
			}

			// compute subject area
			setMargins(4, 4);
			Rectangle area = fProposalTable.getBounds();

			area.x = 0; // subject area is the whole subject control
			area.y = 0;

			Rectangle parentBounds = fProposalTable.getParent().getBounds();
			Rectangle itemBounds = item.getBounds(0);

			int verticalOffset = parentBounds.y + itemBounds.y;
			// The SWT/Cocoa bug ( https://bugs.eclipse.org/bugs/show_bug.cgi?id=275617 ) causes problems
			// in computation of the y offset of the information pop-up window for alignment with
			// the selected completion item. Disable the smart positioning behavior for macosx/cocoa.
			if (Platform.getWS().equals(Platform.WS_COCOA)) {
				verticalOffset = parentBounds.y;
			}

			// set information & subject area
			setInformation(information, area, verticalOffset);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.AbstractInformationControlManager#computeSizeConstraints(org.eclipse.swt.widgets.Control,
	 *      org.eclipse.jface.text.IInformationControl)
	 */
	protected Point computeSizeConstraints(Control subjectControl, IInformationControl informationControl)
	{
		Point sizeConstraint = super.computeSizeConstraints(subjectControl, informationControl);
		Point size = subjectControl.getSize();

		Rectangle otherTrim = subjectControl.getShell().computeTrim(0, 0, 0, 0);
		size.x += otherTrim.width;
		size.y += otherTrim.height;

		if (informationControl instanceof IInformationControlExtension3)
		{
			Rectangle thisTrim = ((IInformationControlExtension3) informationControl).computeTrim();
			size.x -= thisTrim.width;
			size.y -= thisTrim.height;
		}

		if (sizeConstraint.x < size.x)
		{
			sizeConstraint.x = size.x;
		}
		if (sizeConstraint.y < size.y)
		{
			sizeConstraint.y = size.y;
		}
		return sizeConstraint;
	}
}
