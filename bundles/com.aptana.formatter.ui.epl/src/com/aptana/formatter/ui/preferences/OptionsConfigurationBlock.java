/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.ui.preferences.WorkingCopyManager;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.epl.FormatterUIEplPlugin;
import com.aptana.formatter.ui.util.IStatusChangeListener;

/**
 * Direct port from the jdt ui, this class should not be extended by anyone but the internal ui.
 * 
 * @see com.aptana.ui.preferences.AbstractOptionsBlock
 */
public abstract class OptionsConfigurationBlock
{

	private static final String REBUILD_COUNT_KEY = "preferences_build_requested"; //$NON-NLS-1$

	private final IStatusChangeListener fContext;
	protected final IProject fProject; // project or null
	protected final PreferenceKey[] fAllKeys;
	protected IScopeContext[] fLookupOrder;

	private Shell fShell;

	private final IWorkingCopyManager fManager;
	private IWorkbenchPreferenceContainer fContainer;

	// null when project specific settings are turned off
	private Map<PreferenceKey, String> fDisabledProjectSettings;

	// used to prevent multiple dialogs that ask for a rebuild
	private int fRebuildCount;

	public OptionsConfigurationBlock(IStatusChangeListener context, IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container)
	{
		fContext = context;
		fProject = project;
		fAllKeys = allKeys;
		fContainer = container;
		if (container == null)
		{
			fManager = new WorkingCopyManager();
		}
		else
		{
			fManager = container.getWorkingCopyManager();
		}

		if (fProject != null)
		{
			fLookupOrder = new IScopeContext[] { new ProjectScope(fProject), InstanceScope.INSTANCE,
					DefaultScope.INSTANCE };
		}
		else
		{
			fLookupOrder = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
		}

		testIfOptionsComplete(allKeys);
		if (fProject == null || hasProjectSpecificOptions(fProject))
		{
			fDisabledProjectSettings = null;
		}
		else
		{
			fDisabledProjectSettings = new HashMap<PreferenceKey, String>();
			for (int i = 0; i < allKeys.length; i++)
			{
				PreferenceKey curr = allKeys[i];
				fDisabledProjectSettings.put(curr, curr.getStoredValue(fLookupOrder, false, fManager));
			}
		}

		fRebuildCount = getRebuildCount();
	}

	protected final IWorkbenchPreferenceContainer getPreferenceContainer()
	{
		return fContainer;
	}

	private void testIfOptionsComplete(PreferenceKey[] allKeys)
	{
		for (PreferenceKey key : allKeys)
		{
			validateValuePresenceFor(key);
		}
	}

	protected void validateValuePresenceFor(PreferenceKey key)
	{
		if (key.getStoredValue(fLookupOrder, false, fManager) == null)
		{
			IdeLog.logError(FormatterUIEplPlugin.getDefault(),
					MessageFormat.format("preference option missing: {0} ({1})", key, this.getClass().getName()), //$NON-NLS-1$
					IDebugScopes.DEBUG);
		}
	}

	private int getRebuildCount()
	{
		return fManager.getWorkingCopy(DefaultScope.INSTANCE.getNode(FormatterUIEplPlugin.PLUGIN_ID)).getInt(
				REBUILD_COUNT_KEY, 0);
	}

	private void incrementRebuildCount()
	{
		fRebuildCount++;
		fManager.getWorkingCopy(DefaultScope.INSTANCE.getNode(FormatterUIEplPlugin.PLUGIN_ID)).putInt(
				REBUILD_COUNT_KEY, fRebuildCount);
	}

