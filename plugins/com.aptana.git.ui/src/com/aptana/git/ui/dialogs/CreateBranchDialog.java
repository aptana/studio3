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
package com.aptana.git.ui.dialogs;

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

import com.aptana.git.core.model.GitRepository;

public class CreateBranchDialog extends InputDialog
{

	private GitRepository repo;
	private Text startPointText;
	private Button trackButton;
	private boolean track;
	private String startPoint;

	public CreateBranchDialog(final Shell parentShell, final GitRepository repo)
	{
		super(parentShell, Messages.CreateBranchDialog_CreateBranchDialog_Title,
				Messages.CreateBranchDialog_CreateBranchDialog_Message, "", //$NON-NLS-1$
				new IInputValidator()
				{

					public String isValid(String newText)
					{
						if (newText == null || newText.trim().length() == 0)
							return Messages.CreateBranchDialog_NonEmptyBranchNameMessage;
						if (newText.trim().contains(" ") || newText.trim().contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
							return Messages.CreateBranchDialog_NoWhitespaceBranchNameMessage;
						if (repo.localBranches().contains(newText.trim()))
							return Messages.CreateBranchDialog_BranchAlreadyExistsMessage;
						if (!repo.validBranchName(newText.trim()))
							return Messages.CreateBranchDialog_InvalidBranchNameMessage;
						return null;
					}
				});
		this.repo = repo;
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
				// TODO If name is a remote branch, turn on track by default?
			}
		});

		String[] proposals = repo.allSimpleRefs().toArray(new String[0]);

		new AutoCompleteField(startPointText, new TextContentAdapter(), proposals);
		
		//Have CTRL+SPACE also trigger content assist
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(proposals);
		proposalProvider.setFiltering(true);
		ContentProposalAdapter adapter = new ContentProposalAdapter(startPointText, new TextContentAdapter(),
				proposalProvider, KeyStroke.getInstance(SWT.CONTROL, ' '), null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		
		ControlDecoration decoration = new ControlDecoration(startPointText, SWT.LEFT);
		decoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());

		trackButton = new Button(group, SWT.CHECK);
		trackButton.setText(Messages.CreateBranchDialog_Track_label);
		trackButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				track = trackButton.getSelection();
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
