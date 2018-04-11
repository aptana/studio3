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
package com.aptana.formatter.preferences;

/**
 * Interface that acts as a bridge to allow preference pages and shared preference/property control blocks to use the
 * {@link ControlBindingManager}.
 * <p>
 * When implementing this interface, the <code>key</code> parameter should be cast to one of two objects, depending upon
 * the preference implementation used - interfacing with the {@link org.eclipse.jface.preference.IPreferenceStore} directly, or using a
 * {@link PreferenceKey}.
 * </p>
 * 
 * @see AbstractOptionsBlock
 * @see ImprovedAbstractConfigurationBlock
 */
public interface IPreferenceDelegate
{

	/**
	 * A delimiter that can be used to store/read lists of items.
	 */
	static final String PREFERECE_DELIMITER = "!"; //$NON-NLS-1$

	/**
	 * Returns the string value for the given preference key
	 */
	String getString(Object key);

	/**
	 * Returns the boolean value for the given preference key
	 */
	boolean getBoolean(Object key);

	/**
	 * Set a boolean preference value
	 */
	void setBoolean(Object key, boolean value);

	/**
	 * Set a string preference value
	 */
	void setString(Object key, String value);
}
