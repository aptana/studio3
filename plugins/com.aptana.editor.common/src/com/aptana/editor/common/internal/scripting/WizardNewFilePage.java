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

import java.io.InputStream;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jruby.embed.io.ReaderInputStream;

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
		String templateContent = NewFileWizard.getTemplateContent(templateSelectionPage.getSelectedTemplate(), getContainerFullPath().append(getFileName()));
		if (templateContent != null)
		{
			return new ReaderInputStream(new StringReader(templateContent), "UTF-8"); //$NON-NLS-1$
		}
		return super.getInitialContents();
	}

	private void collectTemplates()
	{
		final String filename = getFileName();
		CommandElement[] commands = BundleManager.getInstance().getCommands(new IModelFilter()
		{
			@SuppressWarnings("nls")
			public boolean include(AbstractElement element)
			{
				if (element instanceof TemplateElement)
				{
					TemplateElement te = (TemplateElement) element;
					String filetype = te.getFiletype();
					if (filetype == null)
						return false;
					filetype = filetype.replaceAll("\\.", "\\\\.");
					filetype = filetype.replaceAll("\\*", ".*");
					filetype = filetype.replaceAll("\\?", ".");
					filetype += "$";
					filetype = "^" + filetype;
					return Pattern.matches(filetype, filename);
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
