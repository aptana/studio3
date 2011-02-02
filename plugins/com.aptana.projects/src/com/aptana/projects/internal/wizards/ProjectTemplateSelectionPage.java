/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.scripting.model.ProjectTemplateElement;

public class ProjectTemplateSelectionPage extends WizardPage implements SelectionListener, ISelectionChangedListener
{

	private Button fUseTemplateButton;
	private TableViewer fTemplateSelectionViewer;
	private Text fPreviewText;

	private ProjectTemplateElement[] fTemplates;
	private ProjectTemplateElement fSelectedTemplate;

	public ProjectTemplateSelectionPage(String pageName, List<ProjectTemplateElement> templates)
	{
		super(pageName);
		
		if (templates == null)
		{
			fTemplates = new ProjectTemplateElement[0];
		}
		else
		{
			fTemplates = templates.toArray(new ProjectTemplateElement[templates.size()]);
		}
		setTitle(Messages.ProjectTemplateSelectionPage_Title);
		setDescription(Messages.ProjectTemplateSelectionPage_Description);
	}

	public ProjectTemplateElement getSelectedTemplate()
	{
		if (fUseTemplateButton.getSelection())
		{
			return fSelectedTemplate;
		}
		return null;
	}

	public void createControl(Composite parent)
	{
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

		fPreviewText = new Text(sashForm, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);

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
			fSelectedTemplate = (ProjectTemplateElement) selection.getFirstElement();
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

		@Override
		public String getText(Object element)
		{
			if (element instanceof ProjectTemplateElement)
			{
				ProjectTemplateElement template = (ProjectTemplateElement) element;
				return template.getDisplayName();
			}
			return super.getText(element);
		}
	}
}
