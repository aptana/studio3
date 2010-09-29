package com.aptana.deploy.internal.wizard;

import java.lang.reflect.Field;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

import com.aptana.deploy.Activator;
import com.aptana.deploy.wizard.DeployWizard;

@SuppressWarnings("restriction")
public class EngineYardDeployWizardPage extends WizardPage
{

	private static final String EY_ICON = "icons/ey_small.png"; //$NON-NLS-1$

	public static final String NAME = "EngineYardDeploy"; //$NON-NLS-1$

	protected EngineYardDeployWizardPage()
	{
		super(NAME, Messages.EngineYardDeployWizardPage_Title, Activator.getImageDescriptor(EY_ICON));
	}

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
		label.setText(Messages.EngineYardDeployWizardPage_ApplicationNameLabel);

		Label note = new Label(composite, SWT.WRAP);
		Font dialogFont = JFaceResources.getDialogFont();
		FontData[] data = dialogFont.getFontData();
		for (FontData dataElement : data)
		{
			dataElement.setStyle(dataElement.getStyle() | SWT.ITALIC);
		}
		Font italic = new Font(dialogFont.getDevice(), data);
		note.setFont(italic);
		note.setLayoutData(new GridData(400, SWT.DEFAULT));
		note.setText(Messages.EngineYardDeployWizardPage_ApplicationNoteLabel);

		// Link to Engine Yard dashbaord
		Link link = new Link(composite, SWT.NONE);
		link.setText(Messages.EngineYardDeployWizardPage_ApplicationLinkLabel);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						try
						{
							final String BROWSER_ID = "EngineYard-login"; //$NON-NLS-1$
							final URL url = new URL("https://cloud.engineyard.com/dashboard"); //$NON-NLS-1$

							final int style = IWorkbenchBrowserSupport.NAVIGATION_BAR
									| IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.STATUS;

							WebBrowserEditorInput input = new WebBrowserEditorInput(url, style, BROWSER_ID);
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							if (page == null || input == null)
							{
								return;
							}
							IEditorPart editorPart = page.openEditor(input, WebBrowserEditor.WEB_BROWSER_EDITOR_ID);
							if (editorPart == null)
							{
								return;
							}
							WebBrowserEditor webBrowserEditor = (WebBrowserEditor) editorPart;
							Field f = WebBrowserEditor.class.getDeclaredField("webBrowser"); //$NON-NLS-1$
							f.setAccessible(true);
							BrowserViewer viewer = (BrowserViewer) f.get(webBrowserEditor);
							final Browser browser = viewer.getBrowser();
							browser.addProgressListener(new ProgressListener()
							{

								public void completed(ProgressEvent event)
								{
									browser.removeProgressListener(this);
								}

								public void changed(ProgressEvent event)
								{
									// ignore
								}
							});
						}
						catch (Exception e)
						{
							Activator.logError(e);
						}
					}
				});

				// close the wizard when the user clicks on the dashboard link
				((WizardDialog) getContainer()).close();

			}
		});

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

	protected IProject getProject()
	{
		DeployWizard wizard = (DeployWizard) getWizard();
		return wizard.getProject();
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
		setErrorMessage(null);
		return true;
	}
}
