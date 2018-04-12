/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.js.debug.ui.internal.actions.Messages;

/**
 * @author Max Stepanov
 */
public class HitCountDialog extends InputDialog {
	private boolean enabled;

	public HitCountDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Button checkbox = new Button(composite, SWT.CHECK);
		checkbox.setText(Messages.BreakpointHitCountAction_EnableHitCount);
		GridDataFactory.fillDefaults().grab(true, false)
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
				.applyTo(checkbox);
		checkbox.setFont(parent.getFont());

		checkbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				enabled = ((Button) e.widget).getSelection();
				getText().setEnabled(enabled);
				if (enabled) {
					validateInput();
				} else {
					setErrorMessage(null);
				}
			}
		});

		enabled = true;
		checkbox.setSelection(enabled);

		return composite;
	}

	public boolean isHitCountEnabled() {
		return enabled;
	}
}