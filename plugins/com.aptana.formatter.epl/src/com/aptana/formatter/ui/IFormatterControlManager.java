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
package com.aptana.formatter.ui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.aptana.formatter.preferences.IFieldValidator;
import com.aptana.formatter.preferences.IPreferenceDelegate;

public interface IFormatterControlManager extends IPreferenceDelegate
{
	/**
	 * Default minimum value for {@link Spinner} controls - {@value #DEFAULT_SPINNER_MIN}
	 */
	int DEFAULT_SPINNER_MIN = 0;
	/**
	 * Default maximum value for {@link Spinner} controls - {@value #DEFAULT_SPINNER_MAX}
	 */
	int DEFAULT_SPINNER_MAX = 5;

	public interface IInitializeListener
	{
		void initialize();
	}

	void addInitializeListener(IInitializeListener listener);

	void removeInitializeListener(IInitializeListener listener);

	Button createCheckbox(Composite parent, Object key, String text);

	Button createCheckbox(Composite parent, Object key, String text, int hspan);

	/**
	 * @param parent
	 * @param key
	 * @param label
	 * @param items
	 * @return
	 * @deprecated
	 */
	Combo createCombo(Composite parent, Object key, String label, String[] items);

	Combo createCombo(Composite parent, Object key, String label, String[] itemValues, String[] itemLabels);

	Text createNumber(Composite parent, Object key, String label);

	/**
	 * Create a {@link Text} that holds a number, and has a minimum value validation.
	 * 
	 * @param parent
	 * @param key
	 * @param label
	 * @param minValue
	 * @return
	 */
	Text createNumber(Composite parent, String key, String label, int minValue);

	/**
	 * Creates a {@link Spinner} with a min and max values.
	 * 
	 * @param parent
	 * @param key
	 * @param min
	 * @param max
	 * @param style
	 * @return A {@link Spinner} bound to the given preference key.
	 */
	Spinner createSpinner(Composite parent, Object key, int min, int max, int style);

	/**
	 * Creates a {@link Spinner} with the default min and max values.
	 * 
	 * @param parent
	 * @param key
	 * @return A {@link Spinner} bound to the given preference key.
	 * @see #DEFAULT_SPINNER_MIN
	 * @see #DEFAULT_SPINNER_MAX
	 */
	Spinner createSpinner(Composite parent, Object key);

	/**
	 * Creates a {@link Spinner} with the default min and max values.
	 * 
	 * @param parent
	 * @param key
	 * @param style
	 * @return A {@link Spinner} bound to the given preference key.
	 * @see #DEFAULT_SPINNER_MIN
	 * @see #DEFAULT_SPINNER_MAX
	 */
	Spinner createSpinner(Composite parent, Object key, int style);

	Text createText(Composite parent, Object key, String label);

	Text createText(Composite parent, Object key, String label, IFieldValidator validator);

	void enableControl(Control control, boolean enabled);

	/**
	 * Creates a list of items that are manageable through 'Add' and 'Remove' buttons on its side bar.<br>
	 * [Aptana Addition]
	 * 
	 * @param group
	 * @param key
	 *            The preferences key that the values will be saved as a comma-separated strings.
	 */
	Control createManagedList(Group group, Object key);
}
