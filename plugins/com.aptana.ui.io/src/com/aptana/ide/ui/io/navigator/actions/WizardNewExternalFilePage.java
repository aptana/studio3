/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.internal.scripting.WizardNewFilePage;

public class WizardNewExternalFilePage extends WizardNewFilePage
{

	private String initialFilename;
	private boolean collectTemplates;

	private Text filenameText;

	public WizardNewExternalFilePage(String pageName)
	{
		this(pageName, StringUtil.EMPTY);
	}

	public WizardNewExternalFilePage(String pageName, String initialName)
	{
		this(pageName, initialName, true);
	}

	public WizardNewExternalFilePage(String pageName, String initialName, boolean collectTemplates)
	{
		super(pageName, StructuredSelection.EMPTY);
		initialFilename = initialName;
		this.collectTemplates = collectTemplates;
		setPageComplete(true);
	}

	@Override
	public void handleEvent(Event event)
	{
		if (collectTemplates)
		{
			super.handleEvent(event);
		}
	}

	@Override
	public String getFileName()
	{
		return filenameText.getText();
	}

	@Override
	public boolean canFlipToNextPage()
	{
		return collectTemplates && super.canFlipToNextPage();
	}

	@Override
	public void createControl(Composite parent)
	{
		initializeDialogUnits(parent);
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		main.setFont(parent.getFont());

		new Label(main, SWT.NONE).setText(Messages.WizardNewExternalFilePage_LBL_Filename);
		filenameText = new Text(main, SWT.BORDER | SWT.SINGLE);
		filenameText.setText(initialFilename);
		filenameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		filenameText.addListener(SWT.Modify, this);

		setErrorMessage(null);
		setMessage(null);
		setControl(main);
	}

	@Override
	public IPath getContainerFullPath()
	{
		return Path.EMPTY;
	}

	@Override
	public void setVisible(boolean visible)
	{
		getControl().setVisible(visible);
	}

	@Override
	protected boolean validatePage()
	{
		return true;
	}

	@Override
	protected InputStream getInitialContents()
	{
		return collectTemplates ? super.getInitialContents() : null;
	}
}
