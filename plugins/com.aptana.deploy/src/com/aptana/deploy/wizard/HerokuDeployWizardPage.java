package com.aptana.deploy.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.deploy.Activator;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;

public class HerokuDeployWizardPage extends WizardPage
{

	private static final String HEROKU_ICON = "icons/heroku.png"; //$NON-NLS-1$

	static final String NAME = "HerokuDeploy"; //$NON-NLS-1$

	private Text appName;
	private Button publishButton;

	protected HerokuDeployWizardPage()
	{
		super(NAME, Messages.HerokuDeployWizardPage_Title, Activator.getImageDescriptor(HEROKU_ICON));
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		initializeDialogUnits(parent);

		// Actual contents
		Composite appSettings = new Composite(composite, SWT.NULL);
		appSettings.setLayout(new GridLayout(2, false));

		Label label = new Label(appSettings, SWT.NONE);
		label.setText(Messages.HerokuDeployWizardPage_ApplicationNameLabel);

		appName = new Text(appSettings, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(250, SWT.DEFAULT);
		appName.setLayoutData(gd);
		// Set default name to project name
		appName.setText(getProjectName());
		appName.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(ModifyEvent e)
			{
				getContainer().updateButtons();
			}
		});

		publishButton = new Button(composite, SWT.CHECK);
		publishButton.setText(Messages.HerokuDeployWizardPage_PublishApplicationLabel);
		publishButton.setSelection(true);

		if (doesntHaveGitRepo())
		{
			Label note = new Label(composite, SWT.WRAP);
			// We need this italic, we may need to set a font explicitly here to get it
			Font dialogFont = JFaceResources.getDialogFont();
			FontData[] data = dialogFont.getFontData();
			for (FontData dataElement : data)
				dataElement.setStyle(dataElement.getStyle() | SWT.ITALIC);
			Font italic = new Font(dialogFont.getDevice(), data);
			note.setFont(italic);

			gd = new GridData(400, SWT.DEFAULT);
			note.setLayoutData(gd);
			note.setText(Messages.HerokuDeployWizardPage_NoGitRepoNote);
		}

		Dialog.applyDialogFont(composite);
	}

	protected String getProjectName()
	{
		IProject project = getProject();
		if (project == null)
		{
			return ""; // Seems like we have big issues if we ever got into this state... //$NON-NLS-1$
		}
		return project.getName();
	}

	protected boolean doesntHaveGitRepo()
	{
		IProject project = getProject();
		if (project == null)
		{
			return false; // Seems like we have big issues if we ever got into this state...
		}
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager()
				.getUnattachedExisting(project.getLocationURI());
		if (repo != null)
		{
			return false;
		}
		return true;
	}

	protected IProject getProject()
	{
		DeployWizard wizard = (DeployWizard) getWizard();
		IProject project = wizard.getProject();
		return project;
	}

	@Override
	public IWizardPage getNextPage()
	{
		// This is the end of the line!
		return null;
	}

	@Override
	public boolean isPageComplete()
	{
		// Make sure the app name is not blank
		String app = this.appName.getText();
		if (app == null || app.trim().length() < 1)
		{
			setErrorMessage(Messages.HerokuDeployWizardPage_EmotyApplicationNameError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	public String getAppName()
	{
		return appName.getText();
	}

	public boolean publishImmediately()
	{
		return publishButton.getSelection();
	}
}
