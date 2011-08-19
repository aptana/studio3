/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;

import com.aptana.js.debug.core.model.IJSVariable;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("rawtypes")
public class ActionFilterAdapterFactory implements IAdapterFactory {

	/**
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType.isInstance(adaptableObject)) {
			return adaptableObject;
		}
		if (IActionFilter.class.equals(adapterType)) {
			if (adaptableObject instanceof IJSVariable) {
				return new JSVariableActionFilter();
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { IActionFilter.class };
	}
}
