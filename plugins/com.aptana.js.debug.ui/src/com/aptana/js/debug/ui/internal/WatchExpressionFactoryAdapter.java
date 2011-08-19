/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.js.debug.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter;

import com.aptana.js.debug.core.model.IJSVariable;

/**
 * @author Max Stepanov
 */
public class WatchExpressionFactoryAdapter implements IWatchExpressionFactoryAdapter {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter#
	 * createWatchExpression(org.eclipse.debug.core.model.IVariable)
	 */
	public String createWatchExpression(IVariable variable) throws CoreException {
		if (variable instanceof IJSVariable) {
			return ((IJSVariable) variable).getFullName();
		}
		return variable.getName();
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IWatchExpressionFactoryAdapter.class.equals(adapterType)) {
				return new WatchExpressionFactoryAdapter();
			}
			return null;
		}

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IWatchExpressionFactoryAdapter.class };
		}

	}

}
