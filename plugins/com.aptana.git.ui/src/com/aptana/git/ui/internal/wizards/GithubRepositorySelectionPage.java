/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.preferences.GithubAccountPageProvider;

public class GithubRepositorySelectionPage extends WizardPage
{

	/**
	 * Template for default Git URI.
	 */
	private static final String DEFAULT_GIT_URI = "git://github.com/{0}/example.git"; //$NON-NLS-1$

	/**
	 * Fallback value for username if we don't have github login or a value in "user.name".
	 */
	private static final String USER = "user"; //$NON-NLS-1$

	/**
	 * System property containing username. If we don't know user's github login we try the value here.
	 */
	private static final String USER_NAME = "user.name"; //$NON-NLS-1$

	/**
	 * Location where we're cloning to.
	 */
	private String destination;

	private GithubAccountPageProvider userInfoProvider;
	private Control userInfoControl;

	/**
	 * Text field to hold the destination directory.
	 */
	private Text destinationText;

	private Text ownerText;

	private Text repoText;

	private String owner;

	private String repoName;

	protected GithubRepositorySelectionPage()
	{
		super(RepositorySelectionPage.class.getName());
		setTitle(Messages.RepositorySelectionPage_Title);
		setDescription(Messages.RepositorySelectionPage_Description);
	}

	public void createControl(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		GridDataFactory inputData = GridDataFactory.swtDefaults().hint(300, SWT.DEFAULT);

		// adds control for login credentials
		userInfoProvider = new GithubAccountPageProvider();
		userInfoControl = userInfoProvider.createContents(main);
		userInfoControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());

		// Owner
		Label ownerLabel = new Label(main, SWT.NONE);
		ownerLabel.setText("Owner:");

		ownerText = new Text(main, SWT.BORDER | SWT.SINGLE);
		ownerText.setLayoutData(inputData.create());
		ownerText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				owner = ownerText.getText();
				validate();
			}
		});
		
		// spacer to take up last section of grid
		new Label(main, SWT.NONE);

		// Repo
		Label repoLabel = new Label(main, SWT.NONE);
		repoLabel.setText("Repository Name:");

		repoText = new Text(main, SWT.BORDER | SWT.SINGLE);
		repoText.setLayoutData(inputData.create());
		repoText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				repoName = repoText.getText();
				validate();
			}
		});

		// spacer to take up last section of grid
		new Label(main, SWT.NONE);

		Label dest = new Label(main, SWT.NONE);
		dest.setText(Messages.RepositorySelectionPage_Destination_Label);

		destinationText = new Text(main, SWT.BORDER | SWT.SINGLE);
		destinationText.setLayoutData(inputData.create());
		destinationText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				validate();
			}
		});

		Button destinationButton = new Button(main, SWT.PUSH);
		destinationButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		destinationButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				FileDialog d = new FileDialog(getShell(), SWT.APPLICATION_MODAL | SWT.SAVE);
				String text = destinationText.getText();
				if (!StringUtil.isEmpty(text))
				{
					File file = new File(text).getAbsoluteFile();
					d.setFilterPath(file.getParent());
					d.setFileName(file.getName());
				}

				String r = d.open();
				if (r != null)
				{
					destinationText.setText(r);
				}
			}
		});

		setErrorMessage(null);

		setControl(main);
	}

	protected void updateURI()
	{
		if (owner != null && repoName != null)
		{
			// Try to stick to a default of a project under workspace matching the last path of the remote git
			// repo
			String workspacePath = generateDestinationPath();
			destinationText.setText(workspacePath);
		}
		validate();
	}

	/**
	 * Check internal state for page completion status.
	 */
	private void validate()
	{
		if (StringUtil.isEmpty(owner))
		{
			setErrorMessage("Owner required");
			setPageComplete(false);
			return;
		}

		if (StringUtil.isEmpty(repoName))
		{
			setErrorMessage("Repository name required");
			setPageComplete(false);
			return;
		}

		String dstpath = destinationText.getText();
		if (StringUtil.isEmpty(dstpath))
		{
			setErrorMessage(Messages.RepositorySelectionPage_DestinatioNRequired_Message);
			setPageComplete(false);
			return;
		}
		File absoluteFile = new File(dstpath).getAbsoluteFile();
		if (!isEmptyDir(absoluteFile))
		{
			setErrorMessage(NLS.bind(Messages.RepositorySelectionPage_DirectoryExists_ErrorMessage,
					absoluteFile.getPath()));
			setPageComplete(false);
			return;
		}

		if (!canCreateSubdir(absoluteFile.getParentFile()))
		{
			setErrorMessage(NLS.bind(Messages.RepositorySelectionPage_CannotCreateDirectory_ErrorMessage,
					absoluteFile.getPath()));
			setPageComplete(false);
			return;
		}
		this.destination = dstpath;

		// Validate by checking repo exists
		try
		{
			getGithubManager().getRepo(owner, repoName);

			setErrorMessage(null);
			setPageComplete(true);
		}
		catch (CoreException e)
		{
			setErrorMessage(e.getStatus().getMessage());
			setPageComplete(false);
		}
	}

	private IGithubManager getGithubManager()
	{
		return GitPlugin.getDefault().getGithubManager();
	}

	private static boolean isEmptyDir(File dir)
	{
		if (!dir.exists())
		{
			return true;
		}
		if (!dir.isDirectory())
		{
			return false;
		}
		return dir.listFiles().length == 0;
	}

	// this is actually just an optimistic heuristic - should be named
	// isThereHopeThatCanCreateSubdir() as probably there is no 100% reliable
	// way to check that in Java for Windows
	private static boolean canCreateSubdir(File parent)
	{
		if (parent == null)
		{
			return true;
		}
		if (parent.exists())
		{
			return parent.isDirectory() && parent.canWrite();
		}
		return canCreateSubdir(parent.getParentFile());
	}

	public String getDestination()
	{
		return destination;
	}

	protected String generateDestinationPath()
	{
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		String uri = owner + '/' + repoName;
		int index = uri.lastIndexOf(GitRepository.GIT_DIR);
		if (index == -1)
		{
			index = uri.length();
		}
		int slash = uri.lastIndexOf('/', index);
		if (slash != -1)
		{
			workspacePath += File.separator + uri.substring(slash + 1, index);
		}
		return workspacePath;
	}

	public String getOwner()
	{
		return owner;
	}

	public String getRepoName()
	{
		return repoName;
	}
}
