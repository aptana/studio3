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
 *******************************************************************************/
package com.aptana.formatter.ui.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.AlreadyExistsDialog;
import com.aptana.formatter.ui.CreateProfileDialog;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.IProfile;
import com.aptana.formatter.ui.IProfileManager;
import com.aptana.formatter.ui.IProfileStore;
import com.aptana.formatter.ui.IProfileVersioner;
import com.aptana.formatter.ui.IScriptFormatterFactory;
import com.aptana.formatter.ui.ProfileKind;
import com.aptana.formatter.ui.ScriptFormatterManager;
import com.aptana.ui.ContributionExtensionManager;
import com.aptana.ui.IContributedExtension;
import com.aptana.ui.dialogs.PropertyLinkArea;
import com.aptana.ui.preferences.AbstractOptionsBlock;
import com.aptana.ui.preferences.IPreferencesLookupDelegate;
import com.aptana.ui.preferences.IPreferencesSaveDelegate;
import com.aptana.ui.preferences.PreferenceKey;
import com.aptana.ui.util.IStatusChangeListener;
import com.aptana.ui.util.SWTFactory;
import com.aptana.ui.util.SWTUtil;
import com.aptana.ui.util.Util;

public abstract class AbstractFormatterSelectionBlock extends AbstractOptionsBlock
{

	private Composite fComposite;
	private Combo fProfileCombo;
	private Combo fFactoryCombo;
	private Label fFactoryDescription;
	private Button fEditButton;
	private Button fDeleteButton;
	private Button fNewButton;
	private Button fLoadButton;

	private int selectedFactory;
	private IScriptFormatterFactory[] factories;
	private Map<IScriptFormatterFactory, IProfileManager> profileByFactory = new HashMap<IScriptFormatterFactory, IProfileManager>();
	protected SourceViewer fPreviewViewer;

	private static List<IScriptFormatterFactory> TEMP_LIST = new ArrayList<IScriptFormatterFactory>();

	protected abstract IFormatterModifyDialogOwner createDialogOwner();

	/**
	 * Returns the extension manager for the contributed extension.
	 */
	protected abstract ContributionExtensionManager getExtensionManager();

	/**
	 * Returns the message that will be used to create the link to the preference or property page.
	 */
	protected abstract String getPreferenceLinkMessage();

	/**
	 * Returns the preference key that will be used to store the contribution preference.
	 */
	protected abstract PreferenceKey getSavedContributionKey();

	protected abstract void updatePreview();

	protected abstract SourceViewer createPreview(Composite parent);

	public AbstractFormatterSelectionBlock(IStatusChangeListener context, IProject project, PreferenceKey formatterKey,
			String contentType, IWorkbenchPreferenceContainer container)
	{
		super(context, project, collectPreferenceKeys(TEMP_LIST, contentType, formatterKey), container);
		factories = (IScriptFormatterFactory[]) TEMP_LIST.toArray(new IScriptFormatterFactory[TEMP_LIST.size()]);
		TEMP_LIST = new ArrayList<IScriptFormatterFactory>();
	}

	protected IProfileManager getProfileManager()
	{
		return getProfileManager(getSelectedExtension());
	}

	protected IProfileManager getProfileManager(IScriptFormatterFactory factory)
	{
		IProfileManager manager = profileByFactory.get(factory);
		if (manager == null)
		{
			List<IProfile> allProfiles = new ArrayList<IProfile>();
			List<IProfile> buitinProfiles = factory.getBuiltInProfiles();
			if (buitinProfiles != null && buitinProfiles.size() > 0)
			{
				allProfiles.addAll(buitinProfiles);
			}
			else
			{
				FormatterPlugin.logError(NLS.bind(FormatterMessages.AbstractFormatterSelectionBlock_noBuiltInProfiles,
						factory.getId()));
			}
			allProfiles.addAll(factory.getCustomProfiles());
			manager = factory.createProfileManager(allProfiles);
			selectCurrentProfile(factory, manager);
			profileByFactory.put(factory, manager);
		}
		return manager;
	}

