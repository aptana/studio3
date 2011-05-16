/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.io.efs.SyncUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.internal.UniformFileStoreEditorInput;
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
				try {
					editorInput = UniformFileStoreEditorInputFactory.getUniformEditorInput(fileStore, monitor);
				} catch (CoreException e) {
					UIUtils.showErrorMessage(MessageFormat.format(Messages.EditorUtils_OpeningEditor, fileStore.toString()), e);
					return Status.CANCEL_STATUS;
				}
				final IEditorInput finalEditorInput = editorInput;

				UIJob openEditor = new UIJob(MessageFormat.format(Messages.EditorUtils_OpeningEditor, fileStore.toString())) {

					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						try
						{
							IWorkbenchPage page = UIUtils.getActivePage();
							IEditorPart editorPart = null;
							if (page != null)
							{
								String editorId = editorDescriptor == null ? IDE.getEditorDescriptor(
										finalEditorInput.getName()).getId() : editorDescriptor.getId();
								int matchFlags = IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;
								boolean opened = page.findEditors(finalEditorInput, editorId, matchFlags).length > 0;

								editorPart = page.openEditor(finalEditorInput, editorId, true, matchFlags);
								if (!opened && editorPart != null)
								{
									attachSaveListener(editorPart);
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
				openEditor.setSystem(true);
				openEditor.schedule();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * Watches the local file for changes and saves it back to the original remote file when the editor is saved.
	 * 
	 * @param editorPart
	 *            the editor part the file is opened on
	 */
	public static void attachSaveListener(final IEditorPart editorPart)
	{
		final IEditorInput editorInput = editorPart.getEditorInput();
		if (!(editorInput instanceof UniformFileStoreEditorInput)
				|| !((UniformFileStoreEditorInput) editorInput).isRemote())
		{
			// the original is a local file; no need to re-save it
			return;
		}

		editorPart.addPropertyListener(new IPropertyListener()
		{

			public void propertyChanged(Object source, int propId)
			{
				if (propId == EditorPart.PROP_DIRTY && source instanceof EditorPart)
				{
					EditorPart ed = (EditorPart) source;

					if (ed.isDirty())
					{
						return;
					}
					Job job = new Job(Messages.EditorUtils_MSG_RemotelySaving + ed.getPartName())
					{

						protected IStatus run(IProgressMonitor monitor)
						{
							UniformFileStoreEditorInput input = (UniformFileStoreEditorInput) editorInput;
							IFileStore localCacheFile = input.getLocalFileStore();
							IFileStore originalFile = input.getFileStore();
							IFileInfo originalFileInfo = input.getFileInfo();
							try
							{
								IFileInfo currentFileInfo = originalFile.fetchInfo(EFS.NONE, monitor);
								if (currentFileInfo.exists()
										&& (currentFileInfo.getLastModified() != originalFileInfo.getLastModified() || currentFileInfo
												.getLength() != originalFileInfo.getLength()))
								{
									if (!UIUtils.showPromptDialog(Messages.EditorUtils_OverwritePrompt_Title,
											MessageFormat.format(Messages.EditorUtils_OverwritePrompt_Message,
													originalFile.getName())))
									{
										return Status.CANCEL_STATUS;
									}
								}
								SyncUtils.copy(localCacheFile, null, originalFile, EFS.NONE, monitor);
							}
							catch (CoreException e)
							{
								UIUtils.showErrorMessage(
										MessageFormat.format(Messages.EditorUtils_ERR_SavingRemoteFile,
												originalFile.getName()), e);
							}
							finally
							{
								// update cached remote file info
								try
								{
									input.setFileInfo(originalFile.fetchInfo(EFS.NONE, monitor));
								}
								catch (CoreException e)
								{
									IOUIPlugin.logError(e);
								}
							}
							return Status.OK_STATUS;
						}
					};
					job.schedule();
				}
			}
		});
	}
}
