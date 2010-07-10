/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Aaron Luchko, aluchko@redhat.com - 105926 [Formatter] Exporting Unnamed profile fails silently
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.PreferencesAccess;
import com.aptana.ui.preferences.formatter.ProfileManager.Profile;

/**
 * The code formatter preference page.
 */

public abstract class CodeFormatterConfigurationBlock extends ProfileConfigurationBlock
{

	/**
	 * 
	 */
	protected static final String FORMATTER_DIALOG_PREFERENCE_KEY = "formatter_page"; //$NON-NLS-1$

	/**
	 * 
	 */
	protected static final String DIALOGSTORE_LASTSAVELOADPATH = UIEplPlugin.PLUGIN_ID + ".codeformatter"; //$NON-NLS-1$

	String editor;

	/**
	 * Some Java source code used for preview.
	 */
	protected final String PREVIEW;

	private class PreviewController implements Observer
	{

		/**
		 * @param profileManager
		 */
		public PreviewController(ProfileManager profileManager)
		{
			profileManager.addObserver(this);
			fJavaPreview.setWorkingValues(profileManager.getSelected().getSettings());
			fJavaPreview.update();
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg)
		{
			final int value = ((Integer) arg).intValue();
			switch (value)
			{
				case ProfileManager.PROFILE_CREATED_EVENT:
				case ProfileManager.PROFILE_DELETED_EVENT:
				case ProfileManager.SELECTION_CHANGED_EVENT:
				case ProfileManager.SETTINGS_CHANGED_EVENT:
					fJavaPreview.setWorkingValues(((ProfileManager) o).getSelected().getSettings());
					fJavaPreview.update();
			}
		}

	}

	/**
	 * The Preview.
	 */
	private Preview fJavaPreview;

	private IPreferenceStore store;

	/**
	 * Create a new <code>CodeFormatterConfigurationBlock</code>.
	 * 
	 * @param project
	 * @param access
	 * @param editor
	 * @param store
	 * @param preview
	 * @param pluginId
	 */
	public CodeFormatterConfigurationBlock(IProject project, PreferencesAccess access, String editor,
			IPreferenceStore store, String preview, String pluginId)
	{
		super(project, access, DIALOGSTORE_LASTSAVELOADPATH, pluginId);
		this.editor = editor;
		this.store = store;
		this.PREVIEW = preview;

	}

	protected ProfileStore createProfileStore()
	{
		return new FormatterProfileStore(pluginId);
	}

	protected ProfileManager createProfileManager(List<Profile> profiles, IScopeContext context,
			PreferencesAccess access)
	{
		return new FormatterProfileManager(profiles, context, access, pluginId);
	}

	private static boolean firstRun = true;

	protected void configurePreview(Composite composite, int numColumns, ProfileManager profileManager)
	{
		createLabel(composite, FormatterMessages.CodingStyleConfigurationBlock_preview_label_text, numColumns);
		if (firstRun)
		{
			CompilationUnitPreview result = new CompilationUnitPreview(profileManager.getSelected().getSettings(),
					composite, editor, store);
			result.setPreviewText(PREVIEW);
			result.getControl().dispose();
			firstRun = false;
		}
		CompilationUnitPreview result = new CompilationUnitPreview(profileManager.getSelected().getSettings(),
				composite, editor, store);
		result.setPreviewText(PREVIEW);
		fJavaPreview = result;

		final GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.verticalSpan = 7;
		gd.widthHint = 0;
		gd.heightHint = 0;
		fJavaPreview.getControl().setLayoutData(gd);

		new PreviewController(profileManager);
	}

	protected abstract ModifyDialog createModifyDialog(Shell shell, Profile profile, ProfileManager profileManager,
			ProfileStore profileStore, boolean newProfile);

	// {
	// return new FormatterModifyDialog(shell, profile, profileManager,
	// profileStore, newProfile, FORMATTER_DIALOG_PREFERENCE_KEY,
	// DIALOGSTORE_LASTSAVELOADPATH, editor);
	// }
}
