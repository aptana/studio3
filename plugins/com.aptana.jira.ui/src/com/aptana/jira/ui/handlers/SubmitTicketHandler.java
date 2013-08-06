/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.ui.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.jira.core.JiraCorePlugin;
import com.aptana.jira.core.JiraException;
import com.aptana.jira.core.JiraIssue;
import com.aptana.jira.core.JiraIssueSeverity;
import com.aptana.jira.core.JiraIssueType;
import com.aptana.jira.core.JiraManager;
import com.aptana.jira.ui.internal.SubmitTicketDialog;
import com.aptana.ui.commands.DiagnosticHandler;
import com.aptana.ui.dialogs.HyperlinkMessageDialog;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class SubmitTicketHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		SubmitTicketDialog dialog = new SubmitTicketDialog(UIUtils.getActiveShell());
		if (dialog.open() == Window.OK)
		{
			// runs in a job since it could be long-running
			final JiraIssueType type = dialog.getType();
			final JiraIssueSeverity severity = dialog.getSeverity();
			final String summary = dialog.getSummary();
			final String description = dialog.getDescription();
			final boolean studioLogSelected = dialog.getStudioLogSelected();
			final boolean diagnosticLogSelected = dialog.getDiagnosticLogSelected();
			final Set<IPath> screenshots = dialog.getScreenshots();

			Job job = new Job(StringUtil.ellipsify(Messages.SubmitTicketHandler_JobTitle))
			{

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					JiraManager manager = getJiraManager();
					JiraIssue issue = null;
					try
					{
						// Replace any Windows new-line ending with a \n.
						String d = description.replaceAll("(\r\n|\n)", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
						issue = manager.createIssue(type, severity, summary, d);
					}
					catch (final JiraException e)
					{
						// shows an error message
						UIUtils.getDisplay().asyncExec(new Runnable()
						{

							public void run()
							{
								// using workbench window's shell instead of UIUtils.getActiveShell() since the latter
								// could be the shell of job's progress dialog and would cause this dialog to close
								// automatically when job finishes
								MessageDialog.openError(UIUtils.getActiveWorkbenchWindow().getShell(),
										Messages.SubmitTicketHandler_ERR_CreateFailed, e.getMessage());
							}
						});
					}
					catch (final IOException e)
					{
						// shows an error message. In this case likely to be because we couldn't create the temp file
						// for the description
						UIUtils.getDisplay().asyncExec(new Runnable()
						{

							public void run()
							{
								// using workbench window's shell instead of UIUtils.getActiveShell() since the latter
								// could be the shell of job's progress dialog and would cause this dialog to close
								// automatically when job finishes
								MessageDialog.openError(UIUtils.getActiveWorkbenchWindow().getShell(),
										Messages.SubmitTicketHandler_ERR_CreateFailed, e.getMessage());
							}
						});
					}

					if (issue != null)
					{
						// issue is successfully created; now adds the attachments
						try
						{
							if (studioLogSelected)
							{
								String logFile = System.getProperty("osgi.logfile"); //$NON-NLS-1$
								File file = new File(logFile);
								if (file.exists())
								{
									manager.addAttachment(Path.fromOSString(file.getAbsolutePath()), issue);
								}
							}
							if (diagnosticLogSelected)
							{
								String logContent = DiagnosticHandler.getLogContent();
								try
								{
									File file = File.createTempFile("diagnostic", ".log"); //$NON-NLS-1$ //$NON-NLS-2$
									file.deleteOnExit();
									IOUtil.write(new FileOutputStream(file), logContent);
									manager.addAttachment(Path.fromOSString(file.getAbsolutePath()), issue);
								}
								catch (IOException e)
								{
									IdeLog.logWarning(JiraCorePlugin.getDefault(), e);
								}
							}
							for (IPath screenshot : screenshots)
							{
								manager.addAttachment(screenshot, issue);
							}
						}
						catch (JiraException e)
						{
							IdeLog.logWarning(JiraCorePlugin.getDefault(), e);
						}
						// shows a success message
						showSuccess(issue);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}

		return null;
	}

	protected JiraManager getJiraManager()
	{
		return JiraCorePlugin.getDefault().getJiraManager();
	}

	private static void showSuccess(final JiraIssue issue)
	{
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				// using workbench window's shell instead of UIUtils.getActiveShell() for the same reason above
				HyperlinkMessageDialog.openInformation(UIUtils.getActiveWorkbenchWindow().getShell(),
						Messages.SubmitTicketHandler_Success_Title,
						MessageFormat.format(Messages.SubmitTicketHandler_Success_Message, issue.getUrl()));
			}
		});
	}
}
