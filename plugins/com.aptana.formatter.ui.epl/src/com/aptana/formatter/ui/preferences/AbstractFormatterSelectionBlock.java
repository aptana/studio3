/******************************************************************************* 
 * Copyright (c) 2008 xored software, Inc.  
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *     Aptana Inc. - Modified to support multiple languages in the same page (Shalom Gibly)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.formatter.ContributionExtensionManager;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.preferences.IPreferencesSaveDelegate;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.preferences.profile.IProfile;
import com.aptana.formatter.preferences.profile.IProfileManager;
import com.aptana.formatter.preferences.profile.IProfileStore;
import com.aptana.formatter.preferences.profile.IProfileVersioner;
import com.aptana.formatter.preferences.profile.ProfileKind;
import com.aptana.formatter.preferences.profile.ProfileManager;
import com.aptana.formatter.preferences.profile.ProfileStore;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.util.ExceptionHandler;
import com.aptana.formatter.ui.util.IStatusChangeListener;
import com.aptana.formatter.ui.util.SWTFactory;
import com.aptana.formatter.ui.util.SWTUtil;

/**
 * Abstract formatter option block that displays multiple languages and let the user select a profile and a language to
 * format.
 * 
 * @author Yuri Strot, Shalom Gibly <sgibly@aptana.com>
 * @since Aptana Studio 3.0
 */
public abstract class AbstractFormatterSelectionBlock extends AbstractOptionsBlock
{

	private Composite fComposite;
	private Combo fProfileCombo;
	private Button fDeleteButton;
	private Button fNewButton;
	private Button fLoadButton;
	private Button fSaveButton;

	// Have this one static to keep the selection when re-opening the preferences in the same Studio session.
	private static int selectedFormatter;
	private IScriptFormatterFactory[] factories;
	protected SourceViewer fSelectedPreviewViewer;
	private ArrayList<SourceViewer> sourcePreviewViewers;
	private StackLayout previewStackLayout;
	private IProfileManager profileManager;
	private IPropertyChangeListener profileChangeListener;

	private static List<IScriptFormatterFactory> TEMP_LIST = new ArrayList<IScriptFormatterFactory>();

	protected abstract IFormatterModifyDialogOwner createDialogOwner(IScriptFormatterFactory formatter);

	/**
	 * Returns the extension manager for the contributed extension.
	 */
	protected abstract ContributionExtensionManager getExtensionManager();

	/**
	 * Returns the message that will be used to create the link to the preference or property page.
	 */
	protected abstract String getPreferenceLinkMessage();

	protected abstract void updatePreview();

	protected abstract SourceViewer createSourcePreview(Composite parent, IScriptFormatterFactory factory);

	public AbstractFormatterSelectionBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container)
	{
		super(context, project, ProfileManager.collectPreferenceKeys(TEMP_LIST), container);
		Collections.sort(TEMP_LIST, new Comparator<IScriptFormatterFactory>()
		{
			public int compare(IScriptFormatterFactory s1, IScriptFormatterFactory s2)
			{
				return s1.getName().compareToIgnoreCase(s2.getName());
			}
		});
		factories = TEMP_LIST.toArray(new IScriptFormatterFactory[TEMP_LIST.size()]);
		TEMP_LIST = new ArrayList<IScriptFormatterFactory>();
		sourcePreviewViewers = new ArrayList<SourceViewer>();
	}

	protected IProfileManager getProfileManager()
	{
		if (profileManager == null)
		{
			profileManager = ProfileManager.getInstance();
		}
		return profileManager;
	}

	/**
	 * Saves the values for all the profiles.
	 */
	@Override
	protected boolean saveValues()
	{
		IProfileManager manager = getProfileManager();
		PreferenceKey profilesKey = manager.getProfilesKey();
		if (manager.isDirty())
		{
			final IProfileStore store = manager.getProfileStore();
			try
			{
				String value = ((ProfileStore) store).writeProfiles(manager.getSortedProfiles());
				profilesKey.setStoredValue(new InstanceScope(), value);
				manager.clearDirty();
			}
			catch (CoreException e)
			{
				FormatterPlugin.logError(e);
			}
		}
		return super.saveValues();
	}

	/**
	 * Apply the preferences on all the registered formatter factories.
	 */
	protected void applyPreferences()
	{
		IProfileManager manager = getProfileManager();
		IProfile profile = manager.getSelected();
		Map<String, String> settings = new HashMap<String, String>();
		if (profile != null)
		{
			settings.putAll(profile.getSettings());
		}
		PreferenceKey activeProfileKey = profileManager.getActiveProfileKey();
		if (activeProfileKey != null)
		{
			if (profile != null)
			{
				settings.put(activeProfileKey.getName(), profile.getID());
			}
			else
			{
				settings.remove(activeProfileKey.getName());
			}
		}
		IPreferencesSaveDelegate delegate = new SaveDelegate();
		for (IScriptFormatterFactory factory : factories)
		{
			factory.savePreferences(settings, delegate);
		}
		if (selectedFormatter < 0)
		{
			selectedFormatter = 0;
		}
		fSelectedPreviewViewer = sourcePreviewViewers.get(selectedFormatter);
		previewStackLayout.topControl = fSelectedPreviewViewer.getControl();
		updatePreview();
	}

	@Override
	public final Control createOptionsBlock(Composite parent)
	{
		return createSelectorBlock(parent);
	}

	protected Composite createSelectorBlock(Composite parent)
	{
		final int numColumns = 5;

		PixelConverter fPixConv = new PixelConverter(parent);
		fComposite = createComposite(parent, numColumns);

		final Group group = SWTFactory.createGroup(fComposite,
				FormatterMessages.AbstractFormatterSelectionBlock_profilesGroup, numColumns, numColumns,
				GridData.FILL_BOTH);

		Label profileLabel = new Label(group, SWT.NONE);
		profileLabel.setText(FormatterMessages.AbstractFormatterSelectionBlock_activeProfile);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = numColumns;
		profileLabel.setLayoutData(data);

		fProfileCombo = createProfileCombo(group, 3, fPixConv.convertWidthInCharsToPixels(20));
		updateComboFromProfiles();
		fProfileCombo.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				updateSelection();
			}
		});

		fNewButton = createButton(group, FormatterMessages.AbstractFormatterSelectionBlock_newProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fNewButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				createNewProfile(group.getShell());
			}
		});

		fDeleteButton = createButton(group, FormatterMessages.AbstractFormatterSelectionBlock_removeProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fDeleteButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				doDelete();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				doDelete();
			}

			protected void doDelete()
			{
				IProfileManager profileManager = getProfileManager();
				IProfile selected = profileManager.getSelected();
				if (MessageDialog.openQuestion(group.getShell(),
						FormatterMessages.AbstractFormatterSelectionBlock_confirmRemoveLabel, NLS.bind(
								FormatterMessages.AbstractFormatterSelectionBlock_confirmRemoveMessage, selected
										.getName())))
				{
					profileManager.deleteProfile(selected);
					updateComboFromProfiles();
					applyPreferences();
				}
			}
		});

		// add a filler
		createLabel(group, "", 3); //$NON-NLS-1$

		fLoadButton = createButton(group, FormatterMessages.AbstractFormatterSelectionBlock_importProfile,
				GridData.HORIZONTAL_ALIGN_END);
		fLoadButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				doImport(group);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				doImport(group);
			}

		});

		fSaveButton = createButton(group, FormatterMessages.FormatterModifyDialog_export, SWT.PUSH);
		fSaveButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				doExport();
			}
		});

		configurePreview(group, numColumns);
		updateButtons();
		applyPreferences();

		return fComposite;
	}

	/**
	 * Create a new formatter profile. This profile will hold all the formatters that are registered in the system.
	 * 
	 * @param shell
	 */
	protected void createNewProfile(Shell shell)
	{
		final CreateProfileDialog p = new CreateProfileDialog(shell, getProfileManager(), profileManager
				.getProfileVersioner());
		if (p.open() != Window.OK)
		{
			return;
		}
		applyPreferences();
		updateComboFromProfiles();
	}

	protected void doImport(Composite group)
	{
		final FileDialog dialog = new FileDialog(group.getShell(), SWT.OPEN);
		dialog.setText(FormatterMessages.AbstractFormatterSelectionBlock_importProfileLabel);
		dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
		final String path = dialog.open();
		if (path == null)
			return;

		final File file = new File(path);
		// IScriptFormatterFactory factory = getSelectedExtension();
		Collection<IProfile> profiles = null;
		IProfileStore store = profileManager.getProfileStore();
		try
		{
			profiles = store.readProfilesFromFile(file);
		}
		catch (CoreException e)
		{
			FormatterPlugin.logError(FormatterMessages.AbstractFormatterSelectionBlock_notValidProfile, e);
		}
		if (profiles == null || profiles.isEmpty())
			return;

		final IProfile profile = profiles.iterator().next();

		IProfileVersioner versioner = profileManager.getProfileVersioner();

		if (profile.getVersion() > versioner.getCurrentVersion())
		{
			final String title = FormatterMessages.AbstractFormatterSelectionBlock_importingProfile;
			final String message = FormatterMessages.AbstractFormatterSelectionBlock_moreRecentVersion;
			MessageDialog.openWarning(group.getShell(), title, message);
		}

		final IProfileManager profileManager = getProfileManager();
		if (profileManager.containsName(profile.getName()))
		{
			final AlreadyExistsDialog aeDialog = new AlreadyExistsDialog(group.getShell(), profile, profileManager);
			if (aeDialog.open() != Window.OK)
				return;
		}
		((IProfile.ICustomProfile) profile).setVersion(versioner.getCurrentVersion());
		profileManager.addProfile(profile);
		updateComboFromProfiles();
		applyPreferences();
	}

	/**
	 * Export the formatter as an XML.
	 */
	private void doExport()
	{
		IProfileManager manager = getProfileManager();
		IProfileStore store = manager.getProfileStore();
		IProfile activeProfile = manager.getSelected();
		IProfile selected = manager.create(ProfileKind.TEMPORARY, activeProfile.getName(), activeProfile.getSettings(),
				activeProfile.getVersion());

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
			store.writeProfilesToFile(profiles, file);
		}
		catch (CoreException e)
		{
			final String title = FormatterMessages.FormatterModifyDialog_exportProfile;
			message = FormatterMessages.FormatterModifyDialog_exportProblem;
			ExceptionHandler.handle(e, getShell(), title, message);
		}
	}

	protected void configurePreview(Composite composite, int numColumns)
	{
		createLabel(composite, FormatterMessages.AbstractFormatterSelectionBlock_preview, numColumns);
		Composite previewGroup = new Composite(composite, SWT.NONE);
		previewGroup.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		previewGroup.setLayoutData(gd);

		// Adds a SashForm to create left and right areas. The left will hold the list of formatters, while the right
		// will hold a preview pane
		SashForm sashForm = new SashForm(previewGroup, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		final ListViewer listViewer = new ListViewer(sashForm, SWT.SINGLE | SWT.BORDER);
		listViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		// Add the right panel (code preview and buttons)
		Composite rightPanel = new Composite(sashForm, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		rightPanel.setLayout(layout);
		rightPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Buttons panel
		Composite buttons = new Composite(rightPanel, SWT.NONE);
		layout = new GridLayout(2, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		final Button editBt = new Button(buttons, SWT.PUSH);
		editBt.setText(FormatterMessages.AbstractFormatterSelectionBlock_edit);
		GridData editLayoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		editBt.setLayoutData(editLayoutData);
		final Button defaultsBt = new Button(buttons, SWT.PUSH);
		defaultsBt.setText(FormatterMessages.AbstractFormatterSelectionBlock_defaults);
		GridData defaultLauoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		defaultsBt.setLayoutData(defaultLauoutData);

		Point defaultSize = defaultsBt.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point editSize = editBt.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (defaultSize.x > editSize.x)
		{
			editLayoutData.widthHint = defaultSize.x;
		}
		else
		{
			defaultLauoutData.widthHint = editSize.x;
		}
		IProfileManager profileManager = getProfileManager();
		defaultsBt.setEnabled(!profileManager.getSelected().isBuiltInProfile());

		// Previews area
		final Composite previewPane = new Composite(rightPanel, SWT.BORDER);
		GridData previewGridData = new GridData(GridData.FILL_BOTH);
		previewGridData.heightHint = 300;
		previewGridData.widthHint = 450;
		previewPane.setLayoutData(previewGridData);
		previewStackLayout = new StackLayout();
		previewPane.setLayout(previewStackLayout);

		// Set the data into the list
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				IScriptFormatterFactory factory = (IScriptFormatterFactory) element;
				return factory.getName();
			}
		});
		listViewer.setInput(this.factories);
		if (selectedFormatter < 0)
		{
			selectedFormatter = 0;
		}
		listViewer.setSelection(new StructuredSelection(this.factories[selectedFormatter]));
		listViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				// Update the preview
				selectedFormatter = listViewer.getList().getSelectionIndex();
				if (selectedFormatter > -1)
				{
					fSelectedPreviewViewer = sourcePreviewViewers.get(selectedFormatter);
					previewStackLayout.topControl = fSelectedPreviewViewer.getControl();
					previewPane.layout();
					updatePreview();
				}
			}
		});

		for (IScriptFormatterFactory factory : this.factories)
		{
			SourceViewer sourcePreview = createSourcePreview(previewPane, factory);
			sourcePreviewViewers.add(sourcePreview);
		}
		if (selectedFormatter > -1 && sourcePreviewViewers.size() > selectedFormatter)
		{
			fSelectedPreviewViewer = sourcePreviewViewers.get(selectedFormatter);
			previewStackLayout.topControl = fSelectedPreviewViewer.getControl();
			previewPane.layout();
		}

		sashForm.setWeights(new int[] { 1, 3 });

		// Attach the listeners
		profileChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (IProfileManager.PROFILE_SELECTED.equals(event.getProperty()))
				{
					IProfile profile = (IProfile) event.getNewValue();
					fSelectedPreviewViewer = sourcePreviewViewers.get(selectedFormatter);
					previewStackLayout.topControl = fSelectedPreviewViewer.getControl();
					previewPane.layout();
					updatePreview();
					defaultsBt.setEnabled(!profile.isBuiltInProfile());
				}
			}
		};
		profileManager.addPropertyChangeListener(profileChangeListener);

		// Edit
		editBt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				editButtonPressed();
			}
		});

		// Restore Defaults
		defaultsBt.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IScriptFormatterFactory formatter = getSelectedFormatter();
				if (formatter == null)
				{
					return;
				}
				PreferenceKey[] preferenceKeys = formatter.getPreferenceKeys();
				IProfileManager manager = getProfileManager();
				if (!MessageDialog.openQuestion(defaultsBt.getShell(),
						FormatterMessages.AbstractFormatterSelectionBlock_confirmDefaultsTitle, NLS.bind(
								FormatterMessages.AbstractFormatterSelectionBlock_confirmDefaultsMessage, formatter
										.getName())))
				{
					return;
				}
				List<IProfile> builtInProfiles = manager.getBuiltInProfiles();
				String defaultProfileId = manager.getDefaultProfileID();
				IProfile defaultProfile = null;
				for (IProfile profile : builtInProfiles)
				{
					if (profile.getID().equals(defaultProfileId))
					{
						defaultProfile = profile;
						break;
					}
				}
				if (defaultProfile != null)
				{
					Map<String, String> defaultSettings = defaultProfile.getSettings();
					Map<String, String> activeSettings = manager.getSelected().getSettings();
					IScopeContext context = new InstanceScope();
					for (PreferenceKey key : preferenceKeys)
					{
						String name = key.getName();
						if (defaultSettings.containsKey(name))
						{
							String value = defaultSettings.get(name);
							activeSettings.put(name, value);
							key.setStoredValue(context, value);
						}
						else
						{
							activeSettings.remove(name);
						}
					}
					manager.getSelected().setSettings(activeSettings);
					manager.markDirty();
					// Apply the preferences. This will update the preview as well.
					applyPreferences();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.preferences.AbstractOptionsBlock#dispose()
	 */
	public void dispose()
	{
		IProfileManager profileManager = getProfileManager();
		profileManager.removePropertyChangeListener(profileChangeListener);
		super.dispose();
	}

	/**
	 * Returns the {@link IScriptFormatterFactory} for the formatter that is selected in the list of the
	 * formatters-preview.
	 * 
	 * @return The selected {@link IScriptFormatterFactory} (e.g. that one that is currently previewed)
	 */
	protected IScriptFormatterFactory getSelectedFormatter()
	{
		if (selectedFormatter < 0 || selectedFormatter >= factories.length)
		{
			return null;
		}
		return factories[selectedFormatter];
	}

	protected final void updateSelection()
	{
		IProfile selected = getProfileManager().getSortedProfiles().get(fProfileCombo.getSelectionIndex());
		getProfileManager().setSelected(selected);
		updateButtons();
		applyPreferences();
		updatePreview();
	}

	/**
	 * Open the formatter settings dialog for the selected language in the code-formatter main page.
	 */
	protected void editButtonPressed()
	{
		IScriptFormatterFactory factory = getSelectedFormatter();
		if (factory != null)
		{
			final IProfileManager manager = getProfileManager();
			final IFormatterModifyDialog dialog = factory.createDialog(createDialogOwner(factory));
			if (dialog != null)
			{
				IProfile profile = manager.getSelected();
				String title = NLS.bind(FormatterMessages.FormatterModifyDialog_dialogTitle, factory.getName(), profile
						.getName());
				dialog.setProfileManager(manager, title);
				dialog.setPreferences(profile.getSettings());
				if (dialog.open() == Window.OK)
				{
					profile = manager.getSelected();
					updateComboFromProfiles();
					final Map<String, String> newSettings = dialog.getPreferences();
					if (!profile.getSettings().equals(newSettings))
					{
						profile.setSettings(newSettings);
						manager.markDirty();
						applyPreferences();
					}
				}
			}
		}
	}

	protected void updateComboFromProfiles()
	{
		if (fProfileCombo != null && !fProfileCombo.isDisposed())
		{
			fProfileCombo.removeAll();

			List<IProfile> profiles = getProfileManager().getSortedProfiles();
			IProfile selected = getProfileManager().getSelected();
			int selection = 0, index = 0;
			for (IProfile profile : profiles)
			{
				fProfileCombo.add(profile.getName());
				if (profile.equals(selected))
					selection = index;
				index++;
			}
			fProfileCombo.select(selection);
			updateButtons();
		}
	}

	protected void updateButtons()
	{
		if (fDeleteButton != null && !fDeleteButton.isDisposed())
		{
			IProfile selected = getProfileManager().getSelected();
			fDeleteButton.setEnabled(!selected.isBuiltInProfile());
		}
	}

	private class SaveDelegate implements IPreferencesSaveDelegate
	{

		public void setBoolean(String qualifier, String key, boolean value)
		{
			setValue(new PreferenceKey(qualifier, key), value);
		}

		public void setInt(String qualifier, String key, int value)
		{
			setValue(new PreferenceKey(qualifier, key), String.valueOf(value));
		}

		public void setString(String qualifier, String key, String value)
		{
			setValue(new PreferenceKey(qualifier, key), value);
		}

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

	protected static Label createLabel(Composite composite, String text, int numColumns)
	{
		return createLabel(composite, text, numColumns, false);
	}

	protected static Label createLabel(Composite composite, String text, int numColumns, boolean wrap)
	{
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		if (wrap)
		{
			gd.heightHint = new PixelConverter(composite).convertHeightInCharsToPixels(2);
		}

		final Label label = new Label(composite, wrap ? SWT.WRAP : SWT.NONE);
		label.setFont(composite.getFont());
		label.setText(text);
		label.setLayoutData(gd);
		return label;
	}
}
