/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.internal.formatter.CommonFormatterUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.TemplateElement;

@SuppressWarnings("restriction")
public class NewFileWizard extends BasicNewFileResourceWizard
{
	protected static final String TEMPLATE_PAGE_NAME = "templatePage";//$NON-NLS-1$
	protected static final String MAIN_PAGE_NAME = "mainPage";//$NON-NLS-1$

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

	@Override
	public boolean canFinish()
	{
		if (getContainer().getCurrentPage() == mainPage)
		{
			if (mainPage.isPageComplete())
			{
				return true;
			}
		}
		return super.canFinish();
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
	 * @param absoluteFilePath
	 * @return The output string that this template yield when executed. Null, in case the given template was null or
	 *         did not contain a file pattern.
	 */
	public static String getTemplateContent(TemplateElement template, IPath absoluteFilePath)
	{
		if (template == null)
		{
			return null;
		}
		String pattern = template.getFiletype();
		if (StringUtil.isEmpty(pattern))
		{
			return null;
		}
		CommandContext context = template.createCommandContext();
		context.put("TM_NEW_FILE_BASENAME", absoluteFilePath.removeFileExtension().lastSegment()); //$NON-NLS-1$
		context.put("TM_NEW_FILE", absoluteFilePath.toOSString()); //$NON-NLS-1$
		context.put("TM_NEW_FILE_DIRECTORY", absoluteFilePath.removeLastSegments(1).toOSString()); //$NON-NLS-1$
		CommandResult result = template.execute(context);
		if (result == null)
		{
			return null;
		}
		// Run the template's output through the current formatter and return its formatted output
		return CommonFormatterUtils.format(absoluteFilePath, result.getOutputString());
	}
}
