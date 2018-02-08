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
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRef;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;

/**
 * Gives various references to compare the current resource with. These include commit SHAs from the resource's git
 * history, as well as various branches/tags.
 * 
 * @author cwilliams
 */
public class CompareWithDialog extends StatusDialog
{

	private GitRepository repo;
	private Combo refText;
	private final List<GitCommit> commits;
	private final Set<GitRef> simpleRefs;
	private String refValue;

	public CompareWithDialog(Shell parentShell, GitRepository repo, List<GitCommit> commits)
	{
		super(parentShell);
		setTitle(Messages.CompareWithDialog_Title);

		Assert.isNotNull(repo, "Must have a non-null git repository!"); //$NON-NLS-1$
		this.repo = repo;
		this.commits = commits;
		this.simpleRefs = repo.simpleRefs();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		// The explanatory message
		Label description = new Label(composite, SWT.WRAP);
		description.setText(Messages.CompareWithDialog_Message);
		description.setLayoutData(GridDataFactory.fillDefaults().hint(250, 70).create());

		// A label and combo with CA for choosing the ref to compare with
		Composite group = new Composite(composite, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).hint(250, 30).create());

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.CompareWithDialog_Ref_label);

		refText = new Combo(group, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
		refText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		refText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				refValue = refText.getText();

				// In case they picked a commit, grab only the SHA (shortened)
				int index = refValue.indexOf(' ');
				if (index != -1)
				{
					refValue = refValue.substring(0, index);
				}
				validate();
			}
		});

		// populate possible common values: HEAD, branches, tags, commits
		refText.add(GitRepository.HEAD);
		for (GitRef ref : simpleRefs)
		{
			refText.add(ref.shortName());
		}
		for (GitCommit commit : commits)
		{
			refText.add(commitMessage(commit));
		}
		// set default value of HEAD
		refText.setText(refValue = GitRepository.HEAD);

		SearchingContentProposalProvider proposalProvider = new SearchingContentProposalProvider();
		ContentProposalAdapter adapter = new ContentProposalAdapter(refText, new ComboContentAdapter(),
				proposalProvider, KeyStroke.getInstance(SWT.CONTROL, ' '), null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		ControlDecoration decoration = new ControlDecoration(refText, SWT.LEFT);
		decoration.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());

		updateStatus(Status.OK_STATUS);
		return composite;
	}

	protected void validate()
	{
		if (StringUtil.isEmpty(refValue))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CompareWithDialog_NonEmptyRefMessage));
			return;
		}

		IStatus status = repo.revParse(refValue);
		if (!status.isOK())
		{
			status = new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), Messages.CompareWithDialog_InvalidRefError);
		}
		updateStatus(status);
	}

	public GitCommit getRefCommit()
	{
		return new GitCommit(repo, refValue);
	}

	protected static String commitMessage(GitCommit commit)
	{
		return MessageFormat.format("{0} {1}", commit.sha().substring(0, 8), commit.getSubject()); //$NON-NLS-1$
	}

	/**
	 * Based on contents of input field, we search the set of git commits provided for any containing that input in the
	 * commit message. Matching commits are returned as results. We also search the set of "simple refs".
	 * 
	 * @author cwilliams
	 */
	private class SearchingContentProposalProvider implements IContentProposalProvider
	{

		private SearchingContentProposalProvider()
		{
		}

		public IContentProposal[] getProposals(String contents, int position)
		{
			List<IContentProposal> list = new ArrayList<IContentProposal>();

			// HEAD is a possibility
			if (GitRepository.HEAD.indexOf(contents) != -1)
			{
				list.add(new SimpleContentProposal(GitRepository.HEAD));
			}

			// Any local/remote branch or tag is a possibility
			for (GitRef ref : simpleRefs)
			{
				if (ref.shortName().indexOf(contents) != -1)
				{
					list.add(new SimpleContentProposal(ref.shortName()));
				}
				else if (ref.toString().indexOf(contents) != -1)
				{
					list.add(new SimpleContentProposal(ref.toString()));
				}
			}

			// Any commit in the resource's history is a possibility
			for (GitCommit commit : commits)
			{
				String msg = commitMessage(commit);
				if (msg.indexOf(contents) != -1)
				{
					list.add(new SimpleContentProposal(msg));
				}
			}
			return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
		}
	}

	/**
	 * Simplest implementation of {@link IContentProposal}
	 * 
	 * @author cwilliams
	 */
	private static class SimpleContentProposal implements IContentProposal
	{
		private String msg;

		private SimpleContentProposal(String msg)
		{
			this.msg = msg;
		}

		public String getContent()
		{
			return msg;
		}

		public int getCursorPosition()
		{
			return msg.length();
		}

		public String getDescription()
		{
			return null;
		}

		public String getLabel()
		{
			return msg;
		}
	}
}
