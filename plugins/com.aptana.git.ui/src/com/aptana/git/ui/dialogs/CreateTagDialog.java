/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRef;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;

/**
 * @author cwilliams
 */
public class CreateTagDialog extends InputDialog
{

	private GitRepository repo;
	private Combo startPointText;
	private Text messageText;
	private String startPoint;
	private String message;

	public CreateTagDialog(final Shell parentShell, final GitRepository repo)
	{
		super(parentShell, Messages.CreateTagDialog_Title, Messages.CreateTagDialog_Message, StringUtil.EMPTY,
				new IInputValidator()
				{

					public String isValid(String newText)
					{
						if (StringUtil.isEmpty(newText))
						{
							return Messages.CreateTagDialog_NonEmptyBranchNameMessage;
						}
						String trimmed = newText.trim();
						if (trimmed.contains(" ") || trimmed.contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
						{
							return Messages.CreateTagDialog_NoWhitespaceBranchNameMessage;
						}
						if (repo.tags().contains(trimmed))
						{
							return Messages.CreateTagDialog_BranchAlreadyExistsMessage;
						}
						if (!repo.validRefName(GitRef.REFS_TAGS + trimmed))
						{
							return Messages.CreateTagDialog_InvalidBranchNameMessage;
						}
						return null;
					}
				});
		this.repo = repo;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		// Add an advanced section so users can specify a non-HEAD SHA/ref tag point
		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.CreateTagDialog_Message_label);

		messageText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		messageText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(300, 100).create());
		messageText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				message = messageText.getText();
			}
		});

		// TODO Add a minimize/maximize button for the advanced section
		Group group = new Group(composite, SWT.DEFAULT);
		group.setText(Messages.CreateTagDialog_AdvancedOptions_label);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		label = new Label(group, SWT.NONE);
		label.setText(Messages.CreateTagDialog_StartPoint_label);

		startPointText = new Combo(group, getInputTextStyle() | SWT.DROP_DOWN);
		startPointText.setText(GitRepository.HEAD);
		startPointText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		startPointText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				startPoint = startPointText.getText();
				// In case they picked a commit, grab only the SHA (shortened)
				int index = startPoint.indexOf(' ');
				if (index != -1)
				{
					startPoint.substring(0, index);
				}
				// TODO Validate the start point. Must be branch name, commit id or tag ref. repo.validateRefName?
			}
		});

		Job job = new Job("Populating commit SHAs") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
				GitRevList revList = new GitRevList(repo);
				revList.walkRevisionListWithSpecifier(new GitRevSpecifier("."), subMonitor.newChild(95)); //$NON-NLS-1$
				final List<GitCommit> commits = revList.getCommits();
				Display.getDefault().asyncExec(new Runnable()
				{

					public void run()
					{
						// FIXME This is probably pretty nasty memory wise. Can we look up commits based on typing,
						// rather than pre-calculate?
						for (GitCommit commit : commits)
						{
							startPointText.add(commitMessage(commit));
						}

						SearchingContentProposalProvider proposalProvider = new SearchingContentProposalProvider(
								commits);
						ContentProposalAdapter adapter = new ContentProposalAdapter(startPointText,
								new ComboContentAdapter(), proposalProvider, KeyStroke.getInstance(SWT.CONTROL, ' '),
								null);
						adapter.setPropagateKeys(true);
						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

						ControlDecoration decoration = new ControlDecoration(startPointText, SWT.LEFT);
						decoration.setImage(FieldDecorationRegistry.getDefault()
								.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
					}
				});
				subMonitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.setPriority(Job.SHORT);
		job.schedule();

		return composite;
	}

	public String getStartPoint()
	{
		return startPoint;
	}

	public String getMessage()
	{
		return message;
	}

	protected static String commitMessage(GitCommit commit)
	{
		return MessageFormat.format("{0} {1}", commit.sha().substring(0, 8), commit.getSubject()); //$NON-NLS-1$
	}

	private static class SearchingContentProposalProvider implements IContentProposalProvider
	{
		private List<GitCommit> commits;

		private SearchingContentProposalProvider(List<GitCommit> commits)
		{
			this.commits = commits;
		}

		public IContentProposal[] getProposals(String contents, int position)
		{
			List<IContentProposal> list = new ArrayList<IContentProposal>();
			for (GitCommit commit : commits)
			{
				String msg = commitMessage(commit);
				if (msg.indexOf(contents) != -1)
				{
					list.add(new ContentProposal(msg));
				}
			}
			return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
		}

	}
}
