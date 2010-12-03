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
package com.aptana.ide.syncing.ui.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import com.aptana.core.CoreStrings;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.core.old.handlers.SyncEventHandlerAdapterWithProgressMonitor;
import com.aptana.ui.UIUtils;

class SyncActionEventHandler extends SyncEventHandlerAdapterWithProgressMonitor
{

	public static interface Client
	{
		public void syncCompleted();
	}

	private String fTaskTitle;
	private int fItemCount;
	private IProgressMonitor fMonitor;
	private Client fClient;

	private int fSyncDoneCount;
	private boolean fContinue;

	public SyncActionEventHandler(String taskTitle, int itemCount, IProgressMonitor monitor, Client client)
	{
		super(monitor);
		fTaskTitle = taskTitle;
		fItemCount = itemCount;
		fMonitor = monitor;
		fClient = client;
	}

	@Override
	public void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor)
	{
		super.syncDone(item, monitor);
		fSyncDoneCount++;
		checkDone();
	}

	@Override
	public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e, IProgressMonitor monitor)
	{
		showError(e.getLocalizedMessage(), e);
		fSyncDoneCount++;
		checkDone();
		return fContinue && super.syncErrorEvent(item, e, monitor);
	}

	@Override
	public boolean getFilesEvent(IConnectionPoint manager, String path)
	{
		fMonitor.subTask(MessageFormat.format("{0} {1}", fTaskTitle, path)); //$NON-NLS-1$
		return super.getFilesEvent(manager, path);
	}

	private void checkDone()
	{
		if (fSyncDoneCount == fItemCount && fClient != null)
		{
			fClient.syncCompleted();
		}
	}

	private void showError(final String message, final Exception e)
	{
		fContinue = false;
		UIUtils.getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				MessageDialog md = new MessageDialog(UIUtils.getActiveShell(), CoreStrings.ERROR, null, message,
						MessageDialog.WARNING, new String[] { CoreStrings.CONTINUE, CoreStrings.CANCEL }, 1);
				fContinue = (md.open() == 0);
			}
		});
	}
}
