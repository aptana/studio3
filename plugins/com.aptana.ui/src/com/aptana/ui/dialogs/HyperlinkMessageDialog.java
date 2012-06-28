/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author Max Stepanov
 *
 */
public class HyperlinkMessageDialog extends MessageDialog {

	private Link messageLink;
	
	/**
	 * @param parentShell
	 * @param dialogTitle
	 * @param dialogTitleImage
	 * @param dialogMessage
	 * @param dialogImageType
	 * @param dialogButtonLabels
	 * @param defaultIndex
	 */
	public HyperlinkMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IconAndMessageDialog#createMessageArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createMessageArea(Composite composite) {
		String message = this.message;
		this.message = null;
		Composite messageArea = (Composite) super.createMessageArea(composite);
		messageLink = new Link(messageArea, getMessageLabelStyle() | SWT.NO_FOCUS);
		messageLink.setText("<a></a>"+message); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
				.applyTo(messageLink);
		messageLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WorkbenchBrowserUtil.launchExternalBrowser(e.text);
			}
		});
		return messageArea;
	}

	public static boolean open(int kind, Shell parent, String title, String message, int style) {
		HyperlinkMessageDialog dialog = new HyperlinkMessageDialog(parent, title, null, message, kind, getButtonLabels(kind), 0);
		return dialog.open() == 0;
	}

	public static void openInformation(Shell parent, String title, String message) {
		open(INFORMATION, parent, title, message, SWT.NONE);
	}

	public static void openError(Shell parent, String title, String message) {
		open(ERROR, parent, title, message, SWT.NONE);
	}

	private static String[] getButtonLabels(int kind) {
		switch (kind) {
		case ERROR:
		case INFORMATION:
		case WARNING:
			return new String[] { IDialogConstants.OK_LABEL };
		case CONFIRM:
			return new String[] {
					IDialogConstants.OK_LABEL,
					IDialogConstants.CANCEL_LABEL };
		case QUESTION:
			return new String[] {
					IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL };
		case QUESTION_WITH_CANCEL:
			return new String[] {
					IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL };
		default:
			throw new IllegalArgumentException(
					"Illegal value for kind in HyperlinkMessageDialog.open()"); //$NON-NLS-1$
		}
	}

}
