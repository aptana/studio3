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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.CoreStrings;
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
	 * @param file
	 *            the file store of the remote file
	 * @param editorDesc
	 */
	public static void openFileInEditor(final IFileStore fileStore)
	{
		Job job = new Job(Messages.EditorUtils_MSG_OpeningRemoteFile + fileStore.getName())
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				final IEditorInput editorInput = UniformFileStoreEditorInputFactory.getUniformEditorInput(fileStore,
						monitor);

				if (editorInput == null)
				{
					UIUtils.showErrorMessage(CoreStrings.ERROR,
							MessageFormat.format(Messages.EditorUtils_ERR_OpeningEditor, fileStore.toString()));
				}
				else
				{
					UIJob openEditor = new UIJob("Opening editor") { //$NON-NLS-1$

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							try
							{
								IWorkbenchPage page = IOUIPlugin.getActivePage();
								IEditorPart editorPart = null;
								if (page != null)
								{
									boolean opened = (page.findEditor(editorInput) != null);

									editorPart = page.openEditor(editorInput,
											IDE.getEditorDescriptor(editorInput.getName()).getId());
									if (!opened && editorPart != null)
									{
										attachSaveListener(editorPart);
									}
								}
							}
							catch (Exception e)
							{
								UIUtils.showErrorMessage(
										MessageFormat.format(Messages.EditorUtils_ERR_OpeningEditor,
												fileStore.toString()), e);
							}
							return Status.OK_STATUS;
						}
					};
					openEditor.setSystem(true);
					openEditor.schedule();
				}

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
								if (currentFileInfo.getLastModified() != originalFileInfo.getLastModified()
										|| currentFileInfo.getLength() != originalFileInfo.getLength())
								{
									if (!UIUtils.showPromptDialog(Messages.EditorUtils_OverwritePrompt_Title,
											MessageFormat.format(Messages.EditorUtils_OverwritePrompt_Message,
													originalFile.getName())))
									{
										return Status.CANCEL_STATUS;
									}
								}
								localCacheFile.copy(originalFile, EFS.OVERWRITE, monitor);
								// update cached remote file info
								input.setFileInfo(originalFile.fetchInfo(EFS.NONE, monitor));
							}
							catch (CoreException e)
							{
								UIUtils.showErrorMessage(
										MessageFormat.format(Messages.EditorUtils_ERR_SavingRemoteFile,
												originalFile.getName()), e);
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
