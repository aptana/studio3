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
import org.eclipse.swt.widgets.Text;

import com.aptana.formatter.preferences.IPreferenceDelegate;

public interface IFormatterControlManager extends IPreferenceDelegate
{

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
