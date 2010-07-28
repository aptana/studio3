package com.aptana.editor.common.internal.scripting;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.TemplateElement;

@SuppressWarnings("restriction")
public class NewFileWizard extends BasicNewFileResourceWizard
{
	protected static String TEMPLATE_PAGE_NAME = "templatePage";//$NON-NLS-1$
	protected static String MAIN_PAGE_NAME = "mainPage";//$NON-NLS-1$

	private WizardNewFilePage mainPage;
	private TemplateSelectionPage templateSelectionPage;

	public NewFileWizard()
	{
		super();
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public void addPages()
	{
		mainPage = new WizardNewFilePage(MAIN_PAGE_NAME, getSelection());
		mainPage.setTitle(ResourceMessages.FileResource_pageTitle);
		mainPage.setDescription(ResourceMessages.FileResource_description);

		templateSelectionPage = new TemplateSelectionPage(TEMPLATE_PAGE_NAME);

		addPage(mainPage);
		addPage(templateSelectionPage);
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public boolean performFinish()
	{
		IFile file = mainPage.createNewFile();
		if (file == null)
		{
			return false;
		}

		selectAndReveal(file);

		// Open editor on new file.
		IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
		try
		{
			if (dw != null)
			{
				IWorkbenchPage page = dw.getActivePage();
				if (page != null)
				{
					IDE.openEditor(page, file, true);
				}
			}
		}
		catch (PartInitException e)
		{
			DialogUtil.openError(dw.getShell(), ResourceMessages.FileResource_errorMessage, e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Execute the given template and return the output-string result of that execution.
	 * 
	 * @param template
	 *            A {@link TemplateElement}
	 * @return The output string that this template yield when executed. Null, in case the given template was null or
	 *         did not contain a file pattern.
	 */
	public static String getTemplateContent(TemplateElement template)
	{
		if (template == null)
		{
			return null;
		}
		String pattern = template.getFiletype();
		if (pattern.isEmpty())
		{
			return null;
		}
		// Escape periods in pattern (for regexp)
		pattern = pattern.replaceAll("\\.", "\\\\."); //$NON-NLS-1$ //$NON-NLS-2$
		// Replace * wildcard pattern with .+? regexp
		pattern = pattern.replaceAll("\\*", "\\.\\+\\?"); //$NON-NLS-1$ //$NON-NLS-2$
		CommandContext context = template.createCommandContext();
		CommandResult result = template.execute(context);
		// FIXME When I use vi on the resulting files, it complains of no eol. Somehow fix that here?
		return result.getOutputString().trim();
	}
}
