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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.StatusDialog;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRef;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;

/**
 * @author cwilliams
 */
public class CreateTagDialog extends StatusDialog
{

	private GitRepository repo;
	private Combo startPointText;
	private Text messageText;
	private String startPoint;
	private String message;
	private Text tagNameText;
	private String tagName;
	private List<GitCommit> commits;

	public CreateTagDialog(final Shell parentShell, final GitRepository repo, final List<GitCommit> commits)
	{
		super(parentShell);
		setTitle(Messages.CreateTagDialog_Title);
		this.repo = repo;
		this.commits = commits;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label tagNameLabel = new Label(composite, SWT.NONE);
		tagNameLabel.setText(Messages.CreateTagDialog_Message);

		tagNameText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tagNameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(300, 100).create());
		tagNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				tagName = tagNameText.getText();
				validate();
			}
		});

		Label tagMessageLabel = new Label(composite, SWT.NONE);
		tagMessageLabel.setText(Messages.CreateTagDialog_Message_label);

		messageText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		messageText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(300, 100).create());
		messageText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				message = messageText.getText();
				validate();
			}
		});

		// TODO Add a minimize/maximize button for the advanced section
		Group group = new Group(composite, SWT.DEFAULT);
		group.setText(Messages.CreateTagDialog_AdvancedOptions_label);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Label tagRevLabel = new Label(group, SWT.NONE);
		tagRevLabel.setText(Messages.CreateTagDialog_StartPoint_label);

		startPointText = new Combo(group, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
		startPointText.setText(startPoint = GitRepository.HEAD);
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
					startPoint = startPoint.substring(0, index);
				}
				validate();
			}
		});

		for (GitCommit commit : commits)
		{
			startPointText.add(commitMessage(commit));
		}

		SearchingContentProposalProvider proposalProvider = new SearchingContentProposalProvider(commits);
		ContentProposalAdapter adapter = new ContentProposalAdapter(startPointText, new ComboContentAdapter(),
				proposalProvider, KeyStroke.getInstance(SWT.CONTROL, ' '), null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		ControlDecoration decoration = new ControlDecoration(startPointText, SWT.LEFT);
		decoration.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());

		return composite;
	}

	protected void validate()
	{
		if (StringUtil.isEmpty(tagName))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CreateTagDialog_NonEmptyTagNameMessage));
			return;
		}

		String trimmed = tagName.trim();
		if (trimmed.contains(" ") || trimmed.contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CreateTagDialog_NoWhitespaceTagNameMessage));
			return;
		}
		if (repo.tags().contains(trimmed))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CreateTagDialog_TagAlreadyExistsMessage));
			return;
		}
		if (!repo.validRefName(GitRef.REFS_TAGS + trimmed))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CreateTagDialog_InvalidTagNameMessage));
			return;
		}

		// Validate the rev object for startPoint
		IStatus status = repo.revParse(startPoint);
		if (!status.isOK())
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CompareWithDialog_InvalidRefError));
			return;
		}
		updateStatus(Status.OK_STATUS);
	}

	public String getStartPoint()
	{
		return startPoint;
	}

	public String getMessage()
	{
		return message;
	}

	public String getTagName()
	{
		return tagName;
	}

	protected static String commitMessage(GitCommit commit)
	{
		return MessageFormat.format("{0} {1}", commit.sha().substring(0, 8), commit.getSubject()); //$NON-NLS-1$
	}

	/**
	 * Based on contents of input field, we search the set of git commits provided for any containing that input in the
	 * commit message. Matching commits are returned as results.
	 * 
	 * @author cwilliams
	 */
	private static class SearchingContentProposalProvider implements IContentProposalProvider
	{

		// TODO Combine with provider from CompareWithDialog...
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
			return list.toArray(new IContentProposal[list.size()]);
		}
	}
}
