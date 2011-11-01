/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.IUniformFileStoreEditorInput;
import com.aptana.ide.ui.io.internal.UniformFileStoreEditorInputFactory;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class EditorUtils
{

	/**
	 * Opens a remote file in its editor.
	 * 
	 * @param fileStore
	 *            the file store of the remote file
	 * @param editorDescriptor
	 *            the editor descriptor to use, null if the default one for the file type is desired
	 */
	public static void openFileInEditor(final IFileStore fileStore, final IEditorDescriptor editorDescriptor)
	{
		Job job = new Job(MessageFormat.format(Messages.EditorUtils_OpeningEditor, fileStore.getName()))
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				IEditorInput editorInput;
				try
				{
					editorInput = UniformFileStoreEditorInputFactory.getUniformEditorInput(fileStore, monitor);
				}
				catch (CoreException e)
				{
					UIUtils.showErrorMessage(
							MessageFormat.format(Messages.EditorUtils_OpeningEditor, fileStore.toString()), e);
					return Status.CANCEL_STATUS;
				}
				final IEditorInput finalEditorInput = editorInput;

				UIJob openEditor = new UIJob(MessageFormat.format(Messages.EditorUtils_OpeningEditor,
						fileStore.toString()))
				{

					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						try
						{
							IWorkbenchPage page = UIUtils.getActivePage();
							if (page != null)
							{
								String name;
								if (finalEditorInput instanceof IUniformFileStoreEditorInput)
								{
									name = ((IUniformFileStoreEditorInput) finalEditorInput).getFileStore().getName();
								}
								else
								{
									name = finalEditorInput.getName();
								}
								String editorId = (editorDescriptor == null) ? IDE.getEditorDescriptor(name).getId()
										: editorDescriptor.getId();
								int matchFlags = IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;
								page.openEditor(finalEditorInput, editorId, true, matchFlags);
							}
						}
						catch (Exception e)
						{
							return new Status(IStatus.ERROR, IOUIPlugin.PLUGIN_ID, null, e);
						}
						return Status.OK_STATUS;
					}
				};
				openEditor.setSystem(true);
				openEditor.schedule();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