	private void selectCurrentProfile(IScriptFormatterFactory factory, IProfileManager manager)
	{
		PreferenceKey activeProfileKey = factory.getActiveProfileKey();
		if (activeProfileKey != null)
		{
			String profileId = getValue(activeProfileKey);
			if (profileId != null && profileId.length() != 0)
			{
				IProfile profile = manager.findProfile(profileId);
				if (profile != null)
				{
					manager.setSelected(profile);
					return;
				}
			}
		}
		Map<String, String> preferences = factory.retrievePreferences(new LoadDelegate());
		if (!preferences.isEmpty())
		{
			for (IProfile profile : manager.getSortedProfiles())
			{
				if (profile.equalsTo(preferences))
				{
					manager.setSelected(profile);
					return;
				}
			}
		}
		String name = getProfileName(manager.getSortedProfiles(),
				FormatterMessages.AbstractFormatterSelectionBlock_activeProfileName);
		IProfile profile = manager.create(ProfileKind.CUSTOM, name, preferences, factory.getId(), factory
				.getProfileVersioner().getCurrentVersion());
		manager.setSelected(profile);
	}

	protected String getProfileName(List<IProfile> profiles, String prefix)
	{
		HashSet<String> names = new HashSet<String>(profiles.size());
		for (IProfile profile : profiles)
		{
			names.add(profile.getName());
		}
		if (!names.contains(prefix))
			return prefix;
		for (int i = 2;; i++)
		{
			String name = prefix + " " + i; //$NON-NLS-1$
			if (!names.contains(name))
				return name;
		}
	}

	protected IProfile findProfile(Map<String, String> preferences, List<IProfile> profiles)
	{
		for (IProfile profile : profiles)
		{
			if (profile.equalsTo(preferences))
				return profile;
		}
		return null;
	}

	@Override
	protected boolean saveValues()
	{
		for (Map.Entry<IScriptFormatterFactory, IProfileManager> entry : profileByFactory.entrySet())
		{
			final IProfileManager manager = entry.getValue();
			if (manager.isDirty())
			{
				entry.getKey().saveCustomProfiles(manager.getSortedProfiles());
				manager.clearDirty();
			}
		}
		return super.saveValues();
	}

	protected void applyPreferences()
	{
		IScriptFormatterFactory factory = getSelectedExtension();
		IProfileManager manager = getProfileManager(factory);
		IProfile profile = manager.getSelected();
		Map<String, String> settings = new HashMap<String, String>();
		if (profile != null)
		{
			settings.putAll(profile.getSettings());
		}
		PreferenceKey activeProfileKey = factory.getActiveProfileKey();
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
		factory.savePreferences(settings, delegate);
		updatePreview();
	}

	protected static PreferenceKey[] collectPreferenceKeys(List<IScriptFormatterFactory> factories, String contentType,
			PreferenceKey formatterKey)
	{
		List<PreferenceKey> result = new ArrayList<PreferenceKey>();
		result.add(formatterKey);
		IContributedExtension[] extensions = ScriptFormatterManager.getInstance().getContributions(contentType);
		for (int i = 0; i < extensions.length; ++i)
		{
			IScriptFormatterFactory factory = (IScriptFormatterFactory) extensions[i];
			factories.add(factory);
			final PreferenceKey[] keys = factory.getPreferenceKeys();
			if (keys != null)
			{
				for (int j = 0; j < keys.length; ++j)
				{
					final PreferenceKey prefKey = keys[j];
					result.add(prefKey);
				}
			}
		}
		return result.toArray(new PreferenceKey[result.size()]);
	}

	// ~ Methods

	@Override
	public final Control createOptionsBlock(Composite parent)
	{
		return createSelectorBlock(parent);
	}

	protected Composite createDescription(Composite parent, IContributedExtension contrib)
	{
		Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL);

