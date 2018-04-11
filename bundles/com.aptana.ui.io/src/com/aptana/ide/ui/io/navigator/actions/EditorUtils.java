/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.EclipseUtil;
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
		openFileInEditor(fileStore, editorDescriptor, null);
	}

	/**
	 * Opens a remote file in its editor.
	 * 
	 * @param fileStore
	 * @param editorDescriptor
	 * @param textSelection
	 */
	public static void openFileInEditor(final IFileStore fileStore, final IEditorDescriptor editorDescriptor,
			final TextSelection textSelection)
	{
		Job job = new Job(MessageFormat.format(Messages.EditorUtils_OpeningEditor, fileStore.getName()))
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				final IEditorInput editorInput;
				try
				{
					editorInput = UniformFileStoreEditorInputFactory.getUniformEditorInput(fileStore, monitor);
				}
				catch (CoreException e)
				{
					UIUtils.showErrorMessage(
							MessageFormat.format(Messages.EditorUtils_OpeningEditor, fileStore.toString()), e);
					IdeLog.logError(IOUIPlugin.getDefault(),
							MessageFormat.format("Unable to open file {0}", fileStore.toString()), e); //$NON-NLS-1$
					return Status.CANCEL_STATUS;
				}

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
								IEditorPart editorPart;
								IFile file = findFileInWorkspace(fileStore);
								if (file == null)
								{
									// a non-workspace file
									String name;
									if (editorInput instanceof IUniformFileStoreEditorInput)
									{
										name = ((IUniformFileStoreEditorInput) editorInput).getFileStore().getName();
									}
									else
									{
										name = editorInput.getName();
									}
									String editorId = (editorDescriptor == null) ? IDE.getEditorDescriptor(name)
											.getId() : editorDescriptor.getId();
									int matchFlags = IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;
									editorPart = page.openEditor(editorInput, editorId, true, matchFlags);
								}
								else
								{
									// a workspace file
									editorPart = IDE.openEditor(page, file);
								}
								if (textSelection != null && editorPart instanceof AbstractTextEditor)
								{
									AbstractTextEditor editor = (AbstractTextEditor) editorPart;
									editor.selectAndReveal(textSelection.getOffset(), textSelection.getLength());
								}
							}
						}
						catch (Exception e)
						{
							return new Status(IStatus.ERROR, IOUIPlugin.PLUGIN_ID, null, e);
						}
						return Status.OK_STATUS;
					}
				};
				EclipseUtil.setSystemForJob(openEditor);
				openEditor.schedule();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private static IFile findFileInWorkspace(IFileStore fileStore)
	{
		if (fileStore == null)
		{
			return null;
		}
		IResource resource = (IResource) fileStore.getAdapter(IResource.class);
		if (resource instanceof IFile)
		{
			return (IFile) resource;
		}
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(fileStore.toURI());
		return ArrayUtil.isEmpty(files) ? null : files[0];
	}
}
