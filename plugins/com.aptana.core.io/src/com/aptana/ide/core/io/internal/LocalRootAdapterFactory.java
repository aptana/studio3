/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.internal;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdapterFactory;

import com.aptana.ide.core.io.LocalRoot;

@SuppressWarnings("rawtypes")
public class LocalRootAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IFileStore.class.equals(adapterType)
				&& adaptableObject instanceof LocalRoot) {
			return ((LocalRoot) adaptableObject).getAdapter(IFileStore.class);
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { LocalRoot.class };
	}
}
