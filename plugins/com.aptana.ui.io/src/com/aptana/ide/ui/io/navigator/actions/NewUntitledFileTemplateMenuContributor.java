/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jruby.embed.io.ReaderInputStream;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.UntitledFileStorageEditorInput;
import com.aptana.editor.common.internal.scripting.NewFileWizard;
import com.aptana.editor.text.ITextConstants;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.ui.util.UIUtils;

public class NewUntitledFileTemplateMenuContributor extends NewFileTemplateMenuContributor
{

	private Map<String, Integer> countByFileType = new HashMap<String, Integer>();

	public NewUntitledFileTemplateMenuContributor()
	{
	}

	public NewUntitledFileTemplateMenuContributor(String id)
	{
		super(id);
	}

	@Override
	protected void createNewFileFromTemplate(TemplateElement template)
	{
		// creates untitled file
		String filetype = template.getFiletype();
		// strips the leading * before . if there is one
		int index = filetype.lastIndexOf("."); //$NON-NLS-1$
		if (index > -1)
		{
			filetype = filetype.substring(index);
		}
		try
		{
			File file = File.createTempFile(Messages.NewUntitledFileTemplateMenuContributor_TempSuffix, filetype);
			file.deleteOnExit();
			IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry()
					.getDefaultEditor(file.getName());
			String editorId = (editorDescriptor == null) ? ITextConstants.EDITOR_ID : editorDescriptor.getId();

			IWorkbenchPage page = UIUtils.getActivePage();
			if (page != null)
			{
				String bundleName = template.getOwningBundle().getDisplayName();
				String editorName;
				Integer value = countByFileType.get(bundleName);
				if (value == null)
				{
					editorName = MessageFormat.format(Messages.NewUntitledFileTemplateMenuContributor_DefaultName,
							bundleName);
					countByFileType.put(bundleName, 1);
				}
				else
				{
					editorName = MessageFormat.format(Messages.NewUntitledFileTemplateMenuContributor_DefaultName_2,
							bundleName, value);
					countByFileType.put(bundleName, ++value);
				}
				page.openEditor(
						new UntitledFileStorageEditorInput(editorName, getInitialContents(template,
								Path.fromOSString(file.getAbsolutePath()))), editorId);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(IOUIPlugin.getDefault(), "Failed to create new file from selected template", e); //$NON-NLS-1$
		}
	}

	private InputStream getInitialContents(TemplateElement template, IPath path)
	{
		String templateContent = NewFileWizard.getTemplateContent(template, path);
		if (templateContent != null)
		{
			return new ReaderInputStream(new StringReader(templateContent), "UTF-8"); //$NON-NLS-1$
		}
		return null;
	}
}
