/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

import com.aptana.scripting.model.TemplateElement;

@SuppressWarnings("restriction")
public class NewTemplateFileWizard extends BasicNewFileResourceWizard
{

	private static final String MAIN_PAGE_NAME = "mainPage";//$NON-NLS-1$

	private TemplateElement template;
	private WizardNewTemplateFilePage mainPage;

	public NewTemplateFileWizard(TemplateElement template)
	{
		this.template = template;
	}

	@Override
	public void addPages()
	{
		mainPage = new WizardNewTemplateFilePage(MAIN_PAGE_NAME, getSelection(), template);
		mainPage.setTitle(ResourceMessages.FileResource_pageTitle);
		mainPage.setDescription(ResourceMessages.FileResource_description);
		addPage(mainPage);
	}

	@Override
	public boolean performFinish()
	{
		IFile file = mainPage.createNewFile();
		if (file == null)
		{
			return false;
		}

		selectAndReveal(file);

		// opens editor on the new file
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
		try
		{
			if (window != null)
			{
				IWorkbenchPage page = window.getActivePage();
				if (page != null)
				{
					IDE.openEditor(page, file, true);
				}
			}
		}
		catch (PartInitException e)
		{
			DialogUtil.openError(window.getShell(), ResourceMessages.FileResource_errorMessage, e.getMessage(), e);
		}

		return true;
	}
}
