/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

import org.eclipse.core.runtime.Preferences;

/**
 * A preference based settings store.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as part
 * of a work in progress. There is no guarantee that this API will work or that
 * it will remain the same. Please do not use this API without consulting with
 * the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 */
public class PreferenceSettingStore implements ISettingsStore {
	private final String fPrefix;
	private final Preferences fPreferences;

	/**
	 * Creates a ISettingStore that uses the preferences as backend.
	 *
	 * @param preferences the backed.
	 * @param prefix a string that is prepended to the key
	 */
	public PreferenceSettingStore(Preferences preferences, String prefix) {
		fPreferences=preferences;
		fPrefix=prefix;
	}
	public String get(String key) {
		return fPreferences.getString(makeKey(key));
	}
	public String get(String key, String defaultValue) {
		String value=get(key);
		if ((value == null) || (value.equals(""))) //$NON-NLS-1$
			return defaultValue;

		return value;
	}

	public void put(String key, String value) {
		fPreferences.setValue(makeKey(key), value);
	}
	/**
	 * @param key
	 * @return the full path in the preferences
	 */
	private String makeKey(String key) {
		return fPrefix+key;
	}
}
