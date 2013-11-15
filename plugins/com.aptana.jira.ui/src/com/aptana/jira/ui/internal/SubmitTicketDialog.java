/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.ui.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.jira.core.JiraIssueSeverity;
import com.aptana.jira.core.JiraIssueType;
import com.aptana.jira.ui.JiraUIPlugin;
import com.aptana.jira.ui.preferences.JiraPreferencePageProvider;
import com.aptana.ui.preferences.IAccountPageProvider;
import com.aptana.ui.util.SWTUtils;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class SubmitTicketDialog extends TitleAreaDialog
{

	private static final String IMAGE_PATH = "icons/full/wizban/jira_wiz.png"; //$NON-NLS-1$

	private JiraPreferencePageProvider userInfoProvider;
	private Control userInfoControl;
	private ComboViewer typeCombo;
	private ComboViewer severityCombo;
	private Text summaryText;
	private Text reproduceText;
	private Text actualResultText;
	private Text expectedResultText;
	private Button studioLogCheckbox;
	private Button diagnosticLogCheckbox;
	private Text screenshotText;
	private Button browseButton;
	private Composite screenshotsComposite;
	private List<Button> screenshotCheckboxes;
	private ProgressMonitorPart progressMonitorPart;

	private JiraIssueType type;
	private JiraIssueSeverity severity;
	private String summary;
	private String description;
	private boolean studioLogSelected;
	private boolean diagnosticLogSelected;
	private Set<IPath> screenshots;

	public SubmitTicketDialog(Shell parentShell)
	{
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		screenshotCheckboxes = new ArrayList<Button>();
		screenshots = new LinkedHashSet<IPath>();
	}

	/**
	 * @return the issue type
	 */
	public JiraIssueType getType()
	{
		return type;
	}

	public JiraIssueSeverity getSeverity()
	{
		return severity;
	}

	/**
	 * @return the summary text
	 */
	public String getSummary()
	{
		return summary;
	}

	/**
	 * @return the description text
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return true if the Studio log should be attached, false otherwise
	 */
	public boolean getStudioLogSelected()
	{
		return studioLogSelected;
	}

	/**
	 * @return true if the diagnostic log should be attached, false otherwise
	 */
	public boolean getDiagnosticLogSelected()
	{
		return diagnosticLogSelected;
	}

	/**
	 * @return the set of screenshot paths
	 */
	public Set<IPath> getScreenshots()
	{
		return new LinkedHashSet<IPath>(screenshots);
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.SubmitTicketDialog_ShellTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Label separator = new Label(main, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		// adds control for login credentials
		userInfoProvider = new JiraPreferencePageProvider();
		userInfoControl = userInfoProvider.createContents(main);
		userInfoControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		// issue type
		Label label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_Type));
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		typeCombo = new ComboViewer(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setContentProvider(ArrayContentProvider.getInstance());
		typeCombo.setLabelProvider(new LabelProvider());
		typeCombo.setInput(JiraIssueType.values());
		typeCombo.getControl().setLayoutData(GridDataFactory.swtDefaults().create());
		typeCombo.setSelection(new StructuredSelection(JiraIssueType.BUG));
		ISelectionChangedListener listener = new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				validate();
			}
		};
		typeCombo.addSelectionChangedListener(listener);

		// FIXME Severity doesn't apply for Story in Studio tracker
		// severity
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_Severity));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		severityCombo = new ComboViewer(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		severityCombo.setContentProvider(ArrayContentProvider.getInstance());
		severityCombo.setLabelProvider(new LabelProvider());
		severityCombo.setInput(JiraIssueSeverity.values());
		severityCombo.getControl().setLayoutData(GridDataFactory.swtDefaults().create());
		severityCombo.setSelection(new StructuredSelection(JiraIssueSeverity.MINOR));
		severityCombo.addSelectionChangedListener(listener);

		// summary
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_Summary));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		summaryText = new Text(main, SWT.BORDER | SWT.SINGLE);
		summaryText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		ModifyListener modifyListener = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		};
		summaryText.addModifyListener(modifyListener);

		// Steps to Reproduce
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_StepsToReproduce));
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());

		reproduceText = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		reproduceText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 100).create());
		reproduceText.addModifyListener(modifyListener);
		TraverseListener traverseListener = new TraverseListener()
		{

			public void keyTraversed(TraverseEvent e)
			{
				e.doit = true;
			}
		};
		reproduceText.addTraverseListener(traverseListener);

		// Actual Result
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_ActualResult));
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());

		actualResultText = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		actualResultText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 50).create());
		actualResultText.addModifyListener(modifyListener);
		actualResultText.addTraverseListener(traverseListener);

		// Expected Result
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_ExpectedResult));
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());

		expectedResultText = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		expectedResultText
				.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 50).create());
		expectedResultText.addModifyListener(modifyListener);
		expectedResultText.addTraverseListener(traverseListener);

		// logs to attach
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_LogsToAttach));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		Composite composite = new Composite(main, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		studioLogCheckbox = new Button(composite, SWT.CHECK);
		studioLogCheckbox.setText(Messages.SubmitTicketDialog_LBL_StudioLog);
		studioLogCheckbox.setSelection(true);

		diagnosticLogCheckbox = new Button(composite, SWT.CHECK);
		diagnosticLogCheckbox.setText(Messages.SubmitTicketDialog_LBL_DiagnosticLog);
		diagnosticLogCheckbox.setSelection(true);

		// screenshots
		label = new Label(main, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SubmitTicketDialog_LBL_Screenshots));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		composite = new Composite(main, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		screenshotText = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		screenshotText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());
		screenshotText.addMouseListener(new MouseListener()
		{

			public void mouseUp(MouseEvent e)
			{
				browse();
			}

			public void mouseDown(MouseEvent e)
			{
			}

			public void mouseDoubleClick(MouseEvent e)
			{
			}
		});
		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		browseButton.setLayoutData(GridDataFactory.swtDefaults().create());
		browseButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browse();
			}
		});

		// the list of selected screenshots
		label = new Label(main, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		screenshotsComposite = new Composite(main, SWT.NONE);
		screenshotsComposite.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
		screenshotsComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// a progress bar for when validating the login
		progressMonitorPart = new ProgressMonitorPart(main, GridLayoutFactory.fillDefaults().create());
		progressMonitorPart.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(true).span(2, 1)
				.create());

		userInfoProvider.setProgressMonitor(progressMonitorPart);
		userInfoProvider.addValidationListener(new IAccountPageProvider.IValidationListener()
		{

			public void preValidationStart()
			{
				setUILocked(true);
				progressMonitorPart.setVisible(true);
				((GridData) progressMonitorPart.getLayoutData()).exclude = false;
				layoutShell();
			}

			public void postValidationEnd()
			{
				setUILocked(false);
				if (!progressMonitorPart.isDisposed())
				{
					progressMonitorPart.setVisible(false);
					((GridData) progressMonitorPart.getLayoutData()).exclude = true;
					layoutShell();
				}
			}
		});

		setTitle(Messages.SubmitTicketDialog_Title);
		setTitleImage(SWTUtils.getImage(JiraUIPlugin.getDefault(), IMAGE_PATH));
		setMessage(Messages.SubmitTicketDialog_DefaultMessage);

		return main;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected void cancelPressed()
	{
		progressMonitorPart.setCanceled(true);
		super.cancelPressed();
	}

	@Override
	protected void okPressed()
	{
		if (!userInfoProvider.performOk())
		{
			return;
		}
		type = (JiraIssueType) ((IStructuredSelection) typeCombo.getSelection()).getFirstElement();
		severity = (JiraIssueSeverity) ((IStructuredSelection) severityCombo.getSelection()).getFirstElement();
		summary = summaryText.getText();
		description = MessageFormat.format(
				"h3. Steps to Reproduce\n{0}\n\nh3. Actual Result\n{1}\n\nh3. Expected Result\n{2}", //$NON-NLS-1$
				reproduceText.getText(), actualResultText.getText(), expectedResultText.getText());
		studioLogSelected = studioLogCheckbox.getSelection();
		diagnosticLogSelected = diagnosticLogCheckbox.getSelection();

		super.okPressed();
	}

	protected boolean validate()
	{
		String message = null;
		if (typeCombo.getSelection().isEmpty())
		{
			message = Messages.SubmitTicketDialog_ERR_EmptyType;
		}
		else if (StringUtil.isEmpty(summaryText.getText()))
		{
			message = Messages.SubmitTicketDialog_ERR_EmptySummary;
		}
		else if (StringUtil.isEmpty(reproduceText.getText()))
		{
			message = Messages.SubmitTicketDialog_ERR_EmptyStepsToReproduce;
		}
		else if (StringUtil.isEmpty(actualResultText.getText()))
		{
			message = Messages.SubmitTicketDialog_ERR_EmptyActualResult;
		}
		else if (StringUtil.isEmpty(expectedResultText.getText()))
		{
			message = Messages.SubmitTicketDialog_ERR_EmptyExpectedResult;
		}

		setErrorMessage(message);
		if (message == null)
		{
			setMessage(Messages.SubmitTicketDialog_DefaultMessage);
		}

		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null)
		{
			button.setEnabled(message == null);
		}
		return message == null;
	}

	private void browse()
	{
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		String file = dialog.open();
		if (!StringUtil.isEmpty(file))
		{
			final IPath path = Path.fromOSString(file);
			if (screenshots.contains(path))
			{
				return;
			}
			screenshots.add(path);

			// adds a checkbox entry for the file
			final Button checkbox = new Button(screenshotsComposite, SWT.CHECK);
			screenshotCheckboxes.add(checkbox);
			checkbox.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
			// just shows the filename in case the path is too long
			checkbox.setText(path.lastSegment());
			checkbox.setSelection(true);
			checkbox.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (checkbox.getSelection())
					{
						screenshots.add(path);
					}
					else
					{
						screenshots.remove(path);
					}
				}
			});
			((Composite) getContents()).layout(true, true);
			// resizes the dialog height if necessary
			Shell shell = getShell();
			Point currentSize = shell.getSize();
			int neededHeight = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			if (neededHeight > currentSize.y)
			{
				shell.setSize(currentSize.x, neededHeight);
			}
		}
	}

	private void layoutShell()
	{
		Point size = getInitialSize();
		Rectangle bounds = getConstrainedShellBounds(new Rectangle(0, 0, size.x, size.y));
		getShell().setSize(bounds.width, bounds.height);
	}

	private void setUILocked(boolean locked)
	{
		if (getContents() == null || getContents().isDisposed())
		{
			return;
		}
		typeCombo.getCombo().setEnabled(!locked);
		severityCombo.getCombo().setEnabled(!locked);
		summaryText.setEnabled(!locked);
		reproduceText.setEnabled(!locked);
		actualResultText.setEnabled(!locked);
		expectedResultText.setEnabled(!locked);
		studioLogCheckbox.setEnabled(!locked);
		diagnosticLogCheckbox.setEnabled(!locked);
		screenshotText.setEnabled(!locked);
		browseButton.setEnabled(!locked);
		for (Button checkbox : screenshotCheckboxes)
		{
			checkbox.setEnabled(!locked);
		}
	}
}
