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
package com.aptana.debug.internal.core.model;

import java.text.MessageFormat;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Max Stepanov
 */
public class JSDebugValue extends JSDebugElement implements IValue {
	private String qualifier;
	private String typeName;
	private boolean hasVariables;
	private String valueString;
	private IVariable[] variables;

	/**
	 * JSDebugValue
	 * 
	 * @param target
	 * @param qualifier
	 * @param typeName
	 * @param hasVariables
	 * @param valueString
	 */
	public JSDebugValue(IDebugTarget target, String qualifier, String typeName, boolean hasVariables, String valueString) {
		super(target);
		this.qualifier = qualifier;
		this.typeName = typeName;
		this.hasVariables = hasVariables;
		this.valueString = valueString;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return typeName;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException {
		return valueString != null ? valueString : MessageFormat.format("'''{'{0}}", typeName); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return true;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		if (hasVariables) {
			getVariables0();
		}
		return variables != null ? variables : new IVariable[0];
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		return hasVariables;
	}

	/**
	 * getQualifier
	 * 
	 * @return String
	 */
	protected String getQualifier() {
		return qualifier;
	}

	/**
	 * getVariables0
	 * 
	 * @throws DebugException
	 */
	private void getVariables0() throws DebugException {
		if (variables != null) {
			return;
		}
		variables = ((JSDebugTarget) getDebugTarget()).loadVariables(qualifier);
		hasVariables = variables != null && variables.length > 0;
	}
}
