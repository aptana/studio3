/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.logging.IdeLog.StatusLevel;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.util.SWTUtils;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class TroubleshootingPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private Scale debugSlider;
	private BooleanFieldEditor toggleComponents;
	private CheckboxTableViewer categoryViewer;

	/**
	 * GeneralPreferencePage
	 */
	public TroubleshootingPreferencePage()
	{
		super(GRID);

		IPreferenceStore preferenceStore = new ScopedPreferenceStore(EclipseUtil.instanceScope(), CorePlugin
				.getDefault().getBundle().getSymbolicName());
		setPreferenceStore(preferenceStore);
		setDescription(Messages.TroubleshootingPreferencePage_TroubleshootingPageHeader);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{

		Composite appearanceComposite = getFieldEditorParent();

		Composite group = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.TroubleshootingPreferencePage_LBL_DebuggingOutputLevel);

		Composite debugComp = new Composite(group, SWT.NONE);
		debugComp.setLayout(GridLayoutFactory.fillDefaults().margins(0, 5).numColumns(3).create());
		debugComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Label less = new Label(debugComp, SWT.LEFT);
		less.setText(Messages.TroubleshootingPreferencePage_LBL_Errors);
		debugSlider = new Scale(debugComp, SWT.HORIZONTAL);
		debugSlider.setIncrement(1);
		debugSlider.setMinimum(1);
		debugSlider.setMaximum(3);
		debugSlider.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		debugSlider.setSelection(parseStatusLevel(getPreferenceStore().getString(
				ICorePreferenceConstants.PREF_DEBUG_LEVEL)));

		Label more = new Label(debugComp, SWT.LEFT);
		more.setText(Messages.TroubleshootingPreferencePage_LBL_All);

		final Label currentValue = new Label(debugComp, SWT.LEFT);
		currentValue.setText(getValueLabel(debugSlider.getSelection()));
		currentValue.setFont(SWTUtils.getDefaultSmallFont());
		currentValue.setLayoutData(GridDataFactory.fillDefaults().span(3, 0).grab(true, true).create());

		debugSlider.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent selectionevent)
			{
				currentValue.setText(getValueLabel(debugSlider.getSelection()));
			}

			public void widgetSelected(SelectionEvent selectionevent)
			{
				currentValue.setText(getValueLabel(debugSlider.getSelection()));
			}
		});

		toggleComponents = new BooleanFieldEditor(ICorePreferenceConstants.PREF_SHOW_SYSTEM_JOBS,
				Messages.TroubleshootingPreferencePage_ShowHiddenProcesses, SWT.DEFAULT, group);
		addField(toggleComponents);

		toggleComponents = new BooleanFieldEditor(ICorePreferenceConstants.PREF_ENABLE_COMPONENT_DEBUGGING,
				Messages.TroubleshootingPreferencePage_DebugSpecificComponents, SWT.DEFAULT, group);
		addField(toggleComponents);

		Composite composite = new Composite(group, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().margins(0, 5).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().span(2, 0).hint(400, 300).grab(true, true).create());

		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(group.getFont());

		categoryViewer = new CheckboxTableViewer(table);
		categoryViewer.getControl().setFont(group.getFont());
		categoryViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryViewer.setContentProvider(new ArrayContentProvider());
		ColumnLabelProvider categoryLabelProvider = new ColumnLabelProvider();
		categoryViewer.setLabelProvider(categoryLabelProvider);
		categoryViewer.setSorter(new ViewerSorter());

		Map<String, String> tItems = EclipseUtil.getTraceableItems();
		Set<String> keys = tItems.keySet();
		String[] items = keys.toArray(new String[keys.size()]);
		Arrays.sort(items);

		categoryViewer.setInput(items);
		categoryViewer.setCheckedElements(EclipseUtil.getCurrentDebuggableComponents());
		categoryViewer.getTable().setEnabled(false);

		if (getPreferenceStore().getBoolean(ICorePreferenceConstants.PREF_ENABLE_COMPONENT_DEBUGGING))
		{
			categoryViewer.getTable().setEnabled(true);
		}
	}

	/**
	 * The field editor preference page implementation of this <code>IPreferencePage</code> (and
	 * <code>IPropertyChangeListener</code>) method intercepts <code>IS_VALID</code> events but passes other events on
	 * to its superclass.
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource().equals(toggleComponents))
		{
			categoryViewer.getTable().setEnabled(Boolean.parseBoolean(event.getNewValue().toString()));
		}
	}

	/**
	 * Returns the logging value names
	 * 
	 * @param selection
	 * @return
	 */
	private String getValueLabel(int selection)
	{
		switch (selection)
		{
			case 0:
				return Messages.TroubleshootingPreferencePage_LBL_NoDebuggingOutput;
			case 1:
				return Messages.TroubleshootingPreferencePage_LBL_OnlyError;
			case 2:
				return Messages.TroubleshootingPreferencePage_LBL_ErrorsAndImportant;
			case 3:
				return Messages.TroubleshootingPreferencePage_LBL_AllDebuggingInformation;
			default:
				return Messages.TroubleshootingPreferencePage_LBL_UnknownLoggingLevel;
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		debugSlider.setSelection(getPreferenceStore().getDefaultInt(ICorePreferenceConstants.PREF_DEBUG_LEVEL));
		categoryViewer.setCheckedElements(ArrayUtil.NO_OBJECTS);
		super.performDefaults();
	}

	/**
	 * Method declared on IPreferencePage. Subclasses should override
	 * 
	 * @return boolean
	 */
	public boolean performOk()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setValue(ICorePreferenceConstants.PREF_DEBUG_LEVEL, getStatusLevel(debugSlider.getSelection()).toString());

		String[] currentOptions = EclipseUtil.getCurrentDebuggableComponents();
		EclipseUtil.setBundleDebugOptions(currentOptions, false);

		List<String> al = new ArrayList<String>();
		Object[] elements = categoryViewer.getCheckedElements();
		for (Object i : elements)
		{
			al.add((String) i);
		}

		String[] newOptions = al.toArray(new String[al.size()]);

		store.setValue(ICorePreferenceConstants.PREF_DEBUG_COMPONENT_LIST, StringUtil.join(",", newOptions)); //$NON-NLS-1$

		EclipseUtil.setBundleDebugOptions(currentOptions, true);
		EclipseUtil.setPlatformDebugging(toggleComponents.getBooleanValue());

		return super.performOk();
	}

	/**
	 * Converts the IStatus level into something we get.
	 * 
	 * @param status
	 * @return
	 */
	private static StatusLevel getStatusLevel(int status)
	{
		switch (status)
		{
			case 3:
			{
				return StatusLevel.INFO;
			}
			case 2:
			{
				return StatusLevel.WARNING;
			}
			case 1:
			{
				return StatusLevel.ERROR;
			}
			default:
			{
				return StatusLevel.OFF;
			}
		}
	}

	/**
	 * Converts the String level into something we get.
	 * 
	 * @param status
	 * @return
	 */
	private static int parseStatusLevel(String status)
	{
		IdeLog.StatusLevel newStatus = IdeLog.StatusLevel.ERROR;
		try
		{
			newStatus = Enum.valueOf(IdeLog.StatusLevel.class, status);
		}
		catch (IllegalArgumentException e)
		{
			// can safely ignore
		}

		switch (newStatus)
		{
			case INFO:
			{
				return 3;
			}
			case WARNING:
			{
				return 2;
			}
			case ERROR:
			{
				return 1;
			}
			default:
			{
				return 0;
			}
		}
	}
}
