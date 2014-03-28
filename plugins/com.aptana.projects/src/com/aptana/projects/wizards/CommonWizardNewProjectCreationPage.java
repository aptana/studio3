/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.internal.wizards.Messages;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.SelectedTemplateComposite;
import com.aptana.ui.widgets.StepIndicatorComposite;
import com.aptana.ui.wizards.WizardNewProjectCreationPage;

/**
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class CommonWizardNewProjectCreationPage extends WizardNewProjectCreationPage implements
		IWizardProjectCreationPage, IStepIndicatorWizardPage
{

	private Label warningLabel;

	// Initial Project template
	private IProjectTemplate projectTemplate = null;

	// Used for step indicator composite
	private StepIndicatorComposite stepIndicatorComposite;
	private String[] stepNames;

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
		Composite pageComposite = new Composite(parent, SWT.NONE);
		GridLayout pageLayout = GridLayoutFactory.fillDefaults().spacing(0, 5).create();
		pageComposite.setLayout(pageLayout);
		pageComposite.setLayoutData(GridDataFactory.fillDefaults().create());

		stepIndicatorComposite = new StepIndicatorComposite(pageComposite, stepNames);
		stepIndicatorComposite.setSelection(getStepName());

		createTopArea(pageComposite);
		super.createControl(pageComposite);

		((Composite) getControl()).setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
		((Composite) getControl()).setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).create());
		setControl(pageComposite);

		createProjectTemplateSection(pageComposite);
		createWarningArea();
	}

	/**
	 * Be default the method does nothing. Subclass could override to add additional controls to the top of the wizard
	 * page.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createTopArea(Composite parent)
	{
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

	@SuppressWarnings("unused")
	private void createProjectTemplateSection(Composite parent)
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
			boolean existing = false;

			if (useDefaults())
			{
				String defaultLocation = TextProcessor.process(Platform.getLocation().append(getProjectName())
						.toOSString());
				File dir = new File(defaultLocation);
				if (dir.exists())
				{
					String[] files = dir.list();
					if (!ArrayUtil.isEmpty(files))
					{
						existing = true;
					}
				}
			}
			else if (locationFile.exists())
			{
				String[] files = locationFile.list();
				if (!ArrayUtil.isEmpty(files))
				{
					existing = true;
				}
			}
			if (existing)
			{
				warningLabel.setText(Messages.CommonWizardNewProjectCreationPage_location_has_existing_content_warning);
				warningLabel.getParent().layout(true);
			}
		}

		return isValid;
	}

	public String getStepName()
	{
		return Messages.NewProjectWizard_Step_Lbl;
	}

	public void initStepIndicator(String[] stepNames)
	{
		this.stepNames = stepNames;
	}
}
