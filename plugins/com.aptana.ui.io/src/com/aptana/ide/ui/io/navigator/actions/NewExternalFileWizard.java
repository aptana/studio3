/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.jruby.embed.io.ReaderInputStream;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.internal.scripting.NewFileWizard;
import com.aptana.editor.common.internal.scripting.TemplateSelectionPage;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.ui.util.UIUtils;

public class NewExternalFileWizard extends Wizard
{
	protected static final String TEMPLATE_PAGE_NAME = "templatePage";//$NON-NLS-1$
	protected static final String MAIN_PAGE_NAME = "mainPage";//$NON-NLS-1$

	private String initialFilename;
	private InputStream initialContent;
	private IAdaptable selectedElement;
	private TemplateElement template;

	private WizardNewExternalFilePage mainPage;

	public NewExternalFileWizard(String initialName, InputStream initialContent, IAdaptable selectedElement)
	{
		this(initialName, initialContent, selectedElement, null);
	}

	public NewExternalFileWizard(String initialName, InputStream initialContent, IAdaptable selectedElement,
			TemplateElement template)
	{
		initialFilename = initialName;
		this.initialContent = initialContent;
		this.selectedElement = selectedElement;
		this.template = template;
	}

	@Override
	public void addPages()
	{
		addPage(mainPage = new WizardNewExternalFilePage(MAIN_PAGE_NAME, initialFilename, template == null));
		mainPage.setTitle(Messages.NewExternalFileWizard_Title);
		mainPage.setDescription(Messages.NewExternalFileWizard_Description);

		if (initialContent == null && template == null)
		{
			addPage(new TemplateSelectionPage(TEMPLATE_PAGE_NAME));
		}
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

	@Override
	public boolean performFinish()
	{
		final IFileStore parentStore = getSelectedDirectory();
		final IFileStore newFile = parentStore.getChild(mainPage.getFileName());
		if (Utils.exists(newFile))
		{
			if (!MessageDialog.openConfirm(getShell(), Messages.NewFileAction_Confirm_Title,
					Messages.NewFileAction_Confirm_Message))
			{
				return false;
			}
		}

		final InputStream in = getInitialContents(Path.fromOSString(newFile.toString()));
		// run the file creation in a job
		Job job = new Job(Messages.NewFileAction_JobTitle)
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					OutputStream out = newFile.openOutputStream(EFS.NONE, monitor);
					if (in != null)
					{
						// creates the initial contents
						try
						{
							IOUtil.pipe(in, out);
						}
						catch (IOException e)
						{
							IdeLog.logError(IOUIPlugin.getDefault(), e);
						}
						finally
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
								IdeLog.logError(IOUIPlugin.getDefault(), e);
							}
						}
					}
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						IdeLog.logError(IOUIPlugin.getDefault(), e);
					}

					// opens it in the editor
					EditorUtils.openFileInEditor(newFile, null);

					// refreshes the parent folder
					final IFileStore fileStore = Utils.getFileStore(selectedElement);
					boolean selectionIsDirectory = Utils.isDirectory(selectedElement);
					if (selectionIsDirectory)
					{
						IOUIPlugin.refreshNavigatorView(selectedElement);
					}
					else
					{
						IOUIPlugin.refreshNavigatorView(fileStore.getParent());
					}
				}
				catch (CoreException e)
				{
					showError(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}

	private IFileStore getSelectedDirectory()
	{
		IFileStore fileStore = Utils.getFileStore(selectedElement);
		boolean selectionIsDirectory = Utils.isDirectory(selectedElement);
		if (!selectionIsDirectory && fileStore.getParent() != null)
		{
			return fileStore.getParent();
		}
		return fileStore;
	}

	private InputStream getInitialContents(IPath path)
	{
		if (initialContent != null)
		{
			return initialContent;
		}
		if (template != null)
		{
			String templateContent = NewFileWizard.getTemplateContent(template, path);
			if (templateContent != null)
			{
				return new ReaderInputStream(new StringReader(templateContent), IOUtil.UTF_8);
			}
		}
		return mainPage.getInitialContents();
	}

	private void showError(Exception exception)
	{
		UIUtils.showErrorMessage(exception.getLocalizedMessage(), exception);
	}
}
