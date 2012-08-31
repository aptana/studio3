/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.internal.wizards.Messages;
import com.aptana.projects.templates.IDefaultProjectTemplate;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.widgets.StepIndicatorComposite;

/**
 * Wizard page used to select a project template for new projects
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class ProjectTemplateSelectionPage extends WizardPage implements IStepIndicatorWizardPage
{
	public static final String COMMAND_PROJECT_FROM_TEMPLATE_PROJECT_TEMPLATE_NAME = "projectTemplateId"; //$NON-NLS-1$
	public static final String COMMAND_PROJECT_FROM_TEMPLATE_NEW_WIZARD_ID = "newWizardId"; //$NON-NLS-1$

	private static final int IMAGE_SIZE = 48;

	private Label previewImage;
	private Label previewLabel;
	private Label previewDescription;

	private IProjectTemplate[] fTemplates;
	private IProjectTemplate fSelectedTemplate;

	private static ImageDescriptor wizardDesc = ProjectsPlugin.getImageDescriptor("/icons/project_template_blank.png"); //$NON-NLS-1$
	private Image defaultTemplateImage = null;
	private Map<IProjectTemplate, Image> templateImages;
	private Map<Composite, IProjectTemplate> templateControlMap;

	protected StepIndicatorComposite stepIndicatorComposite;
	protected String[] stepNames;

	public ProjectTemplateSelectionPage(String pageName, List<IProjectTemplate> templates)
	{
		super(pageName);

		if (templates == null)
		{
			fTemplates = new IProjectTemplate[0];
		}
		else
		{
			// sorts the list by priority first and then alphabetically
			Collections.sort(templates, new Comparator<IProjectTemplate>()
			{

				public int compare(IProjectTemplate o1, IProjectTemplate o2)
				{
					int result = o1.getPriority() - o2.getPriority();
					return (result == 0) ? o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName()) : result;
				}
			});
			fTemplates = templates.toArray(new IProjectTemplate[templates.size()]);
		}
		setTitle(Messages.ProjectTemplateSelectionPage_Title);
		setDescription(Messages.ProjectTemplateSelectionPage_Description);
		templateImages = new HashMap<IProjectTemplate, Image>();
		templateControlMap = new LinkedHashMap<Composite, IProjectTemplate>();
	}

	public IProjectTemplate getSelectedTemplate()
	{
		return fSelectedTemplate;
	}

	public void createControl(Composite parent)
	{
		defaultTemplateImage = wizardDesc.createImage();
		parent.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				if (defaultTemplateImage != null)
				{
					defaultTemplateImage.dispose();
					defaultTemplateImage = null;
				}
				for (Image image : templateImages.values())
				{
					if (!image.isDisposed())
					{
						image.dispose();
					}
				}
				templateImages = null;
			}
		});

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 10).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		stepIndicatorComposite = new StepIndicatorComposite(main, stepNames);
		stepIndicatorComposite.setSelection(getStepName());

		Label label = new Label(main, SWT.NONE);
		label.setText(Messages.ProjectTemplateSelectionPage_AvailableTemplates_TXT);
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		Composite templateList = createTemplatesList(main);
		templateList.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// auto-selects the default template if there is one; otherwise selects the first one in the list
		if (!ArrayUtil.isEmpty(fTemplates))
		{
			boolean foundDefault = false;
			for (IProjectTemplate template : fTemplates)
			{
				if (template instanceof IDefaultProjectTemplate)
				{
					foundDefault = true;
					setSelectedTemplate(template);
					break;
				}
			}
			if (!foundDefault)
			{
				setSelectedTemplate(fTemplates[0]);
			}
		}

		Dialog.applyDialogFont(main);
		setControl(main);
	}

	private Composite createTemplatesList(Composite parent)
	{
		Composite main = new Composite(parent, SWT.BORDER);
		main.setLayout(GridLayoutFactory.fillDefaults().create());
		Color background = main.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		main.setBackground(background);

		Composite templatesList = new Composite(main, SWT.NONE);
		templatesList.setLayout(RowLayoutFactory.swtDefaults().extendedMargins(5, 5, 5, 5).spacing(10).fill(true)
				.create());
		templatesList.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(450, 250).create());
		templatesList.setBackground(background);

		for (IProjectTemplate template : fTemplates)
		{
			final Composite templateControl = new Composite(templatesList, SWT.NONE);
			templateControl.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 5, 5).create());
			templateControl.setLayoutData(RowDataFactory.swtDefaults().hint(95, SWT.DEFAULT).create());
			templateControl.setBackground(background);

			Label image = new Label(templateControl, SWT.CENTER);
			image.setImage(getImage(template));
			image.setBackground(background);
			image.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).create());
			Label text = new Label(templateControl, SWT.CENTER | SWT.WRAP);
			text.setText(template.getDisplayName());
			text.setBackground(background);
			text.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.BEGINNING)
					.create());

			MouseAdapter mouseAdapter = new MouseAdapter()
			{

				@Override
				public void mouseDown(MouseEvent e)
				{
					setSelectedTemplate(templateControlMap.get(templateControl));
				}
			};
			templateControl.addMouseListener(mouseAdapter);
			image.addMouseListener(mouseAdapter);
			text.addMouseListener(mouseAdapter);

			templateControlMap.put(templateControl, template);
		}

		Label separator = new Label(main, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite descriptionComp = createTemplateDescription(main);
		descriptionComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 110).create());

		return main;
	}

	protected Composite createTemplateDescription(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().extendedMargins(7, 0, 0, 0).numColumns(2).create());
		Color background = main.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		main.setBackground(background);

		previewImage = new Label(main, SWT.CENTER);
		previewImage.setBackground(background);
		previewImage.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
		previewLabel = new Label(main, SWT.LEFT);
		previewLabel.setBackground(background);
		previewLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());
		FontData[] fontData = SWTUtils.resizeFont(previewLabel.getFont(), 2);
		for (FontData data : fontData)
		{
			data.setStyle(data.getStyle() | SWT.BOLD);
		}
		final Font previewFont = new Font(previewLabel.getDisplay(), fontData);
		previewLabel.setFont(previewFont);
		previewLabel.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				previewFont.dispose();
			}
		});

		previewDescription = new Label(main, SWT.WRAP);
		previewDescription.setBackground(background);
		previewDescription.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());

		return main;
	}

	private void setSelectedTemplate(IProjectTemplate template)
	{
		// make the corresponding template control appear selected
		Set<Composite> templateControls = templateControlMap.keySet();
		for (Composite composite : templateControls)
		{
			Color background;
			if (templateControlMap.get(composite) == template)
			{
				background = composite.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
			}
			else
			{
				background = composite.getDisplay().getSystemColor(SWT.COLOR_WHITE);
			}
			composite.setBackground(background);
			Control[] children = composite.getChildren();
			for (Control childComp : children)
			{
				childComp.setBackground(background);
			}
		}
		// update the preview area according to the template selection
		previewImage.setImage(getImage(template));
		previewLabel.setText(template.getDisplayName());
		String text = (template == null) ? null : template.getDescription();
		previewDescription.setText(text == null ? StringUtil.EMPTY : text);
		fSelectedTemplate = template;
	}

	private Image getImage(IProjectTemplate template)
	{
		Image image = templateImages.get(template);
		if (image == null)
		{
			// Resolve and load the image
			URL iconPath = template.getIconURL();
			if (iconPath != null)
			{
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconPath);
				if (descriptor != null)
				{
					image = descriptor.createImage();
					if (image != null)
					{
						// Scale the image to 48x48 in case it's not.
						ImageData imageData = image.getImageData();
						if (imageData.x != IMAGE_SIZE || imageData.y != IMAGE_SIZE)
						{
							// dispose the previous one
							image.dispose();
							// Scale the image data and create a new image
							imageData = imageData.scaledTo(IMAGE_SIZE, IMAGE_SIZE);
							image = ImageDescriptor.createFromImageData(imageData).createImage();
						}
					}

				}
			}
			if (image == null)
			{
				image = defaultTemplateImage;
			}
			templateImages.put(template, image);
		}
		return image;
	}

	public void initStepIndicator(String[] stepNames)
	{
		this.stepNames = stepNames;
	}

	public String getStepName()
	{
		return getTitle();
	}
}
