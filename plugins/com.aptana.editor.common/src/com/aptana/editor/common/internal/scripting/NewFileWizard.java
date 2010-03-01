package com.aptana.editor.common.internal.scripting;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

public class NewFileWizard extends BasicNewFileResourceWizard
{

	private WizardNewFileCreationPage mainPage;

	public NewFileWizard()
	{
		super();
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public void addPages()
	{
		mainPage = new WizardNewFilePage("newFilePage1", getSelection());//$NON-NLS-1$
		mainPage.setTitle(ResourceMessages.FileResource_pageTitle);
		mainPage.setDescription(ResourceMessages.FileResource_description);
		addPage(mainPage);
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

}
