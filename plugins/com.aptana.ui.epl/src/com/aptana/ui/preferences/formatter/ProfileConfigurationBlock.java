/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.PreferencesAccess;
import com.aptana.ui.preferences.formatter.ProfileManager.CustomProfile;
import com.aptana.ui.preferences.formatter.ProfileManager.Profile;
import com.aptana.ui.util.SWTUtil;

/**
 * 
 *
 */
public abstract class ProfileConfigurationBlock
{

	private class StoreUpdater implements Observer
	{

		/**
		 * 
		 */
		public StoreUpdater()
		{
			fProfileManager.addObserver(this);
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg)
		{
			try
			{
				fPreferenceListenerEnabled = false;
				final int value = ((Integer) arg).intValue();
				switch (value)
				{
					case ProfileManager.PROFILE_DELETED_EVENT:
					case ProfileManager.PROFILE_RENAMED_EVENT:
					case ProfileManager.PROFILE_CREATED_EVENT:
					case ProfileManager.SETTINGS_CHANGED_EVENT:
						try
						{
							fProfileStore.writeProfiles(fProfileManager.getSortedProfiles(), fInstanceScope); // update
																												// profile
																												// store
							fProfileManager.commitChanges(fCurrContext);
						}
						catch (CoreException x)
						{
							UIEplPlugin.getDefault().getLog().log(x.getStatus());
						}
						break;
					case ProfileManager.SELECTION_CHANGED_EVENT:
						fProfileManager.commitChanges(fCurrContext);
						break;
				}
			}
			finally
			{
				fPreferenceListenerEnabled = true;
			}
		}
	}

	class ProfileComboController implements Observer, SelectionListener
	{

		private final List<Profile> fSortedProfiles;

		/**
		 * 
		 */
		public ProfileComboController()
		{
			fSortedProfiles = fProfileManager.getSortedProfiles();
			fProfileCombo.addSelectionListener(this);
			fProfileManager.addObserver(this);
			updateProfiles();
			updateSelection();
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			final int index = fProfileCombo.getSelectionIndex();
			fProfileManager.setSelected((Profile) fSortedProfiles.get(index));
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg)
		{
			if (arg == null)
				return;
			final int value = ((Integer) arg).intValue();
			switch (value)
			{
				case ProfileManager.PROFILE_CREATED_EVENT:
				case ProfileManager.PROFILE_DELETED_EVENT:
				case ProfileManager.PROFILE_RENAMED_EVENT:
					updateProfiles();
					updateSelection();
					break;
				case ProfileManager.SELECTION_CHANGED_EVENT:
					updateSelection();
					break;
			}
		}

		private void updateProfiles()
		{
			fProfileCombo.setItems(fProfileManager.getSortedDisplayNames());
		}

		private void updateSelection()
		{
			fProfileCombo.setText(fProfileManager.getSelected().getName());
		}
	}

	class ButtonController implements Observer, SelectionListener
	{

