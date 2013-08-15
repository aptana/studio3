/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.github.IGithubUser;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.preferences.GithubAccountPageProvider;
import com.aptana.ui.util.UIUtils;

class RepositorySelectionPage extends WizardPage
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
	 * Field which holds the "active" URI to clone from. Toggled by {@link #fromGithub} and {@link #fromURI}.
	 */
	private String sourceURI;
	/**
	 * Location where we're cloning to.
	 */
	private String destination;

	private GithubAccountPageProvider userInfoProvider;
	private Control userInfoControl;

	/**
	 * Combo containing user's github repos.
	 */
	private Combo githubReposCombo;
	/**
	 * Text field to enter a git clone URI manually
	 */
	private Text sourceURIText;
	/**
	 * Text field to hold the destination directory.
	 */
	private Text destinationText;

	/**
	 * Radio buttons to determine if we're grabbing from github or from manual entry.
	 */
	private Button fromGithub;
	private Button fromURI;

	protected RepositorySelectionPage()
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
		userInfoProvider.addListener(new GithubAccountPageProvider.GithubListener()
		{

			public void loggedIn()
			{
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						fromGithub.setEnabled(true);
						reloadGithubRepos();
						updateSourceURIText();
						updateSourceURI();
					}
				});
			}

			public void loggedOut()
			{
				UIUtils.getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						reloadGithubRepos();
						fromGithub.setEnabled(false);
						fromGithub.setSelection(false);
						fromURI.setSelection(true);
						updateEnablement();
						updateSourceURIText();
						updateSourceURI();
					}
				});
			}
		});

		IGithubManager manager = getGithubManager();
		IGithubUser user = manager.getUser();

		// Button and combo for github
		fromGithub = new Button(main, SWT.RADIO);
		fromGithub.setText(StringUtil.makeFormLabel(Messages.RepositorySelectionPage_LBL_Github));
		fromGithub.setEnabled(user != null);
		fromGithub.setSelection(user != null);
		fromGithub.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateSourceURI();
				updateEnablement();
			}
		});

		// Add pulldown for repos
		githubReposCombo = new Combo(main, SWT.READ_ONLY | SWT.SINGLE);
		githubReposCombo.setLayoutData(inputData.create());
		reloadGithubRepos();

		new Label(main, SWT.NONE); // spacer

		// From URI
		fromURI = new Button(main, SWT.RADIO);
		fromURI.setText(Messages.RepositorySelectionPage_SourceURI_Label);
		fromURI.setSelection(user == null);
		fromURI.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateSourceURI();
				updateEnablement();
			}
		});

		sourceURIText = new Text(main, SWT.BORDER | SWT.SINGLE);
		sourceURIText.setLayoutData(inputData.create());
		updateSourceURIText();
		sourceURIText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				updateURI(sourceURIText.getText());
			}
		});

		new Label(main, SWT.NONE);

		// TODO Add a separator here?

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

		updateSourceURI();
		updateEnablement();
		setErrorMessage(null);

		setControl(main);
	}

	private void updateSourceURIText()
	{
		IGithubUser user = getGithubManager().getUser();
		String username = (user == null) ? System.getProperty(USER_NAME) : user.getUsername();
		if (StringUtil.isEmpty(username))
		{
			username = USER;
		}
		sourceURIText.setMessage(MessageFormat.format(DEFAULT_GIT_URI, username));
	}

	protected void updateSourceURI()
	{
		String uri;
		if (fromGithub.getSelection())
		{
			uri = githubReposCombo.getText();
		}
		else
		{
			uri = sourceURIText.getText();
		}
		updateURI(uri);
	}

	protected void updateURI(String uri)
	{
		sourceURI = uri;
		if (sourceURI != null)
		{
			// Try to stick to a default of a project under workspace matching the last path of the remote git
			// repo
			String workspacePath = generateDestinationPath(sourceURI);
			destinationText.setText(workspacePath);
		}
		validate();
	}

	/**
	 * Check internal state for page completion status.
	 */
	private void validate()
	{
		if (StringUtil.isEmpty(sourceURI))
		{
			setErrorMessage(Messages.RepositorySelectionPage_SourceURIRequired_Message);
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

		setErrorMessage(null);
		setPageComplete(true);
	}

	private void reloadGithubRepos()
	{
		IGithubManager manager = getGithubManager();
		IGithubUser user = manager.getUser();
		if (user == null)
		{
			githubReposCombo.removeAll();
		}
		else
		{
			try
			{
				List<IGithubRepository> repoList = user.getAllRepos();
				if (!CollectionsUtil.isEmpty(repoList))
				{
					for (IGithubRepository repo : repoList)
					{
						githubReposCombo.add(repo.getSSHURL());
					}
					githubReposCombo.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							updateURI(githubReposCombo.getText());
						}
					});
					githubReposCombo.select(0);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e);
			}
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

	public String getSource()
	{
		return sourceURI;
	}

	public String getDestination()
	{
		return destination;
	}

	protected String generateDestinationPath(String uri)
	{
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
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

	private void updateEnablement()
	{
		boolean isFromGithub = fromGithub.getSelection();
		githubReposCombo.setEnabled(isFromGithub);
		sourceURIText.setEnabled(!isFromGithub);
	}
}
