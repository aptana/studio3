/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.net.URL;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.projectTemplates.IProjectTemplate;
import com.aptana.projects.ProjectsPlugin;

public class ProjectTemplateSelectionPage extends WizardPage implements SelectionListener, ISelectionChangedListener
{

	private Button fUseTemplateButton;
	private TableViewer fTemplateSelectionViewer;
	private Label fPreviewText;

	private IProjectTemplate[] fTemplates;
	private IProjectTemplate fSelectedTemplate;

	private static ImageDescriptor wizardDesc = ProjectsPlugin.getImageDescriptor("/icons/protect_template_blank.png"); //$NON-NLS-1$
	private Image defaultTemplateImage = null;
	private Map<Object, Image> templateImages;

	public ProjectTemplateSelectionPage(String pageName, List<IProjectTemplate> templates)
	{
		super(pageName);

		if (templates == null)
		{
			fTemplates = new IProjectTemplate[0];
		}
		else
		{
			fTemplates = templates.toArray(new IProjectTemplate[templates.size()]);
		}
		setTitle(Messages.ProjectTemplateSelectionPage_Title);
		setDescription(Messages.ProjectTemplateSelectionPage_Description);
		templateImages = new HashMap<Object, Image>();
	}

	public IProjectTemplate getSelectedTemplate()
	{
		if (fUseTemplateButton.getSelection())
		{
			return fSelectedTemplate;
		}
		return null;
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
		main.setLayout(GridLayoutFactory.fillDefaults().spacing(5, 10).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		createTop(main);

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

		Dialog.applyDialogFont(main);
		setControl(main);
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fUseTemplateButton)
		{
			updatePageControls();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void selectionChanged(SelectionChangedEvent event)
	{
		// change the preview text according to the template selection
		fSelectedTemplate = null;
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		String text = null;
		if (!selection.isEmpty())
		{
			fSelectedTemplate = (IProjectTemplate) selection.getFirstElement();
			text = fSelectedTemplate.getDescription();
		}
		fPreviewText.setText(text == null ? "" : text); //$NON-NLS-1$
	}

	/**
	 * Creates an option to turn off using a template for the project.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createTop(Composite parent)
	{
		fUseTemplateButton = new Button(parent, SWT.CHECK);
		fUseTemplateButton.setText(Messages.ProjectTemplateSelectionPage_UseTemplate_TXT);
		fUseTemplateButton.setLayoutData(GridDataFactory.swtDefaults().create());
		fUseTemplateButton.addSelectionListener(this);
		fUseTemplateButton.setSelection(true);
	}

	/**
	 * Update the template page messages and component enablement by the checkbox selection status.
	 */
	private void updatePageControls()
	{
		boolean enabled = fUseTemplateButton.getSelection();
		fTemplateSelectionViewer.getControl().setEnabled(enabled);
		fPreviewText.setEnabled(enabled);
		setMessage(null);
		if (enabled)
		{
			setDescription(Messages.ProjectTemplateSelectionPage_Description);
		}
		else
		{
			setDescription(""); //$NON-NLS-1$
		}
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
					URL iconPath = template.getIconPath();
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
}