		/**
		 * 
		 */
		public ButtonController()
		{
			fProfileManager.addObserver(this);
			fNewButton.addSelectionListener(this);
			fEditButton.addSelectionListener(this);
			fDeleteButton.addSelectionListener(this);
			fLoadButton.addSelectionListener(this);
			update(fProfileManager, null);
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg)
		{
			Profile selected = ((ProfileManager) o).getSelected();
			final boolean notBuiltIn = !selected.isBuiltInProfile();
			fDeleteButton.setEnabled(notBuiltIn);
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			final Button button = (Button) e.widget;
			if (button == fEditButton)
				modifyButtonPressed();
			else if (button == fDeleteButton)
				deleteButtonPressed();
			else if (button == fNewButton)
				newButtonPressed();
			else if (button == fLoadButton)
				loadButtonPressed();
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		private void modifyButtonPressed()
		{
			final StatusDialog modifyDialog = createModifyDialog(fComposite.getShell(), fProfileManager.getSelected(),
					fProfileManager, fProfileStore, false);
			modifyDialog.open();
		}

		private void deleteButtonPressed()
		{
			if (MessageDialog.openQuestion(fComposite.getShell(),
					FormatterMessages.CodingStyleConfigurationBlock_delete_confirmation_title, MessageFormat.format(
							FormatterMessages.CodingStyleConfigurationBlock_delete_confirmation_question,
							fProfileManager.getSelected().getName())))
			{
				fProfileManager.deleteSelected();
			}
		}

		private void newButtonPressed()
		{
			final CreateProfileDialog p = new CreateProfileDialog(fComposite.getShell(), fProfileManager);
			if (p.open() != Window.OK)
				return;
			if (!p.openEditDialog())
				return;
			final StatusDialog modifyDialog = createModifyDialog(fComposite.getShell(), p.getCreatedProfile(),
					fProfileManager, fProfileStore, true);
			if (modifyDialog != null)
			{
				modifyDialog.open();
			}
		}

		private void loadButtonPressed()
		{
			final FileDialog dialog = new FileDialog(fComposite.getShell(), SWT.OPEN);
			dialog.setText(FormatterMessages.CodingStyleConfigurationBlock_load_profile_dialog_title);
			dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
			final String lastPath = UIEplPlugin.getDefault().getDialogSettings()
					.get(fLastSaveLoadPathKey + ".loadpath"); //$NON-NLS-1$
			if (lastPath != null)
			{
				dialog.setFilterPath(lastPath);
			}
			final String path = dialog.open();
			if (path == null)
				return;
			UIEplPlugin.getDefault().getDialogSettings()
					.put(fLastSaveLoadPathKey + ".loadpath", dialog.getFilterPath()); //$NON-NLS-1$

			final File file = new File(path);
			Collection<Profile> profiles = null;
			try
			{
				profiles = fProfileStore.readProfilesFromFile(file);
			}
			catch (CoreException e)
			{
			}
			if (profiles == null || profiles.isEmpty())
				return;

			final CustomProfile profile = (CustomProfile) profiles.iterator().next();

			// if (!fProfileVersioner.getProfileKind().equals(profile.getKind())) {
			// final String title= FormatterMessages.CodingStyleConfigurationBlock_load_profile_error_title;
			// final String message=
			// StringUtils.format(FormatterMessages.ProfileConfigurationBlock_load_profile_wrong_profile_message, new
			// String[] {fProfileVersioner.getProfileKind(), profile.getKind()});
			// MessageDialog.openError(fComposite.getShell(), title, message);
			// return;
			// }

			// if (profile.getVersion() > fProfileVersioner.getCurrentVersion()) {
			// final String title= FormatterMessages.CodingStyleConfigurationBlock_load_profile_error_too_new_title;
			// final String message= FormatterMessages.CodingStyleConfigurationBlock_load_profile_error_too_new_message;
			// MessageDialog.openWarning(fComposite.getShell(), title, message);
			// }

			if (fProfileManager.containsName(profile.getName()))
			{
				final AlreadyExistsDialog aeDialog = new AlreadyExistsDialog(fComposite.getShell(), profile,
						fProfileManager);
				if (aeDialog.open() != Window.OK)
					return;
			}
			fProfileManager.addProfile(profile);
		}
	}

	/**
	 * The GUI controls
	 */
	private Composite fComposite;
	private Combo fProfileCombo;
	private Button fEditButton;
	private Button fDeleteButton;
	private Button fNewButton;
	private Button fLoadButton;

	private PixelConverter fPixConv;
	/**
	 * The ProfileManager, the model of this page.
	 */
	private final ProfileManager fProfileManager;
	private final IScopeContext fCurrContext;
	private final IScopeContext fInstanceScope;
	private final ProfileStore fProfileStore;

	private final String fLastSaveLoadPathKey;
	private IPreferenceChangeListener fPreferenceListener;
	private final PreferencesAccess fPreferenceAccess;
	private boolean fPreferenceListenerEnabled;
	/**
	 * 
	 */
	protected String pluginId;

