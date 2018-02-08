/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.dialogs;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.theme.ThemePlugin;

/**
 * @author cwilliams
 */
public class CreatePullRequestDialog extends StatusDialog
{

	private Text bodyText;
	private String body;

	private Text titleText;
	private String title;
	private String baseBranch;
	private String head;
	private IGithubRepository baseRepo;
	private Combo baseBranchCombo;
	private Combo baseRepoCombo;
	private Set<IGithubRepository> repos;

	public CreatePullRequestDialog(final Shell parentShell, String defaultTitle, String defaultBody,
			IGithubRepository defaultRepo, Set<IGithubRepository> repos, String head)
	{
		super(parentShell);
		setTitle(Messages.CreatePullRequestDialog_Title);
		this.title = defaultTitle;
		this.body = defaultBody;
		this.head = head;
		this.repos = repos;
		this.baseRepo = defaultRepo;
		this.baseBranch = baseRepo.getDefaultBranch();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		// --- Overview of the two endpoints this PR is for
		Composite overview = new Composite(composite, SWT.NONE);
		overview.setLayout(GridLayoutFactory.fillDefaults().numColumns(5).create());

		// icon
		Label icon = new Label(overview, SWT.NONE);
		icon.setImage(GitUIPlugin.getImage("icons/obj16/pull_request.gif")); //$NON-NLS-1$

		Color lightBlue = ThemePlugin.getDefault().getColorManager().getColor(new RGB(209, 227, 237));
		// base
		baseRepoCombo = new Combo(overview, SWT.DROP_DOWN | SWT.READ_ONLY);
		baseRepoCombo.setFont(JFaceResources.getTextFont());
		baseRepoCombo.setBackground(lightBlue);
		List<String> names = CollectionsUtil.map(repos, new IMap<IGithubRepository, String>()
		{
			public String map(IGithubRepository item)
			{
				return item.getFullName();
			}
		});
		baseRepoCombo.setItems(names.toArray(new String[names.size()]));
		baseRepoCombo.setText(baseRepo.getFullName());
		baseRepoCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final String repoOwnerAndName = baseRepoCombo.getText();
				// Get the repo out of some map?
				baseRepo = CollectionsUtil.find(repos, new IFilter<IGithubRepository>()
				{
					public boolean include(IGithubRepository item)
					{
						return item.getFullName().equals(repoOwnerAndName);
					}
				});
				baseBranch = baseRepo.getDefaultBranch();

				// Reload the branches in the branch combo!
				Set<String> branches = baseRepo.getBranches();
				baseBranchCombo.setItems(branches.toArray(new String[branches.size()]));
				baseBranchCombo.setText(baseBranch);
			}
		});

		// FIXME Use toolbars so we want maintain the blue bg?
		baseBranchCombo = new Combo(overview, SWT.DROP_DOWN | SWT.READ_ONLY);
		baseBranchCombo.setFont(JFaceResources.getTextFont());
		baseBranchCombo.setBackground(lightBlue);

		baseBranchCombo.setItems(new String[] { baseBranch });
		baseBranchCombo.setText(baseBranch);
		// We need to set the items to be the list of branches on the base repo
		Display.getCurrent().asyncExec(new Runnable()
		{
			public void run()
			{
				Set<String> branches = baseRepo.getBranches();
				baseBranchCombo.setItems(branches.toArray(new String[branches.size()]));
				baseBranchCombo.setText(baseBranch);
			}
		});
		baseBranchCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				baseBranch = baseBranchCombo.getText();
			}
		});

		// ...
		Label ellipsis = new Label(overview, SWT.WRAP);
		ellipsis.setText(" ... "); //$NON-NLS-1$
		ellipsis.setFont(JFaceResources.getTextFont());
		ellipsis.setEnabled(false);

		// head
		Label headLabel = new Label(overview, SWT.WRAP);
		headLabel.setText(head);
		headLabel.setFont(JFaceResources.getTextFont());
		headLabel.setBackground(lightBlue);

		// ------- END OVERVIEW --------------------------

		// Title
		Label titleLabel = new Label(composite, SWT.WRAP);
		titleLabel.setText(Messages.CreatePullRequestDialog_TitleFieldLabel);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		titleLabel.setLayoutData(data);
		titleLabel.setFont(parent.getFont());

		titleText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		titleText.setText(title);
		titleText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		titleText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				title = titleText.getText();
				validate();
			}
		});

		// Body
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.CreatePullRequestDialog_BodyFieldLabel);

		bodyText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		bodyText.setText(body);
		GridDataFactory.fillDefaults().hint(400, 300).applyTo(bodyText);
		bodyText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				body = bodyText.getText();
				validate();
			}
		});
		return composite;
	}

	public String getBody()
	{
		return body;
	}

	public String getTitle()
	{
		return title;
	}

	protected void validate()
	{
		if (StringUtil.isEmpty(title))
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CreatePullRequestDialog_EmptyTitleErrMsg));
			return;
		}

		updateStatus(Status.OK_STATUS);
	}

	public String getBaseBranch()
	{
		return baseBranch;
	}

	public IGithubRepository getBaseRepo()
	{
		return baseRepo;
	}
}
