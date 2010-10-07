/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.data;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * @since 3.1
 */
public class DimensionMessages {

	private static final String BUNDLE_NAME= "org.eclipse.test.internal.performance.data.DimensionMessages";//$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE= ResourceBundle.getBundle(BUNDLE_NAME);

	private DimensionMessages() {
		// emtpy
	}

	public static String getString(int id) {
	    return getString("dimension." + id); //$NON-NLS-1$
	}

	public static String getDescription(int id) {
	    return getString("dimension.description." + id); //$NON-NLS-1$
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
