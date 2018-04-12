/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.preview.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.aptana.core.util.StringUtil;
import com.aptana.preview.ProjectPreviewUtil;
import com.aptana.webserver.core.IServer;

/**
 * @author Max Stepanov
 * @author Michael Xia
 */
public class ProjectPreviewPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

	private PreviewSettingComposite previewComposite;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		Label label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.ProjectPreviewPropertyPage_preview_server_label));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		previewComposite = new PreviewSettingComposite(main);
		previewComposite.setSelectedServer(getServer());
		previewComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		return main;
	}

	@Override
	public boolean performOk()
	{
		// saves the settings for the project
		ProjectPreviewUtil.setServerConfiguration(getProject(), previewComposite.getSelectedServer());
		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		previewComposite.setSelectedServer(getServer());
		super.performDefaults();
	}

	private IProject getProject()
	{
		return (IProject) getElement().getAdapter(IProject.class);
	}

	private IServer getServer()
	{
		return ProjectPreviewUtil.getServerConfiguration(getProject());
	}
}
