/**
 * Aptana Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import java.text.MessageFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

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
	private Image templateIcon;
	private boolean displayingMore = false;

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
		GridData gd_group = GridDataFactory.fillDefaults().grab(true, false).create();
		group.setLayoutData(gd_group);
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		if (projectTemplate.getIconURL() != null)
		{
			Label templateLabel = new Label(group, SWT.NONE);
			templateIcon = ImageDescriptor.createFromURL(projectTemplate.getIconURL()).createImage();
			templateLabel.setImage(templateIcon);
			templateLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.BEGINNING).create());
		}

		final Composite labelComp = new Composite(group, SWT.NONE);
		labelComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		labelComp.setLayout(GridLayoutFactory.fillDefaults().create());

		Label templateValue = new Label(labelComp, SWT.WRAP);
		templateValue.setText(projectTemplate.getDisplayName());
		templateValue.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.create());

		String description = projectTemplate.getDescription();
		final Link descriptionValue = new Link(labelComp, SWT.WRAP);
		if (description.length() > 150)
		{
			displayingMore = true;
			int cutoffIndex = description.indexOf(".") + 1; //$NON-NLS-1$
			if (cutoffIndex > 150)
			{
				cutoffIndex = 150;
			}

			final int cutoff = cutoffIndex;
			description = MessageFormat.format(Messages.SelectedTemplateDesc_More_Label, projectTemplate
					.getDescription().substring(0, cutoff));
			descriptionValue.addSelectionListener(new SelectionListener()
			{

				public void widgetSelected(SelectionEvent e)
				{
					Rectangle bounds = descriptionValue.getBounds();
					int width = bounds.width;
					if (!displayingMore)
					{
						descriptionValue.setText(MessageFormat.format(Messages.SelectedTemplateDesc_More_Label,
								projectTemplate.getDescription().substring(0, cutoff)));
					}
					else
					{
						descriptionValue.setText(MessageFormat.format(Messages.SelectedTemplateDesc_Less_Label,
								projectTemplate.getDescription()));
					}

					displayingMore = !displayingMore;
					Point computeSize = descriptionValue.computeSize(width, SWT.DEFAULT);
					int diff = computeSize.y - bounds.height + 2;
					descriptionValue.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(computeSize)
							.create());
					layout(true, true);
					Point size = getShell().getSize();
					size.y += diff;
					getShell().setSize(size);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		}

		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1);
		descriptionValue.setText(description);
		//
		// // The description is wrapped, so we have to give hints for the dimensions
		Point computeSize = templateValue.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		layoutData.widthHint = 1;
		layoutData.heightHint = computeSize.y * 2;

		descriptionValue.setLayoutData(layoutData);

	}

	public void dispose()
	{
		if (templateIcon != null)
		{
			templateIcon.dispose();
		}
		super.dispose();
	}
}
