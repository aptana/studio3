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

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.formatter.preferences.IFieldValidator;
import com.aptana.formatter.preferences.IPreferenceDelegate;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.dialogs.PropToPrefLinkArea;
import com.aptana.formatter.ui.util.IStatusChangeListener;

public abstract class AbstractOptionsBlock extends OptionsConfigurationBlock implements IPreferenceDelegate
{

	private ControlBindingManager bindManager;

	public AbstractOptionsBlock(IStatusChangeListener context, IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container)
	{
		super(context, project, allKeys, container);

		this.bindManager = new ControlBindingManager(this, context);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.preferences.OptionsConfigurationBlock#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createContents(Composite parent)
	{
		setShell(parent.getShell());
		Control control = createOptionsBlock(parent);
		initialize();

		return control;
	}

	protected void initialize()
	{
		bindManager.initialize();
	}

	protected abstract Control createOptionsBlock(Composite parent);

	protected final void bindControl(Button button, PreferenceKey key, Control[] dependencies)
	{
		bindManager.bindControl(button, key, dependencies);
	}

	protected final void bindControl(Text textBox, PreferenceKey key, IFieldValidator validator)
	{
		bindManager.bindControl(textBox, key, validator);
	}

	/**
	 * Binds the specified combobox. The result of {@link Combo#getItem(int)} will be used as value.
	 */
	protected final void bindControl(Combo combo, PreferenceKey key)
	{
		bindManager.bindControl(combo, key);
	}

	/**
	 * Binds the specified combobox. Values are specified via the <code>itemValues</code> array.
	 */
	protected final void bindControl(Combo combo, PreferenceKey key, String[] itemValues)
	{
		bindManager.bindControl(combo, key, itemValues);
	}

	protected final boolean isProjectPreferencePage()
	{
		return fProject != null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.OptionsConfigurationBlock#performDefaults()
	 */
	public void performDefaults()
	{
		super.performDefaults();
		bindManager.initialize();
	}

	protected boolean saveValues()
	{
		return true;
	}

	/*
	 * Override performOk() as public API.
	 * @see OptionsConfigurationBlock#performOk()
	 */
	public boolean performOk()
	{
		return saveValues() && super.performOk();
	}

	/*
	 * Override performApply() as public API.
	 * @see OptionsConfigurationBlock#performApply()
	 */
	public boolean performApply()
	{
		return saveValues() && super.performApply();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#getBoolean(java.lang.Object)
	 */
	public final boolean getBoolean(Object key)
	{
		return getBooleanValue((PreferenceKey) key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#getString(java.lang.Object)
	 */
	public final String getString(Object key)
	{
		return getValue((PreferenceKey) key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#setBoolean(java.lang.Object, boolean)
	 */
	public final void setBoolean(Object key, boolean value)
	{
		super.setValue((PreferenceKey) key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#setString(java.lang.Object, java.lang.String)
	 */
	public final void setString(Object key, String value)
	{
		setValue((PreferenceKey) key, value);
	}

	protected final IProject getProject()
	{
		return fProject;
	}

	protected final void updateStatus(IStatus status)
	{
		bindManager.updateStatus(status);
	}

	protected void createPrefLink(Composite composite, String message, final String prefPageId, final Object data)
	{
		PreferenceLinkArea area = new PreferenceLinkArea(composite, SWT.NONE, prefPageId, message,
				getPreferenceContainer(), data);

		area.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	protected void createPropToPrefLink(Composite composite, String message, final String prefPageId, final Object data)
	{
		PropToPrefLinkArea area = new PropToPrefLinkArea(composite, SWT.NONE, prefPageId, message, getShell(), data);

		area.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	/*
	 * Override getShell() method as public API.
	 * @see OptionsConfigurationBlock#getShell()
	 */
	protected Shell getShell()
	{
		return super.getShell();
	}

	/*
	 * Override dispose() method as public API.
	 * @see OptionsConfigurationBlock#dispose()
	 */
	public void dispose()
	{
		super.dispose();
	}

	/*
	 * Override statusChanged() as public API.
	 * @see OptionsConfigurationBlock#statusChanged(IStatus)
	 */
	protected void statusChanged(IStatus status)
	{
		super.statusChanged(status);
	}

	/*
	 * Override getPreferenceChangeRebuildPrompt() as public API
	 * @see OptionsConfigurationBlock#getPreferenceChangeRebuildPrompt(boolean, java.util.Collection)
	 */
	protected IPreferenceChangeRebuildPrompt getPreferenceChangeRebuildPrompt(boolean workspaceSettings,
			Collection<PreferenceKey> changedOptions)
	{
		return super.getPreferenceChangeRebuildPrompt(workspaceSettings, changedOptions);
	}

}
