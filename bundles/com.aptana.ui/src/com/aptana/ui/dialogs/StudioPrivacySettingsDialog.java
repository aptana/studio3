/**
 * Axway Appcelerator Studio
 * Copyright (c) 2018 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ui.util.WorkbenchBrowserUtil;
import com.aptana.usage.preferences.IPreferenceConstants;

/**
 * @author Kondal Kolipaka
 */
public class StudioPrivacySettingsDialog extends MessageDialog
{
	private boolean sendUsageData;
	private static String dialogTitle = Messages.StudioPrivacySettingsDialog_DialogTitle;
	private static String dialogMessage = Messages.StudioPrivacySettingsDialog_DialogMessage
			+ Messages.StudioPrivacySettingsDialog_DialogMessagg2;

	public StudioPrivacySettingsDialog(Shell parentShell)
	{
		super(parentShell, dialogTitle, null, dialogMessage, MessageDialog.QUESTION, getDialogButtonLabels(), 0);
	}

	@Override
	protected Control createCustomArea(Composite parent)
	{
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		group.setLayoutData(GridDataFactory.fillDefaults().indent(37, SWT.DEFAULT).create());

		final Button usageDataBox = new Button(group, SWT.CHECK);
		usageDataBox.setText(Messages.StudioPrivacySettingsDialog_UsageDataCheckText);

		usageDataBox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				sendUsageData = usageDataBox.getSelection();
			}
		});

		// placeholder
		new Label(group, SWT.NONE);
		createNavigationLabelAndLink(group);

		return usageDataBox;
	}

	public boolean isSendUsageData()
	{
		return sendUsageData;
	}

	private static String[] getDialogButtonLabels()
	{
		String[] buttons = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
		return buttons;
	}

	private void createNavigationLabelAndLink(Composite links)
	{
		Link link = new Link(links, SWT.NONE);
		link.setText("For more information, please refer to <a>Axway's Privacy compliance program</a>.");
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				WorkbenchBrowserUtil.openURL(IPreferenceConstants.AXWAY_GDPR_URL);
			}
		});

	}

}
