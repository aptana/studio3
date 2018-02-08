/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jruby.embed.io.ReaderInputStream;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
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
		String fileExtension = template.getFiletype();
		// strips the leading * before . if there is one
		int index = fileExtension.lastIndexOf('.');
		if (index > -1)
		{
			fileExtension = fileExtension.substring(index + 1);
		}
		String name = template.getOwningBundle().getDisplayName();
		createUntitledFile(name, fileExtension,
				getInitialContents(template, Path.fromOSString(MessageFormat.format("{0}.{1}", name, fileExtension)))); //$NON-NLS-1$
	}

	@Override
	protected void createNewBlankFile(String editorType, String fileExtension)
	{
		createUntitledFile(editorType, fileExtension, new InputStream()
		{

			@Override
			public int read() throws IOException
			{
				return -1;
			}

			@Override
			public int available() throws IOException
			{
				return 0;
			}
		});
	}

	private IEditorPart createUntitledFile(String editorType, String fileExtension, InputStream initialContent)
	{
		FileOutputStream output = null;
		try
		{
			File file = File.createTempFile(Messages.NewUntitledFileTemplateMenuContributor_TempSuffix, "." //$NON-NLS-1$
					+ fileExtension);
			output = new FileOutputStream(file);
			IOUtil.pipe(initialContent, output);
			file.deleteOnExit();

			IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry()
					.getDefaultEditor(file.getName());
			String editorId = (editorDescriptor == null) ? ITextConstants.EDITOR_ID : editorDescriptor.getId();

			IWorkbenchPage page = UIUtils.getActivePage();
			if (page != null)
			{
				String editorName;
				Integer value = countByFileType.get(editorType);
				if (value == null)
				{
					editorName = MessageFormat.format(Messages.NewUntitledFileTemplateMenuContributor_DefaultName,
							editorType);
					countByFileType.put(editorType, 1);
				}
				else
				{
					editorName = MessageFormat.format(Messages.NewUntitledFileTemplateMenuContributor_DefaultName_2,
							editorType, value);
					countByFileType.put(editorType, ++value);
				}
				return page.openEditor(new UntitledFileStorageEditorInput(file.toURI(), editorName, initialContent),
						editorId);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(IOUIPlugin.getDefault(), "Failed to create new file from selected template", e); //$NON-NLS-1$
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					// ignores the exception
				}
			}
		}
		return null;
	}

	private InputStream getInitialContents(TemplateElement template, IPath path)
	{
		String templateContent = NewFileWizard.getTemplateContent(template, path);
		if (templateContent != null)
		{
			return new ReaderInputStream(new StringReader(templateContent), IOUtil.UTF_8);
		}
		return null;
	}
}