	/**
	 * @param project
	 * @param access
	 * @param lastSaveLoadPathKey
	 * @param pluginId
	 */
	public ProfileConfigurationBlock(IProject project, final PreferencesAccess access, String lastSaveLoadPathKey,
			String pluginId)
	{

		fPreferenceAccess = access;
		fLastSaveLoadPathKey = lastSaveLoadPathKey;
		this.pluginId = pluginId;
		fProfileStore = createProfileStore();
		fInstanceScope = access.getInstanceScope();
		if (project != null)
		{
			fCurrContext = access.getProjectScope(project);
		}
		else
		{
			fCurrContext = fInstanceScope;
		}

		List<Profile> profiles = null;
		try
		{
			profiles = fProfileStore.readProfiles(fInstanceScope);
		}
		catch (CoreException e)
		{
			UIEplPlugin.getDefault().getLog().log(e.getStatus());
		}
		if (profiles == null)
		{
			try
			{
				// bug 129427
				profiles = fProfileStore.readProfiles(new DefaultScope());
			}
			catch (CoreException e)
			{
				UIEplPlugin.getDefault().getLog().log(e.getStatus());
			}
		}

		if (profiles == null)
			profiles = new ArrayList<Profile>();

		fProfileManager = createProfileManager(profiles, fCurrContext, access);

		new StoreUpdater();

		fPreferenceListenerEnabled = true;
		fPreferenceListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (fPreferenceListenerEnabled)
				{
					preferenceChanged(event);
				}
			}
		};
		access.getInstanceScope().getNode(pluginId).addPreferenceChangeListener(fPreferenceListener);

	}

	/**
	 * @param event
	 */
	protected void preferenceChanged(PreferenceChangeEvent event)
	{

	}

	/**
	 * @return ProfileStore
	 */
	protected abstract ProfileStore createProfileStore();

	/**
	 * @param profiles
	 * @param context
	 * @param access
	 * @return ProfileManager
	 */
	protected abstract ProfileManager createProfileManager(List<Profile> profiles, IScopeContext context,
			PreferencesAccess access);

	/**
	 * @param shell
	 * @param profile
	 * @param profileManager
	 * @param profileStore
	 * @param newProfile
	 * @return ModifyDialog
	 */
	protected abstract ModifyDialog createModifyDialog(Shell shell, Profile profile, ProfileManager profileManager,
			ProfileStore profileStore, boolean newProfile);

	/**
	 * @param composite
	 * @param numColumns
	 * @param profileManager
	 */
	protected abstract void configurePreview(Composite composite, int numColumns, ProfileManager profileManager);

	private static Button createButton(Composite composite, String text, final int style)
	{
		final Button button = new Button(composite, SWT.PUSH);
		button.setFont(composite.getFont());
		button.setText(text);

		final GridData gd = new GridData(style);
		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		button.setLayoutData(gd);
		return button;
	}

	/**
	 * Create the contents
	 * 
	 * @param parent
	 *            Parent composite
	 * @return Created control
	 */
	public Composite createContents(Composite parent)
	{

		final int numColumns = 5;

		fPixConv = new PixelConverter(parent);
		fComposite = createComposite(parent, numColumns);

		Label profileLabel = new Label(fComposite, SWT.NONE);
		profileLabel.setText(FormatterMessages.ProfileConfigurationBlock_0);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = numColumns;
		profileLabel.setLayoutData(data);

		fProfileCombo = createProfileCombo(fComposite, 3, fPixConv.convertWidthInCharsToPixels(20));
		fEditButton = createButton(fComposite, FormatterMessages.CodingStyleConfigurationBlock_edit_button_desc,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fDeleteButton = createButton(fComposite, FormatterMessages.CodingStyleConfigurationBlock_remove_button_desc,
				GridData.HORIZONTAL_ALIGN_BEGINNING);

		fNewButton = createButton(fComposite, FormatterMessages.CodingStyleConfigurationBlock_new_button_desc,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fLoadButton = createButton(fComposite, FormatterMessages.CodingStyleConfigurationBlock_load_button_desc,
				GridData.HORIZONTAL_ALIGN_END);
		createLabel(fComposite, "", 3); //$NON-NLS-1$

		configurePreview(fComposite, numColumns, fProfileManager);

		new ButtonController();
		new ProfileComboController();

		return fComposite;
	}

	private static Combo createProfileCombo(Composite composite, int span, int widthHint)
	{
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		gd.widthHint = widthHint;

		final Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setFont(composite.getFont());
		combo.setLayoutData(gd);
		return combo;
	}

	/**
	 * @param composite
	 * @param text
	 * @param numColumns
	 * @return Label
	 */
	protected static Label createLabel(Composite composite, String text, int numColumns)
	{
		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.widthHint = 0;

		final Label label = new Label(composite, SWT.WRAP);
		label.setFont(composite.getFont());
		label.setText(text);
		label.setLayoutData(gd);
		return label;
	}

	private Composite createComposite(Composite parent, int numColumns)
	{
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		final GridLayout layout = new GridLayout(numColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		return composite;
	}

	/**
	 * @param project
	 * @return value
	 */
	public final boolean hasProjectSpecificOptions(IProject project)
	{
		if (project != null)
		{
			return fProfileManager.hasProjectSpecificSettings(new ProjectScope(project));
		}
		return false;
	}

	/**
	 * @return boolean
	 */
	public boolean performOk()
	{
		return true;
	}

	/**
	 * 
	 */
	public void performApply()
	{
		try
		{
			fCurrContext.getNode(pluginId).flush();
			fCurrContext.getNode(pluginId).flush();
			if (fCurrContext != fInstanceScope)
			{
				fInstanceScope.getNode(pluginId).flush();
				fInstanceScope.getNode(pluginId).flush();
			}
		}
		catch (BackingStoreException e)
		{

		}
	}

	/**
	 * 
	 */
	public void performDefaults()
	{
		Profile profile = fProfileManager.getDefaultProfile();
		if (profile != null)
		{
			int defaultIndex = fProfileManager.getSortedProfiles().indexOf(profile);
			if (defaultIndex != -1)
			{
				fProfileManager.setSelected(profile);
			}
		}
	}

	/**
	 * 
	 */
	public void dispose()
	{
		if (fPreferenceListener != null)
		{
			fPreferenceAccess.getInstanceScope().getNode(pluginId).removePreferenceChangeListener(fPreferenceListener);
			fPreferenceListener = null;
		}
	}

	/**
	 * @param useProjectSpecificSettings
	 */
	public void enableProjectSpecificSettings(boolean useProjectSpecificSettings)
	{
		if (useProjectSpecificSettings)
		{
			fProfileManager.commitChanges(fCurrContext);
		}
		else
		{
			fProfileManager.clearAllSettings(fCurrContext);
		}
	}

}
