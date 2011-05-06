/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.ui.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class ExportConnectionsPage extends WizardPage
{

	private Text filePathText;
	private Button browseButton;
	private Button overwriteButton;

	protected ExportConnectionsPage()
	{
		super("exportConnections"); //$NON-NLS-1$
	}

	public IPath getLocation()
	{
		return Path.fromOSString(filePathText.getText());
	}

	public boolean isOverwritingExistingFile()
	{
		return overwriteButton.getSelection();
	}

	public void createControl(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

		Label label = new Label(main, SWT.NONE);
		label.setText(Messages.ExportConnectionsPage_LBL_File);

		filePathText = new Text(main, SWT.BORDER);
		filePathText.setText(Platform.getPreferencesService().getString(SyncingUIPlugin.PLUGIN_ID,
				IPreferenceConstants.EXPORT_INITIAL_PATH, StringUtil.EMPTY, null));
		filePathText
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		filePathText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		});

		browseButton = new Button(main, SWT.PUSH);
		browseButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		browseButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browse();
			}
		});

		Group group = new Group(main, SWT.NONE);
		group.setLayout(GridLayoutFactory.swtDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
		group.setText(Messages.ExportConnectionsPage_LBL_Options);

		overwriteButton = new Button(group, SWT.CHECK);
		overwriteButton.setText(Messages.ExportConnectionsPage_LBL_OverwriteWithoutWarning);
		overwriteButton.setSelection(Platform.getPreferencesService().getBoolean(SyncingUIPlugin.PLUGIN_ID,
				IPreferenceConstants.EXPORT_OVEWRITE_FILE_WITHOUT_WARNING, false, null));

		setTitle(Messages.ExportConnectionsPage_Title);
		setDescription(Messages.ExportConnectionsPage_Description);
		setControl(main);

		validate();
	}

	private void browse()
	{
		FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
		String result = dialog.open();
		if (result != null)
		{
			filePathText.setText(result);
			validate();
		}
	}

	private void validate()
	{
		String error = null;
		String path = filePathText.getText();
		if (StringUtil.isEmpty(path))
		{
			error = Messages.ExportConnectionsPage_ERR_EmptyFile;
		}
		else
		{
			File file = new File(path);
			if (file.isDirectory())
			{
				error = Messages.ExportConnectionsPage_ERR_Directory;
			}
		}
		setErrorMessage(error);
		setPageComplete(error == null);
	}
}
