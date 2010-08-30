package com.aptana.projects.internal.wizards;

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

import com.aptana.scripting.model.ProjectTemplate;

public class ProjectTemplateSelectionPage extends WizardPage implements SelectionListener, ISelectionChangedListener
{

	private Button fUseTemplateButton;
	private TableViewer fTemplateSelectionViewer;
	private Text fPreviewText;

	private ProjectTemplate[] fTemplates;
	private ProjectTemplate fSelectedTemplate;

	public ProjectTemplateSelectionPage(String pageName, ProjectTemplate[] templates)
	{
		super(pageName);
		fTemplates = templates;
		setTitle(Messages.ProjectTemplateSelectionPage_Title);
		setDescription(Messages.ProjectTemplateSelectionPage_Description);
	}

	public ProjectTemplate getSelectedTemplate()
	{
		return fSelectedTemplate;
	}

	@Override
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

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fUseTemplateButton)
		{
			updatePageControls();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		// change the preview text according to the template selection
		fSelectedTemplate = null;
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		String text = null;
		if (!selection.isEmpty())
		{
			fSelectedTemplate = (ProjectTemplate) selection.getFirstElement();
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

		@Override
		public Object[] getElements(Object inputElement)
		{
			return fTemplates;
		}

		@Override
		public void dispose()
		{
		}

		@Override
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
			if (element instanceof ProjectTemplate)
			{
				ProjectTemplate template = (ProjectTemplate) element;
				return template.getName();
			}
			return super.getText(element);
		}
	}
}
