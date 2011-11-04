/**
 * Aptana Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.projects.templates.IProjectTemplate;

/**
 * Composite used to display the selected template in a group control
 * 
 * @author nle
 */
public class SelectedTemplateComposite extends Composite
{
	private IProjectTemplate projectTemplate;
	private Group group;

	public SelectedTemplateComposite(Composite parent, IProjectTemplate projectTemplate)
	{
		super(parent, SWT.NONE);
		this.projectTemplate = projectTemplate;
		createContents();
	}

	protected void createContents()
	{
		setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		setLayout(GridLayoutFactory.fillDefaults().create());

		group = new Group(this, SWT.NONE);
		group.setText(Messages.SelectedTemplateGroup_Label);
		GridData gd_group = GridDataFactory.fillDefaults().grab(true, true).create();
		group.setLayoutData(gd_group);
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		Label templateLabel = new Label(group, SWT.NONE);
		templateLabel.setText(Messages.SelectedTemplateName_Label);
		templateLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.FILL).create());

		Label templateValue = new Label(group, SWT.WRAP);
		templateValue.setText(projectTemplate.getDisplayName());
		templateValue.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).create());

		Label descriptionLabel = new Label(group, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		descriptionLabel.setText(Messages.SelectedTemplateDesc_Label);

		Text descriptionValue = new Text(group, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.VERTICAL);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		descriptionValue.setText(projectTemplate.getDescription());

		// The description is wrapped, so we have to give hints for the dimensions
		Point computeSize = descriptionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		layoutData.widthHint = 1;
		layoutData.heightHint = computeSize.y * 4;

		descriptionValue.setLayoutData(layoutData);

	}
}
