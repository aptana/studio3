package com.aptana.deploy.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.deploy.Activator;

public class HerokuSignupPage extends WizardPage
{

	static final String NAME = "HerokuSignup"; //$NON-NLS-1$
	private static final String HEROKU_ICON = "icons/heroku.png"; //$NON-NLS-1$

	private Text userId;
	private String startingUserId;

	protected HerokuSignupPage(String startingUserId)
	{
		super(NAME, Messages.HerokuSignupPage_Title, Activator.getImageDescriptor(HEROKU_ICON));
		this.startingUserId = startingUserId;
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
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.HerokuLoginWizardPage_EnterCredentialsLabel);

		Composite credentials = new Composite(composite, SWT.NONE);
		credentials.setLayout(new GridLayout(2, false));

		Label userIdLabel = new Label(credentials, SWT.NONE);
		userIdLabel.setText(Messages.HerokuLoginWizardPage_UserIDLabel);
		userId = new Text(credentials, SWT.SINGLE | SWT.BORDER);
		userId.setMessage(Messages.HerokuLoginWizardPage_UserIDExample);
		if (startingUserId != null && startingUserId.trim().length() > 0)
		{
			userId.setText(startingUserId);
		}
		GridData gd = new GridData(300, SWT.DEFAULT);
		userId.setLayoutData(gd);

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
		note.setText(Messages.HerokuSignupPage_SignupNote);

		// Add signup button
		Button signup = new Button(composite, SWT.PUSH);
		signup.setText(Messages.HerokuSignupPage_SignupButtonLabel);
		signup.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// basically just perform finish!
				if (getWizard().performFinish())
				{
					((WizardDialog) getContainer()).close();
				}
			}
		});

		Dialog.applyDialogFont(composite);
	}

	@Override
	public IWizardPage getNextPage()
	{
		// The end of the line
		return null;
	}

	public String getUserID()
	{
		return this.userId.getText();
	}

	@Override
	public boolean isPageComplete()
	{

		String userId = this.userId.getText();
		if (userId == null || userId.trim().length() < 1)
		{
			setErrorMessage(Messages.HerokuLoginWizardPage_EmptyUserIDError);
			return false;
		}

		setErrorMessage(null);
		return true;
	}
}
