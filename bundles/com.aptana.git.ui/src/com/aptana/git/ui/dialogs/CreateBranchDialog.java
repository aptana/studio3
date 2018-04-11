/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import java.util.Set;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitRef;
import com.aptana.git.core.model.GitRepository;

public class CreateBranchDialog extends InputDialog
{

	private GitRepository repo;
	private Text startPointText;
	private Button trackButton;
	private boolean track;
	private String startPoint;

	/**
	 * Should we auto-turn on tracking? Look at the git repo's config value in constructor to determine the default
	 * value here.
	 */
	private boolean autoTrack = true;

	public CreateBranchDialog(final Shell parentShell, final GitRepository repo)
	{
		super(parentShell, Messages.CreateBranchDialog_CreateBranchDialog_Title,
				Messages.CreateBranchDialog_CreateBranchDialog_Message, StringUtil.EMPTY,
				new IInputValidator()
				{

					public String isValid(String newText)
					{
						if (newText == null || newText.trim().length() == 0)
						{
							return Messages.CreateBranchDialog_NonEmptyBranchNameMessage;
						}
						if (newText.trim().contains(" ") || newText.trim().contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
						{
							return Messages.CreateBranchDialog_NoWhitespaceBranchNameMessage;
						}
						if (repo.localBranches().contains(newText.trim()))
						{
							return Messages.CreateBranchDialog_BranchAlreadyExistsMessage;
						}
						if (!repo.validRefName(GitRef.REFS_HEADS + newText.trim()))
						{
							return Messages.CreateBranchDialog_InvalidBranchNameMessage;
						}
						return null;
					}
				});
		this.repo = repo;
		this.autoTrack = repo.autoSetupMerge();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		// Add an advanced section so users can specify a start point ref (so they can create a branch that
		// tracks a remote branch!)
		Composite composite = (Composite) super.createDialogArea(parent);

		// TODO Add a minimize/maximize button for the advanced section
		Group group = new Group(composite, SWT.DEFAULT);
		group.setText(Messages.CreateBranchDialog_AdvancedOptions_label);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.CreateBranchDialog_StartPoint_label);

		startPointText = new Text(group, getInputTextStyle());
		startPointText.setText(repo.headRef().simpleRef().shortName());
		startPointText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		startPointText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				startPoint = startPointText.getText();
				// TODO Validate the start point. Must be branch name, commit id or tag ref

				if (startPoint.indexOf('/') != -1 && autoTrack)
				{
					// If name is a remote branch, turn on track by default?
					for (String remoteName : repo.remotes())
					{
						if (startPoint.startsWith(remoteName + '/'))
						{
							trackButton.setSelection(true);
							track = true;
							break;
						}
					}
				}
			}
		});

		Set<String> simpleRefs = repo.allSimpleRefs();
		String[] proposals = simpleRefs.toArray(new String[simpleRefs.size()]);

		new AutoCompleteField(startPointText, new TextContentAdapter(), proposals);

		// Have CTRL+SPACE also trigger content assist
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(proposals);
		proposalProvider.setFiltering(true);
		ContentProposalAdapter adapter = new ContentProposalAdapter(startPointText, new TextContentAdapter(),
				proposalProvider, KeyStroke.getInstance(SWT.CONTROL, ' '), null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		ControlDecoration decoration = new ControlDecoration(startPointText, SWT.LEFT);
		decoration.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());

		trackButton = new Button(group, SWT.CHECK);
		trackButton.setText(Messages.CreateBranchDialog_Track_label);
		trackButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				track = trackButton.getSelection();
				autoTrack = false; // don't change the value since user modified it here.
			}
		});
		return composite;
	}

	public boolean track()
	{
		return track;
	}

	public String getStartPoint()
	{
		return startPoint;
	}
}
