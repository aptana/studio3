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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jruby.embed.io.ReaderInputStream;

import com.aptana.scripting.model.TemplateElement;

public class WizardNewTemplateFilePage extends WizardNewFileCreationPage
{

	private TemplateElement template;

	public WizardNewTemplateFilePage(String pageName, IStructuredSelection selection, TemplateElement template)
	{
		super(pageName, selection);
		this.template = template;

		String filetype = template.getFiletype();
		// strips the leading * before . if there is one
		int index = filetype.lastIndexOf("."); //$NON-NLS-1$
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
			return new ReaderInputStream(new StringReader(templateContent), "UTF-8"); //$NON-NLS-1$
		}
		return super.getInitialContents();
	}
}