	public boolean hasProjectSpecificOptions(IProject project)
	{
		if (project != null)
		{
			IScopeContext projectContext = new ProjectScope(project);
			PreferenceKey[] allKeys = fAllKeys;
			for (int i = 0; i < allKeys.length; i++)
			{
				if (allKeys[i].getStoredValue(projectContext, fManager) != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	protected Shell getShell()
	{
		return fShell;
	}

	protected void setShell(Shell shell)
	{
		fShell = shell;
	}

	protected abstract Control createContents(Composite parent);

	protected String getValue(PreferenceKey key)
	{
		if (fDisabledProjectSettings != null)
		{
			return fDisabledProjectSettings.get(key);
		}
		return key.getStoredValue(fLookupOrder, false, fManager);
	}

	protected boolean getBooleanValue(PreferenceKey key)
	{
		return Boolean.valueOf(getValue(key)).booleanValue();
	}

	protected String setValue(PreferenceKey key, String value)
	{
		if (fDisabledProjectSettings != null)
		{
			return fDisabledProjectSettings.put(key, value);
		}
		String oldValue = getValue(key);
		key.setStoredValue(fLookupOrder[0], value, fManager);
		return oldValue;
	}

	protected String setValue(PreferenceKey key, boolean value)
	{
		return setValue(key, String.valueOf(value));
	}

	private boolean getChanges(IScopeContext currContext, List<PreferenceKey> changedSettings)
	{
		// complete when project settings are enabled
		boolean completeSettings = fProject != null && fDisabledProjectSettings == null;
		boolean needsBuild = false;

		/*
		 * XXX: need to rework this once there are options this affects - this can cause an illegal state exception -
		 * probably due to the fact that key binding is different from the jdt implementation
		 */
		for (int i = 0; i < fAllKeys.length; i++)
		{
			try
			{
				PreferenceKey key = fAllKeys[i];
				String oldVal = key.getStoredValue(currContext, null);
				String val = key.getStoredValue(currContext, fManager);
				if (val == null)
				{
					if (oldVal != null)
					{
						changedSettings.add(key);
						needsBuild |= !oldVal.equals(key.getStoredValue(fLookupOrder, true, fManager));
					}
					else if (completeSettings)
					{
						key.setStoredValue(currContext, key.getStoredValue(fLookupOrder, true, fManager), fManager);
						changedSettings.add(key);
						// no build needed
					}
				}
				else if (!val.equals(oldVal))
				{
					changedSettings.add(key);
					needsBuild |= oldVal != null || !val.equals(key.getStoredValue(fLookupOrder, true, fManager));
				}
			}
			catch (IllegalStateException e)
			{
				if (FormatterUIEplPlugin.DEBUG)
				{
					e.printStackTrace();
				}
			}
		}
		return needsBuild;
	}

	public void useProjectSpecificSettings(boolean enable)
	{
		boolean hasProjectSpecificOption = fDisabledProjectSettings == null;
		if (enable != hasProjectSpecificOption && fProject != null)
		{
			if (enable)
			{
				for (int i = 0; i < fAllKeys.length; i++)
				{
					PreferenceKey curr = fAllKeys[i];
					String val = fDisabledProjectSettings.get(curr);
					curr.setStoredValue(fLookupOrder[0], val, fManager);
				}
				fDisabledProjectSettings = null;
			}
			else
			{
				fDisabledProjectSettings = new HashMap<PreferenceKey, String>();
				for (int i = 0; i < fAllKeys.length; i++)
				{
					PreferenceKey curr = fAllKeys[i];
					String oldSetting = curr.getStoredValue(fLookupOrder, false, fManager);
					fDisabledProjectSettings.put(curr, oldSetting);
				}
			}
		}
	}

	public boolean performOk()
	{
		return processChanges(fContainer);
	}

	public boolean performApply()
	{
		// apply directly
		return processChanges(null);
	}

	protected boolean processChanges(IWorkbenchPreferenceContainer container)
	{
		IScopeContext currContext = fLookupOrder[0];

		List<PreferenceKey> changedOptions = new ArrayList<PreferenceKey>();
		boolean needsBuild = getChanges(currContext, changedOptions);
		if (changedOptions.isEmpty())
		{
			return true;
		}
		if (needsBuild)
		{
			int count = getRebuildCount();
			if (count > fRebuildCount)
			{
				needsBuild = false; // build already requested
				fRebuildCount = count;
			}
		}

		boolean doBuild = false;
		if (needsBuild)
		{
			IPreferenceChangeRebuildPrompt prompt = getPreferenceChangeRebuildPrompt(fProject == null, changedOptions);
			if (prompt != null)
			{
				MessageDialog dialog = new MessageDialog(getShell(), prompt.getTitle(), null, prompt.getMessage(),
						MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
								IDialogConstants.CANCEL_LABEL }, 2);
				int res = dialog.open();
				if (res == 0)
				{
					doBuild = true;
				}
				else if (res != 1)
				{
					return false; // cancel pressed
				}
			}
		}
		if (container != null)
		{
			// no need to apply the changes to the original store: will be done
			// by the page container
			if (doBuild)
			{ // post build
				incrementRebuildCount();
				for (Job job : createBuildJobs(fProject))
				{
					container.registerUpdateJob(job);
				}
			}
		}
		else
		{
			// apply changes right away
			try
			{
				fManager.applyChanges();
			}
			catch (BackingStoreException e)
			{
				IdeLog.logError(FormatterUIEplPlugin.getDefault(), e, IDebugScopes.DEBUG);
				return false;
			}
			if (doBuild)
			{
				for (Job job : createBuildJobs(fProject))
				{
					job.schedule();
				}
			}

		}
		return true;
	}

	/**
	 * Create build Jobs for the given project.
	 * 
	 * @param project
	 * @return Job array for building the given project
	 */
	protected abstract Job[] createBuildJobs(IProject project);

	public void performDefaults()
	{
		for (int i = 0; i < fAllKeys.length; i++)
		{
			PreferenceKey curr = fAllKeys[i];
			String origValue = curr.getStoredValue(fLookupOrder, true, fManager);
			setValue(curr, origValue);
		}
	}

	/**
	 * Returns the prompt that should be used in the popup box that indicates a project build needs to occur.
	 * <p>
	 * Default implementation returns <code>null</code>. Clients should override to return context appropriate message.
	 * </p>
	 * 
	 * @param workspaceSettings
	 *            <code>true</code> if workspace settings were changed, <code>false</code> if project settings were
	 *            changed
	 * @param changedOptions
	 *            options that were actually changed. Could be used to test if particular option was changed.
	 * @return
	 */
	protected IPreferenceChangeRebuildPrompt getPreferenceChangeRebuildPrompt(boolean workspaceSettings,
			Collection<PreferenceKey> changedOptions)
	{
		return null;
	}

	public void dispose()
	{
	}

	protected void statusChanged(IStatus status)
	{
		fContext.statusChanged(status);
	}
}