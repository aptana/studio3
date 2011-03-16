/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FTPPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private Button fReopenButton;

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().create());

		fReopenButton = new Button(main, SWT.CHECK);
		fReopenButton.setText(Messages.FTPPreferencePage_LBL_ReopenRemote);
		fReopenButton.setSelection(FTPPreferenceUtil.getReopenRemoteOnStartup());

		return main;
	}

	@Override
	protected void performDefaults()
	{
		fReopenButton.setSelection(false);
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		FTPPreferenceUtil.setReopenRemoteOnStartup(fReopenButton.getSelection());

		return super.performOk();
	}
}
