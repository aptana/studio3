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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.formatter.ui.util.IStatusChangeListener;

/**
 * Generic base class for preference pages that may also be used as a property pages to allow project specific
 * configurations.
 * <p>
 * A number of {@link AbstractOptionsBlock} implementations already exist that can be used to provide standard
 * preference options for source parsers, debugging engines, etc.
 * </p>
 */
public abstract class AbstractConfigurationBlockPropertyAndPreferencePage extends PropertyAndPreferencePage
{
	private AbstractOptionsBlock block;

	public AbstractConfigurationBlockPropertyAndPreferencePage()
	{
		setDescription();
		setPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public final void createControl(Composite parent)
	{
		// create the configuration block here so the page works as both types
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		block = createOptionsBlock(getNewStatusChangedListener(), getProject(), container);

		// calls createPreferenceContent(Composite)
		super.createControl(parent);

		String helpId = isProjectPreferencePage() ? getProjectHelpId() : getHelpId();
		if (helpId != null)
		{
			PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), helpId);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public final void dispose()
	{
		if (block != null)
		{
			block.dispose();
		}

		super.dispose();
	}

	/*
	 * /(non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	public final void performApply()
	{
		if (block != null)
		{
			block.performApply();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public final boolean performOk()
	{
		if ((block != null) && !block.performOk())
		{
			return false;
		}

		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.preferences.PropertyAndPreferencePage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public final void setElement(IAdaptable element)
	{
		super.setElement(element);
		// no description for property page.
		setDescription(null);
	}

	/**
	 * Creates the specific set of preference control widgets for the given property page.
	 * <p>
	 * Sub-classes should use an existing <code>AbstractOptionsBlock</code> Implementation if one is available for the
	 * type of preference page they wish to implement. See the individual implementations for details on what type of
	 * preference/property page they may be used to create.
	 * </p>
	 */
	protected abstract AbstractOptionsBlock createOptionsBlock(IStatusChangeListener newStatusChangedListener,
			IProject project, IWorkbenchPreferenceContainer container);

	/**
	 * Returns the preference page help id
	 */
	protected abstract String getHelpId();

	/**
	 * Returns the property page help id
	 */
	protected abstract String getProjectHelpId();

	/**
	 * Sub-class implementations should make a call to {@link #setDescription(String)} to set the description for the
	 * page.
	 */
	protected abstract void setDescription();

	/**
	 * Sub-class implementations should make a call to
	 * {@link #setPreferenceStore(org.eclipse.jface.preference.IPreferenceStore)} to set the preference store for the
	 * page.
	 */
	protected abstract void setPreferenceStore();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.preferences.PropertyAndPreferencePage#createPreferenceContent(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected final Control createPreferenceContent(Composite composite)
	{
		return block.createContents(composite);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.PropertyAndPreferencePage#enableProjectSpecificSettings(boolean)
	 */
	protected final void enableProjectSpecificSettings(boolean useProjectSpecificSettings)
	{
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		if (block != null)
		{
			block.useProjectSpecificSettings(useProjectSpecificSettings);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.preferences.PropertyAndPreferencePage#hasProjectSpecificOptions(org.eclipse.core.resources
	 * .IProject)
	 */
	protected final boolean hasProjectSpecificOptions(IProject project)
	{
		return block.hasProjectSpecificOptions(project);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.PropertyAndPreferencePage#performDefaults()
	 */
	protected final void performDefaults()
	{
		super.performDefaults();
		if (block != null)
		{
			block.performDefaults();
		}
	}
}
