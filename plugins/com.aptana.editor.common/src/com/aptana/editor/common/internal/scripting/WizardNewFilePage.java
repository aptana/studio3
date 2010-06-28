package com.aptana.editor.common.internal.scripting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.model.filters.IModelFilter;

public class WizardNewFilePage extends WizardNewFileCreationPage
{

	private TemplateElement[] templates;

	public WizardNewFilePage(String pageName, IStructuredSelection selection)
	{
		super(pageName, selection);
	}

	@Override
	public void handleEvent(Event event)
	{
		// Hook into the event handling to get the typed file name.
		// In case we have a template for that, the canFlipToNextPage method call will show the Next button
		if (event.type == SWT.Modify)
		{
			collectTemplates();
		}
		super.handleEvent(event);
	}

	/**
	 * Returns the templates that are associated with the typed file extension.
	 * 
	 * @return TemplateElement array.
	 */
	public TemplateElement[] getTemplates()
	{
		return templates;
	}

	@Override
	public boolean canFlipToNextPage()
	{
		return templates != null && templates.length > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents()
	{
		IWizard wizard = getWizard();
		TemplateSelectionPage templateSelectionPage = (TemplateSelectionPage) wizard
				.getPage(NewFileWizard.TEMPLATE_PAGE_NAME);
		String templateContent = NewFileWizard.getTemplateContent(templateSelectionPage.getSelectedTemplate());
		if (templateContent != null)
		{
			return new ByteArrayInputStream(templateContent.getBytes());
		}
		return super.getInitialContents();
	}

	private void collectTemplates()
	{
		IPath path = Path.fromOSString(getFileName());
		String extension = path.getFileExtension();
		final String fileExtension = extension != null ? ("*." + extension) : null; //$NON-NLS-1$
		CommandElement[] commands = BundleManager.getInstance().getCommands(new IModelFilter()
		{
			public boolean include(AbstractElement element)
			{
				if (element instanceof TemplateElement)
				{
					TemplateElement te = (TemplateElement) element;
					String filetype = te.getFiletype();
					return filetype != null && filetype.equals(fileExtension);
				}
				return false;
			}
		});
		if (commands != null && commands.length > 0)
		{
			templates = new TemplateElement[commands.length];
			for (int i = 0; i < commands.length; i++)
			{
				templates[i] = (TemplateElement) commands[i];
			}
		}
		else
		{
			templates = new TemplateElement[0];
		}
	}
}
