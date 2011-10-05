/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.actions;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class ConnectionActionFilter implements IActionFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean testAttribute(Object target, String name, String value) {
		if (target instanceof IConnectionPoint) {
			if ( "isConnected".equals(name)) { //$NON-NLS-1$
				return ((IConnectionPoint) target).isConnected() == toBoolean(value);
			}
			if ( "canDisconnect".equals(name)) { //$NON-NLS-1$
				return ((IConnectionPoint) target).canDisconnect() == toBoolean(value);
			}
			if ( "isLocal".equals(name)) { //$NON-NLS-1$
				try {
					return ((IConnectionPoint) target).getRoot().getAdapter(File.class) != null;
				} catch (CoreException e) {
					IdeLog.logWarning(IOUIPlugin.getDefault(), e);
				}
			}			
			if ( "isWorkspace".equals(name)) { //$NON-NLS-1$
				try {
					return ((IConnectionPoint) target).getRoot().getAdapter(IResource.class) != null;
				} catch (CoreException e) {
					IdeLog.logWarning(IOUIPlugin.getDefault(), e);
				}
			}			
		} else if (target instanceof IConnectionPointCategory) {
			if ( "isCategory".equals(name)) { //$NON-NLS-1$
				return String.valueOf(value).equals(((IConnectionPointCategory) target).getId());
			}			
		}
		return false;
	}

	private static boolean toBoolean(Object value) {
		if ( value instanceof Boolean ) {
			 return ((Boolean)value).booleanValue();
		} else if (value instanceof String) {
			return Boolean.parseBoolean((String) value);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IActionFilter.class.equals(adapterType)) {
				return new ConnectionActionFilter();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IActionFilter.class };
		}
		
	}

}
