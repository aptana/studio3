/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;

/**
 * @author Max Stepanov
 */
public class StartPageActionFilter implements IActionFilter {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean testAttribute(Object target, String name, String value) {
		if (target instanceof IFile) {
			if ("StartPageFilter".equals(name)) { //$NON-NLS-1$
				if ("isStartPage".equals(value)) { //$NON-NLS-1$
					return StartPageManager.getDefault().isStartPage((IResource) target);
				}
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (adaptableObject instanceof IFile) {
				return new StartPageActionFilter();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IActionFilter.class };
		}

	}

}
