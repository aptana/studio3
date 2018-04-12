/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.preferences.profile.IProfile;
import com.aptana.formatter.preferences.profile.IProfileManager;
import com.aptana.formatter.preferences.profile.IProfileStore;
import com.aptana.formatter.preferences.profile.ProfileKind;
import com.aptana.formatter.preferences.profile.ProfileManager;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.dialogFields.DialogField;
import com.aptana.formatter.ui.dialogFields.IDialogFieldListener;
import com.aptana.formatter.ui.dialogFields.StringDialogField;
import com.aptana.formatter.ui.util.ExceptionHandler;
import com.aptana.formatter.ui.util.IStatusChangeListener;
import com.aptana.formatter.ui.util.StatusInfo;

public abstract class FormatterModifyDialog extends StatusDialog implements IFormatterModifyDialog,
		IStatusChangeListener
{

	private final FormatterDialogPreferences preferences = new FormatterDialogPreferences();

	private final FormatterControlManager controlManager = new FormatterControlManager(preferences, this);

	private static final int SAVE_BUTTON_ID = IDialogConstants.CLIENT_ID + 1;

	private final IFormatterModifyDialogOwner dialogOwner;
	private final IScriptFormatterFactory formatterFactory;
	final IDialogSettings fDialogSettings;
	private Button fSaveButton;
	private StringDialogField fProfileNameField;

	private IProfileManager manager;
	protected IProfile profile;

	private IStatus tabStatus = Status.OK_STATUS;

	/**
	 * @param parent
	 */
	public FormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner, IScriptFormatterFactory formatterFactory)
	{
		super(dialogOwner.getShell());
		this.dialogOwner = dialogOwner;
		this.formatterFactory = formatterFactory;
		this.fDialogSettings = getDialogSettingsSection(dialogOwner.getDialogSettings(), formatterFactory.getId());
		setStatusLineAboveButtons(false);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public void setProfileManager(IProfileManager manager, String title)
	{
		this.manager = manager;
		this.profile = manager.getSelected(dialogOwner.getProject());
		setTitle(title);
	}

	private static IDialogSettings getDialogSettingsSection(IDialogSettings settings, String sectionId)
	{
		IDialogSettings section = settings.getSection(sectionId);
		if (section == null)
		{
			section = settings.addNewSection(sectionId);
		}
		return section;
	}

	private static final String KEY_X = "x"; //$NON-NLS-1$
	private static final String KEY_Y = "y"; //$NON-NLS-1$
	private static final String KEY_WIDTH = "width"; //$NON-NLS-1$
	private static final String KEY_HEIGHT = "height"; //$NON-NLS-1$

	@Override
	protected Point getInitialSize()
	{
		Point initialSize = super.getInitialSize();
		try
		{
			int lastWidth = fDialogSettings.getInt(KEY_WIDTH);
			// if (initialSize.x > lastWidth)
			// lastWidth = initialSize.x;
			int lastHeight = fDialogSettings.getInt(KEY_HEIGHT);
			// if (initialSize.y > lastHeight)
			// lastHeight = initialSize.y;
			return new Point(lastWidth, lastHeight);
		}
		catch (NumberFormatException ex)
		{
		}
		return initialSize;
	}

	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		try
		{
			return new Point(fDialogSettings.getInt(KEY_X), fDialogSettings.getInt(KEY_Y));
		}
		catch (NumberFormatException ex)
		{
			return super.getInitialLocation(initialSize);
		}
	}

	@Override
	public boolean close()
	{
		final Rectangle shell = getShell().getBounds();
		fDialogSettings.put(KEY_WIDTH, shell.width);
		fDialogSettings.put(KEY_HEIGHT, shell.height);
		fDialogSettings.put(KEY_X, shell.x);
		fDialogSettings.put(KEY_Y, shell.y);
		return super.close();
	}

	private TabFolder fTabFolder;
	private final List<IFormatterModifiyTabPage> fTabPages = new ArrayList<IFormatterModifiyTabPage>();

	@Override
	protected Control createDialogArea(Composite parent)
	{
		final Composite composite = (Composite) super.createDialogArea(parent);

		Composite nameComposite = new Composite(composite, SWT.NONE);
		nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameComposite.setLayout(new GridLayout(3, false));

		fProfileNameField = new StringDialogField();
		fProfileNameField.setLabelText(FormatterMessages.FormatterModifyDialog_profileName);
		if (profile != null)
		{
			fProfileNameField.setText(profile.getName());
		}
		fProfileNameField.getLabelControl(nameComposite)
				.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		fProfileNameField.getTextControl(nameComposite).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fProfileNameField.setDialogFieldListener(new IDialogFieldListener()
		{
			public void dialogFieldChanged(DialogField field)
			{
				validate();
			}
		});

		fSaveButton = createButton(nameComposite, SAVE_BUTTON_ID, FormatterMessages.FormatterModifyDialog_export, false);

		fTabFolder = new TabFolder(composite, SWT.NONE);
		fTabFolder.setFont(composite.getFont());
		fTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		addPages();
		controlManager.initialize();
		return composite;
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == SAVE_BUTTON_ID)
		{
			saveButtonPressed();
		}
		else
		{
			super.buttonPressed(buttonId);
		}
	}

	protected abstract void addPages();

	protected void addTabPage(String title, IFormatterModifiyTabPage tabPage)
	{
		final TabItem tabItem = new TabItem(fTabFolder, SWT.NONE);
		applyDialogFont(tabItem.getControl());
		tabItem.setText(title);
		tabItem.setData(tabPage);
		tabItem.setControl(tabPage.createContents(controlManager, fTabFolder));
		fTabPages.add(tabPage);
	}

	public final void statusChanged(IStatus status)
	{
		tabStatus = status;
		validate();
		for (IFormatterModifiyTabPage tabPage : fTabPages)
		{
			tabPage.updatePreview();
		}
	}

	public IFormatterModifyDialogOwner getOwner()
	{
		return dialogOwner;
	}

	public IScriptFormatterFactory getFormatterFactory()
	{
		return formatterFactory;
	}

	@Override
	protected void updateButtonsEnableState(IStatus status)
	{
		super.updateButtonsEnableState(status);
		if (fSaveButton != null && !fSaveButton.isDisposed())
		{
			fSaveButton.setEnabled(!validateProfileName().matches(IStatus.ERROR));
		}
	}

	protected void validate()
	{
		updateStatus(getValidationStatus());
	}

	protected IStatus getValidationStatus()
	{
		IStatus status = doValidate();
		if (tabStatus.getSeverity() < status.getSeverity())
			return status;
		return tabStatus;
	}

	protected IStatus doValidate()
	{
		if (profile == null || manager == null)
		{
			return Status.OK_STATUS;
		}

		Map<String, String> values = getPreferences();
		String name = getProfileName();
		if (name.equals(profile.getName()) && profile.equalsTo(values))
		{
			return StatusInfo.OK_STATUS;
		}

		IStatus status = validateProfileName();
		if (status.matches(IStatus.ERROR))
		{
			return status;
		}

		if (!name.equals(profile.getName()) && manager.containsName(name))
		{
			return new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID,
					FormatterMessages.FormatterModifyDialog_nameExists);
		}

		if (profile.isBuiltInProfile())
		{
			return new Status(IStatus.INFO, FormatterPlugin.PLUGIN_ID,
					FormatterMessages.FormatterModifyDialog_createNewProfile);
		}

		return StatusInfo.OK_STATUS;
	}

	public String getProfileName()
	{
		return fProfileNameField.getText().trim();
	}

	private IStatus validateProfileName()
	{
		final String name = getProfileName();

		if (profile.isBuiltInProfile())
		{
			if (profile.getName().equals(name))
			{
				return new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID,
						FormatterMessages.FormatterModifyDialog_changeBuiltInProfileName);
			}
		}

		if (name.length() == 0)
		{
			return new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID,
					FormatterMessages.FormatterModifyDialog_nameEmpty);
		}

		return StatusInfo.OK_STATUS;
	}

	/*
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		super.okPressed();
		if (!profile.getName().equals(fProfileNameField.getText()))
		{
			IProject project = dialogOwner.getProject();
			profile = manager.rename(project, profile, fProfileNameField.getText());
			manager.setSelected(project, profile);
		}
	}

	private void saveButtonPressed()
	{
		// IProfileStore store = formatterFactory.getProfileStore();
		IProfileStore store = ProfileManager.getInstance().getProfileStore();
		IProfile selected = manager.create(dialogOwner.getProject(), ProfileKind.TEMPORARY,
				fProfileNameField.getText(), getPreferences(), profile.getVersion());

		final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
		dialog.setText(FormatterMessages.FormatterModifyDialog_exportProfile);
		dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$

		final String path = dialog.open();
		if (path == null)
			return;

		final File file = new File(path);
		String message = NLS.bind(FormatterMessages.FormatterModifyDialog_replaceFileQuestion, file.getAbsolutePath());
		if (file.exists()
				&& !MessageDialog.openQuestion(getShell(), FormatterMessages.FormatterModifyDialog_exportProfile,
						message))
		{
			return;
		}

		final Collection<IProfile> profiles = new ArrayList<IProfile>();
		profiles.add(selected);
		try
		{
			// TODO - WRITE ALL PROFILES
			store.writeProfilesToFile(profiles, file);
		}
		catch (CoreException e)
		{
			final String title = FormatterMessages.FormatterModifyDialog_exportProfile;
			message = FormatterMessages.FormatterModifyDialog_exportProblem;
			ExceptionHandler.handle(e, getShell(), title, message);
		}
	}

	public void setPreferences(Map<String, String> prefs)
	{
		preferences.set(prefs);
		final Shell shell = getShell();
		if (shell != null && !shell.isDisposed())
		{
			controlManager.initialize();
		}
	}

	public Map<String, String> getPreferences()
	{
		return preferences.get();
	}

}
