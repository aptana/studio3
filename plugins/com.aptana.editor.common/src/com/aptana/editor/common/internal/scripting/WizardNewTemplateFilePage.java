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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jruby.embed.io.ReaderInputStream;

import com.aptana.core.util.IOUtil;
import com.aptana.scripting.model.TemplateElement;

public class WizardNewTemplateFilePage extends WizardNewFileCreationPage
{

	private TemplateElement template;

	WizardNewTemplateFilePage(String pageName, IStructuredSelection selection, TemplateElement template)
	{
		super(pageName, selection);
		this.template = template;

		String filetype = template.getFiletype();
		// strips the leading * before . if there is one
		int index = filetype.lastIndexOf('.');
		if (index > -1)
		{
			filetype = filetype.substring(index);
		}
		setFileName("new_file" + filetype); //$NON-NLS-1$
	}

	@Override
	protected InputStream getInitialContents()
	{
		String templateContent = NewFileWizard.getTemplateContent(template, getContainerFullPath()
				.append(getFileName()));
		if (templateContent != null)
		{
			return new ReaderInputStream(new StringReader(templateContent), IOUtil.UTF_8);
		}
		return super.getInitialContents();
	}
}
