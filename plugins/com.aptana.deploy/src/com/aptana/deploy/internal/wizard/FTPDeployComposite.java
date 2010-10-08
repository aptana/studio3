/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.deploy.internal.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ide.ui.secureftp.internal.CommonFTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployComposite extends CommonFTPConnectionPropertyComposite implements SelectionListener
{

	private Button autoSyncCheckbox;
	private Button uploadButton;
	private Button downloadButton;
	private Button syncButton;

	public FTPDeployComposite(Composite parent, int style, IBaseRemoteConnectionPoint connectionPoint, Listener listener)
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

	private void createAutoSyncOptions(Composite parent)
	{
		Composite sync = new Composite(parent, SWT.NONE);
		sync.setLayout(GridLayoutFactory.fillDefaults().create());
		sync.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.END).span(2, 1).create());

		autoSyncCheckbox = new Button(sync, SWT.CHECK);
		autoSyncCheckbox.setText(Messages.FTPDeployComposite_AutoSync);
		autoSyncCheckbox.setSelection(true);
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