		String desc = contrib.getDescription();
		if (desc == null)
		{
			desc = Util.EMPTY_STRING;
		}
		SWTFactory.createLabel(composite, desc, 1);

		String prefPageId = contrib.getPreferencePageId();
		String propPageId = contrib.getPropertyPageId();

		// we're a property page
		if (isProjectPreferencePage() && hasValidId(propPageId))
		{
			new PropertyLinkArea(composite, SWT.NONE, propPageId, fProject, getPreferenceLinkMessage(),
					getPreferenceContainer());
		}

		// we're a preference page
		if (!isProjectPreferencePage() && hasValidId(prefPageId))
		{
			new PreferenceLinkArea(composite, SWT.NONE, prefPageId, getPreferenceLinkMessage(),
					getPreferenceContainer(), null);
		}

		return composite;
	}

	protected Composite createSelectorBlock(Composite parent)
	{
		final int numColumns = 5;

		PixelConverter fPixConv = new PixelConverter(parent);
		fComposite = createComposite(parent, numColumns);

		createFormatterSection(fComposite, numColumns, fPixConv);

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
		fProfileCombo.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				updateSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				updateSelection();
			}
		});

		fEditButton = createButton(group, FormatterMessages.AbstractFormatterSelectionBlock_editProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fEditButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				editButtonPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				editButtonPressed();
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

		fNewButton = createButton(group, FormatterMessages.AbstractFormatterSelectionBlock_newProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fNewButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				createNewProfile();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				createNewProfile();
			}

			protected void createNewProfile()
			{
				IScriptFormatterFactory formatterFactory = getSelectedExtension();
				final CreateProfileDialog p = new CreateProfileDialog(group.getShell(), getProfileManager(),
						formatterFactory.getProfileVersioner());
				if (p.open() != Window.OK)
					return;

				applyPreferences();

				updateComboFromProfiles();
				if (!p.openEditDialog())
					return;
				editButtonPressed();
			}
		});

		fLoadButton = createButton(group, FormatterMessages.AbstractFormatterSelectionBlock_importProfile,
				GridData.HORIZONTAL_ALIGN_END);
		fLoadButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				doImport();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				doImport();
			}

			protected void doImport()
			{
				final FileDialog dialog = new FileDialog(group.getShell(), SWT.OPEN);
				dialog.setText(FormatterMessages.AbstractFormatterSelectionBlock_importProfileLabel);
				dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
				final String path = dialog.open();
				if (path == null)
					return;

				final File file = new File(path);
				IScriptFormatterFactory factory = getSelectedExtension();
				Collection<IProfile> profiles = null;
				IProfileStore store = factory.getProfileStore();
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

				IProfileVersioner versioner = factory.getProfileVersioner();

				if (!versioner.getFormatterId().equals(profile.getFormatterId()))
				{
					final String title = FormatterMessages.AbstractFormatterSelectionBlock_importProfileLabel;
					final String message = NLS.bind(
							FormatterMessages.AbstractFormatterSelectionBlock_notValidFormatter, versioner
									.getFormatterId(), profile.getFormatterId());
					MessageDialog.openError(group.getShell(), title, message);
					return;
				}

				if (profile.getVersion() > versioner.getCurrentVersion())
				{
					final String title = FormatterMessages.AbstractFormatterSelectionBlock_importingProfile;
					final String message = FormatterMessages.AbstractFormatterSelectionBlock_moreRecentVersion;
					MessageDialog.openWarning(group.getShell(), title, message);
				}

				final IProfileManager profileManager = getProfileManager();
				if (profileManager.containsName(profile.getName()))
				{
					final AlreadyExistsDialog aeDialog = new AlreadyExistsDialog(group.getShell(), profile,
							profileManager);
					if (aeDialog.open() != Window.OK)
						return;
				}
				((IProfile.ICustomProfile) profile).setVersion(versioner.getCurrentVersion());
				profileManager.addProfile(profile);
				updateComboFromProfiles();
				applyPreferences();
			}
		});
		createLabel(group, "", 3); //$NON-NLS-1$

		configurePreview(group, numColumns);
		updateButtons();
		applyPreferences();

		return fComposite;
	}

	protected void createFormatterSection(Composite composite, int numColumns, PixelConverter fPixConv)
	{
		String id = getValue(getSavedContributionKey());
		int index = -1;
		for (int i = 0; i < factories.length; i++)
		{
			IScriptFormatterFactory factory = factories[i];
			if (factory.getId().equals(id))
			{
				index = i;
				break;
			}
		}
		if (index == -1 && factories.length != 0)
		{
			index = 0;
			for (int i = 1; i < factories.length; i++)
			{
				if (factories[i].getPriority() > factories[index].getPriority())
				{
					index = i;
				}
			}
			// doSetFactory(index);
		}

		if (factories.length > 1)
		{
			createLabel(composite, FormatterMessages.AbstractFormatterSelectionBlock_formatterLabel, numColumns);
			fFactoryCombo = createProfileCombo(composite, numColumns, fPixConv.convertWidthInCharsToPixels(20));

			for (int i = 0; i < factories.length; i++)
			{
				fFactoryCombo.add(factories[i].getName());
			}

			fFactoryCombo.addSelectionListener(new SelectionListener()
			{

				public void widgetSelected(SelectionEvent e)
				{
					doSetFactory(fFactoryCombo.getSelectionIndex());
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
					doSetFactory(fFactoryCombo.getSelectionIndex());
				}
			});
			fFactoryCombo.select(index);
		}

		fFactoryDescription = createLabel(composite, Util.EMPTY_STRING, numColumns, true);
		doSetFactory(index);
	}

	protected void doSetFactory(int index)
	{
		selectedFactory = index;
		setValue(getSavedContributionKey(), factories[index].getId());
		String desc = getSelectedExtension().getDescription();
		if (desc != null && desc.length() != 0)
		{
			fFactoryDescription.setText(desc);
		}
		else
		{
			fFactoryDescription.setVisible(false);
			GridData data = (GridData) fFactoryDescription.getLayoutData();
			data.exclude = true;
		}
		updateComboFromProfiles();
		applyPreferences();
	}

	protected void configurePreview(Composite composite, int numColumns)
	{
		createLabel(composite, FormatterMessages.AbstractFormatterSelectionBlock_preview, numColumns);
		fPreviewViewer = createPreview(composite);

		final GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.verticalSpan = 7;
		gd.heightHint = 100;
		fPreviewViewer.getControl().setLayoutData(gd);
	}

	protected IScriptFormatterFactory getSelectedExtension()
	{
		return factories[selectedFactory];
	}

	protected final void updateSelection()
	{
		IProfile selected = getProfileManager().getSortedProfiles().get(fProfileCombo.getSelectionIndex());
		getProfileManager().setSelected(selected);
		updateButtons();
		applyPreferences();
		updatePreview();
	}

	protected void editButtonPressed()
	{
		IScriptFormatterFactory factory = getSelectedExtension();
		if (factory != null)
		{
			final IProfileManager manager = getProfileManager();
			final IFormatterModifyDialog dialog = factory.createDialog(createDialogOwner());
			if (dialog != null)
			{
				dialog.setProfileManager(manager);
				IProfile profile = manager.getSelected();
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

	private class LoadDelegate implements IPreferencesLookupDelegate
	{

		public boolean getBoolean(String qualifier, String key)
		{
			return getBooleanValue(new PreferenceKey(qualifier, key));
		}

		public int getInt(String qualifier, String key)
		{
			return getIntValue(new PreferenceKey(qualifier, key));
		}

		public String getString(String qualifier, String key)
		{
			return getValue(new PreferenceKey(qualifier, key));
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

	private boolean hasValidId(String id)
	{
		return (id != null && !"".equals(id)); //$NON-NLS-1$
	}
}
