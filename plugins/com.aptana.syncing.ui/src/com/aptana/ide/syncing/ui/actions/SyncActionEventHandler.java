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
	public void syncDone(VirtualFileSyncPair item)
	{
		super.syncDone(item);
		fSyncDoneCount++;
		checkDone();
	}

	@Override
	public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e)
	{
		showError(e.getLocalizedMessage(), e);
		fSyncDoneCount++;
		checkDone();
		return fContinue && super.syncErrorEvent(item, e);
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
