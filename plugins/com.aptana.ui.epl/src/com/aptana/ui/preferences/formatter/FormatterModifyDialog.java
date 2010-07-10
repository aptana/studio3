/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.aptana.ui.preferences.formatter.ProfileManager.Profile;

/**
 * 
 *
 */
public abstract class FormatterModifyDialog extends ModifyDialog
{

	String editor;

	/**
	 * @param parentShell
	 * @param profile
	 * @param profileManager
	 * @param profileStore
	 * @param newProfile
	 * @param dialogPreferencesKey
	 * @param lastSavePathKey
	 * @param editor
	 */
	public FormatterModifyDialog(Shell parentShell, Profile profile, ProfileManager profileManager,
			ProfileStore profileStore, boolean newProfile, String dialogPreferencesKey, String lastSavePathKey,
			String editor)
	{
		super(parentShell, profile, profileManager, profileStore, newProfile, dialogPreferencesKey, lastSavePathKey);
		this.editor = editor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.preferences.formatter.ModifyDialog#addPages(java.util.Map)
	 */
	protected abstract void addPages(Map<String, String> values);

}
