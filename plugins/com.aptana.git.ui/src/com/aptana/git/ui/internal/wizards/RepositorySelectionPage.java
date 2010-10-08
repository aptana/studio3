package com.aptana.git.ui.internal.wizards;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.git.core.model.GitRepository;

class RepositorySelectionPage extends WizardPage
{

	private Text directoryText;
	private Text source;
	private String sourceURI;
	private String destination;

	protected RepositorySelectionPage()
	{
		super(RepositorySelectionPage.class.getName());
		setTitle(Messages.RepositorySelectionPage_Title);
		setDescription(Messages.RepositorySelectionPage_Description);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));

		Label sourceLabel = new Label(composite, SWT.NONE);
		sourceLabel.setText(Messages.RepositorySelectionPage_SourceURI_Label);

		source = new Text(composite, SWT.BORDER | SWT.SINGLE);
		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		data.horizontalSpan = 2;
		data.widthHint = 300;
		source.setLayoutData(data);
		source.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				String sourceURI = source.getText();
				if (sourceURI != null)
				{
					// Try to stick to a default of a project under workspace matching the last path of the remote git
					// repo
					String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
					int index = sourceURI.lastIndexOf(GitRepository.GIT_DIR);
					if (index == -1)
					{
						index = sourceURI.length();
					}
					int slash = sourceURI.lastIndexOf("/", index); //$NON-NLS-1$
					if (slash != -1)
					{
						workspacePath += File.separator + sourceURI.substring(slash + 1, index);
					}
					directoryText.setText(workspacePath);
				}
				checkPage();
			}
		});

		Label dest = new Label(composite, SWT.NONE);
		dest.setText(Messages.RepositorySelectionPage_Destination_Label);

		directoryText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		data.widthHint = 300;
		directoryText.setLayoutData(data);
		directoryText.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				checkPage();
			}
		});

		final Button b = new Button(composite, SWT.PUSH);
		b.setText(Messages.RepositorySelectionPage_Browse_Label);
		b.addSelectionListener(new SelectionAdapter()
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
					directoryText.setText(r);
			}
		});

		setControl(composite);
		checkPage();
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
}
