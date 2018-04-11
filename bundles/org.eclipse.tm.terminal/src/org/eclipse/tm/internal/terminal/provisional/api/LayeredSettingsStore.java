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

/**
 * Uses an array of {@link ISettingsStore} to find a value.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as part
 * of a work in progress. There is no guarantee that this API will work or that
 * it will remain the same. Please do not use this API without consulting with
 * the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 */
public class LayeredSettingsStore implements ISettingsStore {

	private final ISettingsStore[] fStores;

	/**
	 * @param stores the stores used to search the values.
	 * {@link #put(String, String)} will put the value in the
	 * first store in the list.
	 */
	public LayeredSettingsStore(ISettingsStore[] stores) {
		fStores=stores;
	}
	/**
	 * Convince constructor for two stores
	 * @param s1 first store
	 * @param s2 second store
	 */
	public LayeredSettingsStore(ISettingsStore s1, ISettingsStore s2) {
		this(new ISettingsStore[]{s1,s2});
	}
	public String get(String key) {
		for (int i = 0; i < fStores.length; i++) {
			String value=fStores[i].get(key);
			if(value!=null)
				return value;
		}
		return null;
	}

	public String get(String key, String defaultValue) {
		String value=get(key);
		if ((value == null) || (value.equals(""))) //$NON-NLS-1$
			return defaultValue;
		return value;
	}

	public void put(String key, String value) {
		fStores[0].put(key,value);
	}

}
