/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubUser;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.preferences.GithubAccountPageProvider;

class RepositorySelectionPage extends WizardPage
{

	private Text directoryText;
	private Text source;
	private String sourceURI;
	private String destination;
	private GithubAccountPageProvider userInfoProvider;
	private Control userInfoControl;

	protected RepositorySelectionPage()
	{
		super(RepositorySelectionPage.class.getName());
		setTitle(Messages.RepositorySelectionPage_Title);
		setDescription(Messages.RepositorySelectionPage_Description);
	}

	public void createControl(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		GridDataFactory gdf = GridDataFactory.swtDefaults().hint(300, SWT.DEFAULT);

		// adds control for login credentials
		userInfoProvider = new GithubAccountPageProvider();
		userInfoControl = userInfoProvider.createContents(main);
		userInfoControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		IGithubManager manager = GitPlugin.getDefault().getGithubManager();
		IGithubUser user = manager.getUser();

		// Button and combo for github
		Button fromGithub = new Button(main, SWT.RADIO);
		fromGithub.setText("Github: ");
		fromGithub.setEnabled(user != null);
		fromGithub.setSelection(user != null);

		// Add pulldown for repos
		final Combo repos = new Combo(main, SWT.READ_ONLY | SWT.SINGLE);
		repos.setLayoutData(gdf.create());
		if (user != null)
		{
			try
			{
				List<String> repoList = manager.getRepos();
				if (!CollectionsUtil.isEmpty(repoList))
				{
					for (String repoURI : repoList)
					{
						repos.add(repoURI);
					}
					repos.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							String sourceURI = repos.getText();
							String workspacePath = generateDestinationPath(sourceURI);
							directoryText.setText(workspacePath);
							checkPage();
						}
					});
					repos.select(0);
				}
			}
			catch (CoreException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Button fromURI = new Button(main, SWT.RADIO);
		fromURI.setText(Messages.RepositorySelectionPage_SourceURI_Label);
		fromURI.setSelection(user == null);

		source = new Text(main, SWT.BORDER | SWT.SINGLE);
		source.setLayoutData(gdf.create());
		String username = user == null ? System.getProperty("user.name") : user.getUsername(); //$NON-NLS-1$
		if (StringUtil.isEmpty(username))
		{
			username = "user"; //$NON-NLS-1$
		}
		source.setMessage(MessageFormat.format("git://github.com/{0}/example.git", username)); //$NON-NLS-1$
		source.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				String sourceURI = source.getText();
				if (sourceURI != null)
				{
					// Try to stick to a default of a project under workspace matching the last path of the remote git
					// repo
					String workspacePath = generateDestinationPath(sourceURI);
					directoryText.setText(workspacePath);
				}
				checkPage();
			}
		});

		// TODO Add a separator here?

		Label dest = new Label(main, SWT.NONE);
		dest.setText(Messages.RepositorySelectionPage_Destination_Label);

		Composite comp = new Composite(main, SWT.NONE);
		comp.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		comp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		directoryText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		directoryText.setLayoutData(gdf.create());
		directoryText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				checkPage();
			}
		});

		final Button destinationButton = new Button(comp, SWT.PUSH);
		destinationButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		destinationButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				final FileDialog d;

				d = new FileDialog(getShell(), SWT.APPLICATION_MODAL | SWT.SAVE);
				if (directoryText.getText().length() > 0)
				{
					final File file = new File(directoryText.getText()).getAbsoluteFile();
					d.setFilterPath(file.getParent());
					d.setFileName(file.getName());
				}
				final String r = d.open();
				if (r != null)
				{
					directoryText.setText(r);
				}
			}
		});

		setControl(main);
	}

	/**
	 * Check internal state for page completion status.
	 */
	private void checkPage()
	{
		final String sourceURI = source.getText();
		if (sourceURI.trim().length() == 0)
		{
			setErrorMessage(Messages.RepositorySelectionPage_SourceURIRequired_Message);
			setPageComplete(false);
			return;
		}
		// TODO Check format of the URI
		this.sourceURI = sourceURI;

		final String dstpath = directoryText.getText();
		if (dstpath.length() == 0)
		{
			setErrorMessage(Messages.RepositorySelectionPage_DestinatioNRequired_Message);
			setPageComplete(false);
			return;
		}
		final File absoluteFile = new File(dstpath).getAbsoluteFile();
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

	private static boolean isEmptyDir(final File dir)
	{
		if (!dir.exists())
			return true;
		if (!dir.isDirectory())
			return false;
		return dir.listFiles().length == 0;
	}

	// this is actually just an optimistic heuristic - should be named
	// isThereHopeThatCanCreateSubdir() as probably there is no 100% reliable
	// way to check that in Java for Windows
	private static boolean canCreateSubdir(final File parent)
	{
		if (parent == null)
			return true;
		if (parent.exists())
			return parent.isDirectory() && parent.canWrite();
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

	protected String generateDestinationPath(String sourceURI)
	{
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		int index = sourceURI.lastIndexOf(GitRepository.GIT_DIR);
		if (index == -1)
		{
			index = sourceURI.length();
		}
		int slash = sourceURI.lastIndexOf('/', index);
		if (slash != -1)
		{
			workspacePath += File.separator + sourceURI.substring(slash + 1, index);
		}
		return workspacePath;
	}
}
