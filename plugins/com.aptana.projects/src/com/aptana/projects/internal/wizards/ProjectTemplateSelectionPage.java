/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.wizards.IStepIndicatorWizardPage;
import com.aptana.ui.widgets.StepIndicatorComposite;

/**
 * Wizard page used to select a project template for new projects
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class ProjectTemplateSelectionPage extends WizardPage implements ISelectionChangedListener,
		IStepIndicatorWizardPage
{
	public static final String COMMAND_PROJECT_FROM_TEMPLATE_PROJECT_TEMPLATE_NAME = "projectTemplateId"; //$NON-NLS-1$
	public static final String COMMAND_PROJECT_FROM_TEMPLATE_NEW_WIZARD_ID = "newWizardId"; //$NON-NLS-1$

	private TableViewer fTemplateSelectionViewer;
	private Label fPreviewText;

	private IProjectTemplate[] fTemplates;
	private IProjectTemplate fSelectedTemplate;

	private static ImageDescriptor wizardDesc = ProjectsPlugin.getImageDescriptor("/icons/project_template_blank.png"); //$NON-NLS-1$
	private Image defaultTemplateImage = null;
	private Map<Object, Image> templateImages;

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
			// sorts the list by priority
			Collections.sort(templates, new Comparator<IProjectTemplate>()
			{

				public int compare(IProjectTemplate o1, IProjectTemplate o2)
				{
					return o1.getPriority() - o2.getPriority();
				}

			});
			fTemplates = templates.toArray(new IProjectTemplate[templates.size()]);
		}
		setTitle(Messages.ProjectTemplateSelectionPage_Title);
		setDescription(Messages.ProjectTemplateSelectionPage_Description);
		templateImages = new HashMap<Object, Image>();
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

		SashForm sashForm = new SashForm(main, SWT.HORIZONTAL);
		sashForm.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		fTemplateSelectionViewer = new TableViewer(sashForm, SWT.BORDER);
		fTemplateSelectionViewer.setContentProvider(new ListContentProvider());
		fTemplateSelectionViewer.setLabelProvider(new ListLabelProvider());
		fTemplateSelectionViewer.setInput(fTemplates);
		fTemplateSelectionViewer.addSelectionChangedListener(this);

		fPreviewText = new Label(sashForm, SWT.WRAP | SWT.READ_ONLY);

		// auto-selects the first one
		if (!ArrayUtil.isEmpty(fTemplates))
		{
			fTemplateSelectionViewer.setSelection(new StructuredSelection(fTemplates[0]));
			setSelectedTemplate(fTemplates[0]);
		}

		Dialog.applyDialogFont(main);
		setControl(main);
	}

	public void selectionChanged(SelectionChangedEvent event)
	{
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		setSelectedTemplate(selection.isEmpty() ? null : (IProjectTemplate) selection.getFirstElement());
	}

	private void setSelectedTemplate(IProjectTemplate template)
	{
		// change the preview text according to the template selection
		String text = (template == null) ? null : template.getDescription();
		fPreviewText.setText(text == null ? StringUtil.EMPTY : text);
		fSelectedTemplate = template;
	}

	/**
	 * The content provider for the templates table
	 */
	private class ListContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
		{
			return fTemplates;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	/**
	 * The label provider for the templates table
	 */
	private class ListLabelProvider extends LabelProvider
	{
		private static final int IMAGE_SIZE = 48;

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element)
		{
			Image image = templateImages.get(element);
			if (image == null)
			{
				if (element instanceof IProjectTemplate)
				{
					// Resolve and load the image
					IProjectTemplate template = (IProjectTemplate) element;
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
					templateImages.put(element, image);
				}
				else
				{
					image = defaultTemplateImage;
				}
			}
			return image;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			if (element instanceof IProjectTemplate)
			{
				IProjectTemplate template = (IProjectTemplate) element;
				return template.getDisplayName();
			}
			return super.getText(element);
		}
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
