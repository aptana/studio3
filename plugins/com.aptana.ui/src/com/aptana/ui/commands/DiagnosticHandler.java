/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.CorePlugin;
import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.diagnostic.IDiagnosticManager;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.dialogs.DiagnosticDialog;
import com.aptana.ui.util.UIUtils;

public class DiagnosticHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Job job = new Job("Getting Diagnostic Logs") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				final String content = getLogContent();
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						DiagnosticDialog dialog = new DiagnosticDialog(UIUtils.getActiveShell());
						dialog.open();
						dialog.setText(content);
					}
				});
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();

		return null;
	}

	public static String getLogContent()
	{
		StringBuilder content = new StringBuilder();
		List<IDiagnosticLog> logs = getDiagnosticManager().getLogs();
		String logText;
		for (IDiagnosticLog log : logs)
		{
			logText = log.getLog();
			if (!StringUtil.isEmpty(logText))
			{
				content.append(logText).append('\n');
			}
		}
		return content.toString();
	}

	protected static IDiagnosticManager getDiagnosticManager()
	{
		return CorePlugin.getDefault().getDiagnosticManager();
	}
}
