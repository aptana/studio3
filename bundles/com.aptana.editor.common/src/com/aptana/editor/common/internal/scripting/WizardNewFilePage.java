/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jruby.embed.io.ReaderInputStream;

import com.aptana.core.util.IOUtil;
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
		return isPageComplete() && templates != null && templates.length > 0;
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
		if (wizard.getContainer().getCurrentPage() == templateSelectionPage)
		{
			String templateContent = NewFileWizard.getTemplateContent(templateSelectionPage.getSelectedTemplate(),
					getContainerFullPath().append(getFileName()));
			if (templateContent != null)
			{
				return new ReaderInputStream(new StringReader(templateContent), IOUtil.UTF_8);
			}
		}
		return super.getInitialContents();
	}

	@Override
	protected boolean validatePage()
	{
		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length == 0)
		{
			setErrorMessage(Messages.WizardNewFilePage_ERR_NoProject);
			return false;
		}
		return super.validatePage();
	}

	private void collectTemplates()
	{
		final String filename = getFileName();
		List<CommandElement> commands = BundleManager.getInstance().getExecutableCommands(new IModelFilter()
		{

			public boolean include(AbstractElement element)
			{
				if (element instanceof TemplateElement)
				{
					TemplateElement te = (TemplateElement) element;
					String filetype = te.getFiletype();
					if (filetype == null)
						return false;
					filetype = filetype.replaceAll("\\.", "\\\\."); //$NON-NLS-1$ //$NON-NLS-2$
					filetype = filetype.replaceAll("\\*", ".*"); //$NON-NLS-1$ //$NON-NLS-2$
					filetype = filetype.replaceAll("\\?", "."); //$NON-NLS-1$ //$NON-NLS-2$
					filetype += "$"; //$NON-NLS-1$
					filetype = "^" + filetype; //$NON-NLS-1$
					return Pattern.matches(filetype, filename);
				}
				return false;
			}
		});
		if (commands != null && commands.size() > 0)
		{
			templates = new TemplateElement[commands.size()];
			for (int i = 0; i < commands.size(); i++)
			{
				templates[i] = (TemplateElement) commands.get(i);
			}
		}
		else
		{
			templates = new TemplateElement[0];
		}
	}
}
