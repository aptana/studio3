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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class ImportConnectionsPage extends WizardPage
{

	private Button workspaceButton;
	private Button fileButton;
	private Text filePathText;
	private Button browseButton;

	protected ImportConnectionsPage()
	{
		super("importConnections"); //$NON-NLS-1$
	}

	public boolean isWorkspaceSelected()
	{
		return workspaceButton.getSelection();
	}

	public IPath getLocation()
	{
		return Path.fromOSString(filePathText.getText());
	}

	public void createControl(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().create());

		Composite sourceType = new Composite(main, SWT.NONE);
		sourceType.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
		sourceType.setLayoutData(GridDataFactory.fillDefaults().create());

		Label label = new Label(sourceType, SWT.NONE);
		label.setText(Messages.ImportConnectionsPage_LBL_SourceType);

		workspaceButton = new Button(sourceType, SWT.RADIO);
		workspaceButton.setText(Messages.ImportConnectionsPage_SourceType_Workspace);
		workspaceButton.setSelection(true);
		SelectionAdapter selectionAdapter = new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				validate();
			}
		};
		workspaceButton.addSelectionListener(selectionAdapter);

		fileButton = new Button(sourceType, SWT.RADIO);
		fileButton.setText(Messages.ImportConnectionsPage_SourceType_File);
		fileButton.addSelectionListener(selectionAdapter);

		Composite source = new Composite(main, SWT.NONE);
		source.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		source.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		label = new Label(source, SWT.NONE);
		label.setText(Messages.ImportConnectionsPage_LBL_Path);
		filePathText = new Text(source, SWT.BORDER);
		filePathText
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		filePathText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		});
		browseButton = new Button(source, SWT.PUSH);
		browseButton.setText(StringUtil.ellipsify(com.aptana.core.CoreStrings.BROWSE));
		browseButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browse();
			}
		});

		setTitle(Messages.ImportConnectionsPage_Title);
		setDescription(Messages.ImportConnectionsPage_Description);
		setControl(main);

		validate();
	}

	private void browse()
	{
		String result = null;
		if (workspaceButton.getSelection())
		{
			DirectoryDialog dialog = new DirectoryDialog(getShell());
			result = dialog.open();
		}
		else
		{
			FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
			result = dialog.open();
		}
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
			error = Messages.ImportConnectionsPage_ERR_EmptyPath;
		}
		else
		{
			File file = new File(path);
			if (!file.exists())
			{
				error = Messages.ImportConnectionsPage_ERR_InvalidPath;
			}
			else if (workspaceButton.getSelection() && !file.isDirectory())
			{
				error = Messages.ImportConnectionsPage_ERR_InvalidDirectory;
			}
			else if (fileButton.getSelection() && file.isDirectory())
			{
				error = Messages.ImportConnectionsPage_ERR_InvalidFile;
			}
		}
		setErrorMessage(error);
		setPageComplete(error == null);
	}
}
