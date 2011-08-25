/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.editors;

import java.text.MessageFormat;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class EditorUtils
{

	/**
	 * Opens the connection editor on a specific site connection.
	 * 
	 * @param site
	 *            the connection
	 */
	public static void openConnectionEditor(final ISiteConnection site)
	{
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null)
				{
					IWorkbenchPage page = window.getActivePage();
					try
					{
						IEditorPart editorPart = page.openEditor(new ConnectionEditorInput(site), ConnectionEditor.ID);
						if (editorPart instanceof ConnectionEditor)
						{
							// in case the site information has changed
							((ConnectionEditor) editorPart).setSelectedSite(site);
						}
					}
					catch (PartInitException e)
					{
						IdeLog.logError(SyncingUIPlugin.getDefault(),
								MessageFormat.format(Messages.EditorUtils_FailedToOpenEditor, site.getName()), e);
					}
				}
			}
		});
	}

	/**
	 * Closes the connection editor corresponding to the specific site connection.
	 * 
	 * @param site
	 *            the connection
	 */
	public static void closeConnectionEditor(final ISiteConnection site)
	{
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null)
				{
					IWorkbenchPage page = window.getActivePage();
					IEditorPart editor = page.findEditor(new ConnectionEditorInput(site));
					if (editor != null)
					{
						page.closeEditor(editor, false);
					}
				}
			}
		});
	}
}
