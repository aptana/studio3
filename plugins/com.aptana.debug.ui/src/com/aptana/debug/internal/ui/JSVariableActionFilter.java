/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.debug.core.DebugException;
import org.eclipse.ui.IActionFilter;

import com.aptana.debug.core.JSDetailFormattersManager;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSVariable;
import com.aptana.js.debug.core.model.JSDebugModel;

/**
 * @author Max Stepanov
 */
public class JSVariableActionFilter implements IActionFilter
{
	private static final Set<String> fgPrimitiveTypes = initPrimitiveTypes();

	private static Set<String> initPrimitiveTypes()
	{
		HashSet<String> set = new HashSet<String>(8);
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
	public boolean testAttribute(Object target, String name, String value)
	{
		try
		{
			if (target instanceof IJSVariable)
			{
				IJSVariable variable = (IJSVariable) target;
				if ("PrimitiveVariableActionFilter".equals(name)) { //$NON-NLS-1$
					if ("isPrimitive".equals(value)) { //$NON-NLS-1$
						return isPrimitiveType(variable.getReferenceTypeName());
					}
					else if ("isValuePrimitive".equals(value)) { //$NON-NLS-1$
						return isPrimitiveType(variable.getValue().getReferenceTypeName());
					}
				}
				else if ("DetailFormatterFilter".equals(name) && "isDefined".equals(value)) { //$NON-NLS-1$ //$NON-NLS-2$
					return JSDetailFormattersManager.getDefault().hasAssociatedDetailFormatter(
							variable.getReferenceTypeName());
				} else if ("WatchpointFilter".equals(name) && "isDefined".equals(value)) { //$NON-NLS-1$ //$NON-NLS-2$
					return JSDebugModel.watchpointExists(variable);
				}
			}
		}
		catch (DebugException e)
		{
			DebugUiPlugin.log(e);
		}
		return false;
	}

	/**
	 * isPrimitiveType
	 * 
	 * @param typeName
	 * @return boolean
	 */
	protected boolean isPrimitiveType(String typeName)
	{
		return fgPrimitiveTypes.contains(typeName);
	}
}
