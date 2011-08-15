/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("rawtypes")
public class RetargettableActionAdapterFactory implements IAdapterFactory {
	/**
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IRunToLineTarget.class.equals(adapterType)) {
			return new RunToLineAdapter();
		}
		if (IToggleBreakpointsTarget.class.equals(adapterType)) {
			return new ToggleBreakpointAdapter();
		}
		return null;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { IRunToLineTarget.class, IToggleBreakpointsTarget.class };
	}
}
