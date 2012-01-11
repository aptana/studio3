/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     xored software, Inc. - initial API and Implementation (Yuri Strot)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.formatter.preferences.profile.IProfile;
import com.aptana.formatter.preferences.profile.IProfileManager;
import com.aptana.formatter.preferences.profile.IProfileVersioner;
import com.aptana.formatter.preferences.profile.ProfileKind;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.util.StatusInfo;

/**
 * The dialog to create a new profile.
 */
public class CreateProfileDialog extends StatusDialog
{

	private Text fNameText;
	private Combo fProfileCombo;

	private final static StatusInfo fOk = new StatusInfo();
	private final static StatusInfo fEmpty = new StatusInfo(IStatus.ERROR,
			FormatterMessages.CreateProfileDialog_nameEmpty);
	private final static StatusInfo fDuplicate = new StatusInfo(IStatus.ERROR,
			FormatterMessages.CreateProfileDialog_nameExists);

	private final IProfileManager fProfileManager;
	private final List<IProfile> fSortedProfiles;
	private final String[] fSortedNames;

	private IProfile fCreatedProfile;

	private IProfileVersioner versioner;
	private IProject fProject;

	public CreateProfileDialog(Shell parentShell, IProfileManager profileManager, IProfileVersioner versioner,
			IProject fProject)
	{
		super(parentShell);
		fProfileManager = profileManager;
		this.fProject = fProject;
		fSortedProfiles = fProfileManager.getSortedProfiles();
		fSortedNames = fProfileManager.getSortedDisplayNames();
		this.versioner = versioner;
	}

	@Override
	public void create()
	{
		super.create();
		setTitle(FormatterMessages.CreateProfileDialog_newProfile);
	}

	@Override
	public Control createDialogArea(Composite parent)
	{

		final int numColumns = 2;

		GridData gd;

		final GridLayout layout = new GridLayout(numColumns, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(layout);

		// Create "Profile name:" label
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		gd.widthHint = convertWidthInCharsToPixels(60);
		final Label nameLabel = new Label(composite, SWT.WRAP);
		nameLabel.setText(FormatterMessages.CreateProfileDialog_profileName);
		nameLabel.setLayoutData(gd);

		// Create text field to enter name
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		fNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		fNameText.setLayoutData(gd);
		fNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				doValidation();
			}
		});

		// Create "Initialize settings ..." label
		gd = new GridData();
		gd.horizontalSpan = numColumns;
		Label profileLabel = new Label(composite, SWT.WRAP);
		profileLabel.setText(FormatterMessages.CreateProfileDialog_initSettings);
		profileLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		fProfileCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fProfileCombo.setLayoutData(gd);

		// "Open the edit dialog now" checkbox
		gd = new GridData();
		gd.horizontalSpan = numColumns;

		fProfileCombo.setItems(fSortedNames);
		String selectedProfile = fProfileManager.getSelected(fProject).getName();
		if (fProfileCombo.indexOf(selectedProfile) > -1)
		{
			fProfileCombo.setText(selectedProfile);
		}
		else
		{
			fProfileCombo.select(0);
		}
		updateStatus(fEmpty);

		applyDialogFont(composite);

		fNameText.setFocus();

		return composite;
	}

	/**
	 * Validate the current settings
	 */
	protected void doValidation()
	{
		final String name = fNameText.getText().trim();

		if (fProfileManager.containsName(name))
		{
			updateStatus(fDuplicate);
			return;
		}
		if (name.length() == 0)
		{
			updateStatus(fEmpty);
			return;
		}
		updateStatus(fOk);
	}

	@Override
	protected void okPressed()
	{
		if (!getStatus().isOK())
			return;

		final Map<String, String> baseSettings = new HashMap<String, String>((fSortedProfiles.get(fProfileCombo
				.getSelectionIndex())).getSettings());
		final String profileName = fNameText.getText();

		fCreatedProfile = fProfileManager.create(fProject, ProfileKind.CUSTOM, profileName, baseSettings,
				versioner.getCurrentVersion());
		super.okPressed();
	}

	public final IProfile getCreatedProfile()
	{
		return fCreatedProfile;
	}
}
