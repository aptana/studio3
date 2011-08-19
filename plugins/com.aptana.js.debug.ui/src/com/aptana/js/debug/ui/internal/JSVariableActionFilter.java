/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.debug.core.DebugException;
import org.eclipse.ui.IActionFilter;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.model.IJSVariable;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public class JSVariableActionFilter implements IActionFilter {
	private static final Set<String> fgPrimitiveTypes = initPrimitiveTypes();

	private static Set<String> initPrimitiveTypes() {
		Set<String> set = new HashSet<String>(8);
		set.add("integer"); //$NON-NLS-1$
		set.add("float"); //$NON-NLS-1$
		set.add("boolean"); //$NON-NLS-1$
		set.add("String"); //$NON-NLS-1$
		set.add("Number"); //$NON-NLS-1$
		set.add("Boolean"); //$NON-NLS-1$
		set.add("null"); //$NON-NLS-1$
		return set;
	}

	/**
	 * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean testAttribute(Object target, String name, String value) {
		try {
			if (target instanceof IJSVariable) {
				IJSVariable variable = (IJSVariable) target;
				if ("PrimitiveVariableActionFilter".equals(name)) { //$NON-NLS-1$
					if ("isPrimitive".equals(value)) { //$NON-NLS-1$
						return isPrimitiveType(variable.getReferenceTypeName());
					} else if ("isValuePrimitive".equals(value)) { //$NON-NLS-1$
						return isPrimitiveType(variable.getValue().getReferenceTypeName());
					}
				} else if ("DetailFormatterFilter".equals(name) && "isDefined".equals(value)) { //$NON-NLS-1$ //$NON-NLS-2$
					return JSDebugPlugin.getDefault().getDebugOptionsManager()
							.hasAssociatedDetailFormatter(variable.getReferenceTypeName());
				} else if ("WatchpointFilter".equals(name) && "isDefined".equals(value)) { //$NON-NLS-1$ //$NON-NLS-2$
					return JSDebugModel.watchpointExists(variable);
				}
			}
		} catch (DebugException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		return false;
	}

	/**
	 * isPrimitiveType
	 * 
	 * @param typeName
	 * @return boolean
	 */
	protected boolean isPrimitiveType(String typeName) {
		return fgPrimitiveTypes.contains(typeName);
	}
}
