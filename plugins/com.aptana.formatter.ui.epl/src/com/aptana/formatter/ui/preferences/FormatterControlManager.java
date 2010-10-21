/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.formatter.preferences.IPreferenceDelegate;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.util.IStatusChangeListener;
import com.aptana.formatter.ui.util.SWTFactory;
import com.aptana.formatter.ui.util.Util;

public class FormatterControlManager implements IFormatterControlManager, IStatusChangeListener
{

	private final IPreferenceDelegate delegate;
	private final ControlBindingManager bindingManager;
	private final IStatusChangeListener listener;

	public FormatterControlManager(IPreferenceDelegate delegate, IStatusChangeListener listener)
	{
		this.delegate = delegate;
		this.bindingManager = new ControlBindingManager(delegate, this);
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#createCheckbox(org.eclipse.swt.widgets.Composite, java.lang.Object, java.lang.String)
	 */
	public Button createCheckbox(Composite parent, Object key, String text)
	{
		return createCheckbox(parent, key, text, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#createCheckbox(org.eclipse.swt.widgets.Composite, java.lang.Object, java.lang.String, int)
	 */
	public Button createCheckbox(Composite parent, Object key, String text, int hspan)
	{
		Button button = SWTFactory.createCheckButton(parent, text, null, false, hspan);
		bindingManager.bindControl(button, key, null);
		return button;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#createCombo(org.eclipse.swt.widgets.Composite, java.lang.Object, java.lang.String, java.lang.String[])
	 */
	public Combo createCombo(Composite parent, Object key, String label, String[] items)
	{
		final Label labelControl = SWTFactory.createLabel(parent, label, 1);
		Combo combo = SWTFactory.createCombo(parent, SWT.READ_ONLY | SWT.BORDER, 1, items);
		bindingManager.bindControl(combo, key);
		registerAssociatedLabel(combo, labelControl);
		return combo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#createCombo(org.eclipse.swt.widgets.Composite, java.lang.Object, java.lang.String, java.lang.String[], java.lang.String[])
	 */
	public Combo createCombo(Composite parent, Object key, String label, String[] itemValues, String[] itemLabels)
	{
		final Label labelControl = SWTFactory.createLabel(parent, label, 1);
		Combo combo = SWTFactory.createCombo(parent, SWT.READ_ONLY | SWT.BORDER, 1, itemLabels);
		bindingManager.bindControl(combo, key, itemValues);
		registerAssociatedLabel(combo, labelControl);
		return combo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#createNumber(org.eclipse.swt.widgets.Composite, java.lang.Object, java.lang.String)
	 */
	public Text createNumber(Composite parent, Object key, String label)
	{
		final Label labelControl = SWTFactory.createLabel(parent, label, 1);
		Text text = SWTFactory.createText(parent, SWT.BORDER, 1, Util.EMPTY_STRING);
		bindingManager.bindControl(text, key, FieldValidators.POSITIVE_NUMBER_VALIDATOR);
		registerAssociatedLabel(text, labelControl);
		return text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#createManagedList(org.eclipse.swt.widgets.Group, java.lang.Object)
	 */
	public Control createManagedList(Group group, Object key)
	{
		AddRemoveList list = new AddRemoveList(group);
		bindingManager.bindControl(list.getList(), key);
		return list.getControl();
	}

	private final Map<Control, Label> labelAssociations = new HashMap<Control, Label>();

	/**
	 * @param control
	 * @param label
	 */
	private void registerAssociatedLabel(Control control, Label label)
	{
		labelAssociations.put(control, label);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#enableControl(org.eclipse.swt.widgets.Control, boolean)
	 */
	public void enableControl(Control control, boolean enabled)
	{
		control.setEnabled(enabled);
		final Label label = labelAssociations.get(control);
		if (label != null)
		{
			label.setEnabled(enabled);
		}
	}

	private final ListenerList initListeners = new ListenerList();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#addInitializeListener(com.aptana.formatter.ui.IFormatterControlManager.IInitializeListener)
	 */
	public void addInitializeListener(IInitializeListener listener)
	{
		initListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IFormatterControlManager#removeInitializeListener(com.aptana.formatter.ui.IFormatterControlManager.IInitializeListener)
	 */
	public void removeInitializeListener(IInitializeListener listener)
	{
		initListeners.remove(listener);
	}

	private boolean initialization;

	public void initialize()
	{
		initialization = true;
		try
		{
			bindingManager.initialize();
			final Object[] listeners = initListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i)
			{
				((IInitializeListener) listeners[i]).initialize();
			}
		}
		finally
		{
			initialization = false;
		}
		listener.statusChanged(bindingManager.getStatus());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.util.IStatusChangeListener#statusChanged(org.eclipse.core.runtime.IStatus)
	 */
	public void statusChanged(IStatus status)
	{
		if (!initialization)
		{
			listener.statusChanged(status);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#getBoolean(java.lang.Object)
	 */
	public boolean getBoolean(Object key)
	{
		return delegate.getBoolean(key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#getString(java.lang.Object)
	 */
	public String getString(Object key)
	{
		return delegate.getString(key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#setBoolean(java.lang.Object, boolean)
	 */
	public void setBoolean(Object key, boolean value)
	{
		delegate.setBoolean(key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.preferences.IPreferenceDelegate#setString(java.lang.Object, java.lang.String)
	 */
	public void setString(Object key, String value)
	{
		delegate.setString(key, value);
	}
}
