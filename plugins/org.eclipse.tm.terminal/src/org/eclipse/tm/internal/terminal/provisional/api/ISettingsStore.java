/*******************************************************************************
 * Copyright (c) 2006, 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

/**
 * A simple interface to a store to persist the state of a connection. 
 * 
 * @author Michael Scharf
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API without
 * consulting with the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 */
public interface ISettingsStore {
	/**
	 * @param key alpha numeric key, may contain dots (.)
	 * @return value
	 */
	String get(String key);

	/**
	 * @param key alpha numeric key, may contain dots (.)
	 * @param defaultValue
	 * @return the value or the default
	 */
	String get(String key, String defaultValue);

	/**
	 * Save a string value
	 * @param key alpha numeric key, may contain dots (.)
	 * @param value
	 */
	void put(String key, String value);
}
