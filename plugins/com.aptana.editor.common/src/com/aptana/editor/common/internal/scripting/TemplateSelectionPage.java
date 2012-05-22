/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.ui.util.UIUtils;

/**
 * Wizard page for selecting a file template when creating a new generic file. This wizard looks into the file extension
 * that the user typed in order to display the appropriate templates.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class TemplateSelectionPage extends WizardPage implements ISelectionChangedListener
{
	private static String TEMPLATE_IMAGE_PATH = "icons/template.png"; //$NON-NLS-1$
	private TemplateElement[] templates;
	private Button useTemplateBt;
	private Text templatePreview;
	private ScrolledComposite scroll;
	private TableViewer templateSelectionViewer;

	/**
	 * Constructor for TemplateSelectionPage.
	 */
	public TemplateSelectionPage(String pageName)
	{
		super(pageName);
		setTitle(Messages.TemplateSelectionPage_title);
		setDescription(Messages.TemplateSelectionPage_description);
		templates = new TemplateElement[0];
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(GridLayoutFactory.swtDefaults().spacing(10, 10).create());
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		createAbove(container, 1);
		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.TemplateSelectionPage_available_templates);
		GridData gd = new GridData();
		label.setLayoutData(gd);

		SashForm sashForm = new SashForm(container, SWT.VERTICAL);
		gd = new GridData(GridData.FILL_BOTH);
		// limit the width of the sash form to avoid the wizard
		// opening very wide. This is just preferred size -
		// it can be made bigger by the wizard
		// See bug #83356
		gd.widthHint = 300;
		sashForm.setLayoutData(gd);

		templateSelectionViewer = new TableViewer(sashForm, SWT.BORDER);
		templateSelectionViewer.setContentProvider(new ListContentProvider());
		templateSelectionViewer.setLabelProvider(new ListLabelProvider());
		createPreview(sashForm);
		initializeViewer();
		templateSelectionViewer.setInput(templates);
		templateSelectionViewer.addSelectionChangedListener(this);
		Dialog.applyDialogFont(container);
		setControl(container);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent
	 * )
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		if (!selection.isEmpty())
		{
			setPreviewContent(((TemplateElement) selection.getFirstElement()));
		}
		else
		{
			setPreviewContent(null);
		}
	}

	/**
	 * Returns the selected template. We return a non-null template only when there is a check on the 'use template'
	 * checkbox, the typed file has an extension, and there is a template for the file.
	 * 
	 * @return The selected template, or null if there are no templates or the user selected not to use a template.
	 */
	public TemplateElement getSelectedTemplate()
	{
		if (useTemplateBt.getSelection())
		{
			ISelection selection = templateSelectionViewer.getSelection();
			if (selection != null)
			{
				TemplateElement selected = (TemplateElement) ((StructuredSelection) selection).getFirstElement();
				if (selected != null)
				{
					return selected;
				}
				// we had an empty selection, probably because the user never got to this page.
				// load the templates and select the first by default.
				loadTemplates();
				return (templates != null && templates.length > 0) ? templates[0] : null;
			}
			else if (templates != null && templates.length > 0)
			{
				// just return the first
				return templates[0];
			}
		}
		return null;
	}

	protected void initializeViewer()
	{
		selectInitialTemplate();
	}

	protected void selectInitialTemplate()
	{
		// Select the first one on the list as the initial one
		if (templates.length > 0)
		{
			templateSelectionViewer.setSelection(new StructuredSelection(templates[0]), true);
			setPreviewContent(templates[0]);
		}
	}

	/**
	 * Creates a scrolled, plain-text, preview area for the template.
	 * 
	 * @param composite
	 *            Parent composite.
	 */
	protected void createPreview(Composite composite)
	{
		scroll = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scroll.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		scroll.setLayoutData(gd);
		templatePreview = new Text(scroll, SWT.MULTI | SWT.READ_ONLY);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		scroll.setContent(templatePreview);
	}

	/**
	 * Update the preview content with the given template.
	 * 
	 * @param template
	 */
	protected void setPreviewContent(TemplateElement template)
	{
		WizardNewFileCreationPage fileCreationPage = (WizardNewFileCreationPage) getPreviousPage();
		String templateContent = null;
		try
		{
			IPath path = fileCreationPage.getContainerFullPath().append(fileCreationPage.getFileName());
			templateContent = NewFileWizard.getTemplateContent(template, path);
		}
		catch (Exception e)
		{
			// logs the exception but allows the page to continue
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		if (templateContent == null)
		{
			templatePreview.setText(""); //$NON-NLS-1$
		}
		else
		{
			templatePreview.setText(templateContent);
		}
		scroll.setMinSize(templatePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	protected void createAbove(Composite container, int span)
	{
		useTemplateBt = new Button(container, SWT.CHECK);
		useTemplateBt.setText(Messages.TemplateSelectionPage_use_templates_button_text);
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		useTemplateBt.setLayoutData(gd);
		useTemplateBt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				updatePageControls();
			}
		});
		useTemplateBt.setSelection(true);
	}

	/*
	 * Load the templates by getting them from the main wizard page (WizardNewFilePage).
	 */
	private void loadTemplates()
	{
		WizardNewFilePage page = (WizardNewFilePage) getWizard().getPage(NewFileWizard.MAIN_PAGE_NAME);
		templates = page.getTemplates();
		updatePageControls();
	}

	// Update the template page messages and component enablement by the templates availability and the
	// Checkbox selection status.
	private void updatePageControls()
	{
		boolean componentsEnabled = useTemplateBt.getSelection();
		templateSelectionViewer.getControl().setEnabled(componentsEnabled);
		templatePreview.setEnabled(componentsEnabled);
		setMessage(null);
		if (componentsEnabled)
		{
			setDescription(Messages.TemplateSelectionPage_description);
		}
		else
		{
			setDescription("");//$NON-NLS-1$
		}
	}

	/**
	 * Content provider
	 */
	class ListContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object parent)
		{
			return templates;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	/**
	 * Label provider
	 */
	static class ListLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object obj, int index)
		{
			TemplateElement section = (TemplateElement) obj;
			if (index == 0)
				return section.getDisplayName();
			return section.getFiletype();
		}

		public Image getColumnImage(Object obj, int index)
		{
			return UIUtils.getImage(CommonEditorPlugin.getDefault(), TEMPLATE_IMAGE_PATH);
		}
	}

	/**
	 * Override the default setVisible to update the visible templates according to the file extension.
	 */
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			loadTemplates();
			templateSelectionViewer.setInput(templates);
			selectInitialTemplate();
		}
		super.setVisible(visible);
	}

	@Override
	public boolean isPageComplete()
	{
		// force load the templates
		WizardNewFilePage page = (WizardNewFilePage) getWizard().getPage(NewFileWizard.MAIN_PAGE_NAME);
		templates = page.getTemplates();
		// if no templates or one template, don't force this page to be shown
		if (templates == null)
			return true;
		if (templates.length < 2)
		{
			return true;
		}
		// Force this page to be shown if multiple templates and user hasn't yet selected one
		if (!useTemplateBt.getSelection())
			return true;
		ISelection selection = templateSelectionViewer.getSelection();
		if (selection == null)
			return false;
		TemplateElement selected = (TemplateElement) ((StructuredSelection) selection).getFirstElement();
		return selected != null;
	}
}
