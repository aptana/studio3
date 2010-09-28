/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		// Escape periods in pattern (for regexp)
		pattern = pattern.replaceAll("\\.", "\\\\."); //$NON-NLS-1$ //$NON-NLS-2$
		// Replace * wildcard pattern with .+? regexp
		pattern = pattern.replaceAll("\\*", "\\.\\+\\?"); //$NON-NLS-1$ //$NON-NLS-2$
		CommandContext context = template.createCommandContext();
		context.put("TM_NEW_FILE_BASENAME", absoluteFilePath.removeFileExtension().lastSegment()); //$NON-NLS-1$
		context.put("TM_NEW_FILE", absoluteFilePath.toOSString()); //$NON-NLS-1$
		context.put("TM_NEW_FILE_DIRECTORY", absoluteFilePath.removeLastSegments(1).toOSString()); //$NON-NLS-1$
		CommandResult result = template.execute(context);
		return result.getOutputString();
	}
}
