/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.SelectedTemplateComposite;

/**
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class CommonWizardNewProjectCreationPage extends WizardNewProjectCreationPage implements
		IWizardProjectCreationPage
{

	private Label warningLabel;

	// Initial Project template
	private IProjectTemplate projectTemplate = null;

	/**
	 * Constructs a new common new project creation page.
	 * 
	 * @param pageName
	 */
	public CommonWizardNewProjectCreationPage(String pageName, IProjectTemplate projectTemplate)
	{
		super(pageName);
		this.projectTemplate = projectTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.internal.wizards.IWizardProjectCreationPage#isCloneFromGit()
	 */
	public boolean isCloneFromGit()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.internal.wizards.IWizardProjectCreationPage#getCloneURI()
	 */
	public String getCloneURI()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);

		createProjectTemplateSection((Composite) getControl());
		createWarningArea();
	}

	private void createWarningArea()
	{
		Composite control = (Composite) getControl();
		final Font font = new Font(control.getDisplay(), SWTUtils.italicizedFont(getFont()));

		warningLabel = new Label(control, SWT.WRAP);
		warningLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.create());
		warningLabel.setFont(font);
		warningLabel.setForeground(UIUtils.getDisplay().getSystemColor(SWT.COLOR_RED));

		control.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (font != null && !font.isDisposed())
				{
					font.dispose();
				}
			}
		});
	}

	protected void createProjectTemplateSection(Composite parent)
	{
		if (projectTemplate != null)
		{
			new SelectedTemplateComposite(parent, projectTemplate);
		}

		return;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#validatePage()
	 */
	@Override
	protected boolean validatePage()
	{
		// Check whether the given location already contains files
		boolean isValid = super.validatePage();

		if (warningLabel != null)
		{
			warningLabel.setText(StringUtil.EMPTY);
		}

		if (isValid)
		{
			File locationFile = getLocationPath().toFile();

			if (!useDefaults() && locationFile.exists())
			{
				String[] files = locationFile.list();
				if (files != null && files.length > 0)
				{
					warningLabel
							.setText(Messages.CommonWizardNewProjectCreationPage_location_has_existing_content_warning);
					warningLabel.getParent().layout(true);
				}
			}
		}

		return isValid;
	}

}
