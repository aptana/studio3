/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ftp.ui.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ui.secureftp.internal.CommonFTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployComposite extends CommonFTPConnectionPropertyComposite implements SelectionListener
{

	private Button autoSyncCheckbox;
	private Button uploadButton;
	private Button downloadButton;
	private Button syncButton;

	public FTPDeployComposite(Composite parent, int style, IBaseRemoteConnectionPoint connectionPoint, IListener listener)
	{
		super(parent, style, connectionPoint, listener);

		createAutoSyncOptions(this);
		optionsExpandable.setExpanded(true);
	}

	public boolean isAutoSyncSelected()
	{
		return autoSyncCheckbox.getSelection();
	}

	public SyncDirection getSyncDirection()
	{
		if (uploadButton.getSelection())
		{
			return SyncDirection.UPLOAD;
		}
		if (downloadButton.getSelection())
		{
			return SyncDirection.DOWNLOAD;
		}
		return SyncDirection.BOTH;
	}

	public void setAutoSyncSelected(boolean selected)
	{
		autoSyncCheckbox.setSelection(selected);
		updateEnableState();
	}

	public void setSyncDirection(SyncDirection direction)
	{
		if (direction != null)
		{
			switch (direction)
			{
				case UPLOAD:
					uploadButton.setSelection(true);
					downloadButton.setSelection(false);
					syncButton.setSelection(false);
					break;
				case DOWNLOAD:
					downloadButton.setSelection(true);
					uploadButton.setSelection(false);
					syncButton.setSelection(false);
					break;
				case BOTH:
					syncButton.setSelection(true);
					uploadButton.setSelection(false);
					downloadButton.setSelection(false);
			}
		}
	}

	private void createAutoSyncOptions(Composite parent)
	{
		Composite sync = new Composite(parent, SWT.NONE);
		sync.setLayout(GridLayoutFactory.fillDefaults().create());
		sync.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.END).span(2, 1).create());

		autoSyncCheckbox = new Button(sync, SWT.CHECK);
		autoSyncCheckbox.setText(Messages.FTPDeployComposite_AutoSync);
		autoSyncCheckbox.addSelectionListener(this);

		uploadButton = new Button(sync, SWT.RADIO);
		uploadButton.setText(Messages.FTPDeployComposite_Upload);
		uploadButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());

		downloadButton = new Button(sync, SWT.RADIO);
		downloadButton.setText(Messages.FTPDeployComposite_Download);
		downloadButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());

		syncButton = new Button(sync, SWT.RADIO);
		syncButton.setText(Messages.FTPDeployComposite_Synchronize);
		syncButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());
		syncButton.setSelection(true);

		updateEnableState();
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();

		if (source == autoSyncCheckbox)
		{
			updateEnableState();
		}
	}

	private void updateEnableState()
	{
		boolean enabled = autoSyncCheckbox.getSelection();
		uploadButton.setEnabled(enabled);
		downloadButton.setEnabled(enabled);
		syncButton.setEnabled(enabled);
	}
}
