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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.model.IJSVariable;

/**
 * @author Max Stepanov
 */
public class JSDebugVariable extends JSDebugElement implements IJSVariable {
	/**
	 * FLAGS_MODIFIABLE
	 */
	protected static final int FLAGS_MODIFIABLE = 0x01;

	/**
	 * FLAGS_LOCAL
	 */
	protected static final int FLAGS_LOCAL = 0x02;

	/**
	 * FLAGS_ARGUMENT
	 */
	protected static final int FLAGS_ARGUMENT = 0x04;

	/**
	 * FLAGS_EXCEPTION
	 */
	protected static final int FLAGS_EXCEPTION = 0x08;

	/**
	 * FLAGS_CONST
	 */
	protected static final int FLAGS_CONST = 0x10;

	/**
	 * FLAGS_TOPLEVEL
	 */
	protected static final int FLAGS_TOPLEVEL = 0x80;

	private String qualifier;
	private String name;
	private IValue value;

	/**
	 * flags
	 */
	protected int flags;

	/**
	 * JSDebugVariable
	 * 
	 * @param target
	 * @param qualifier
	 * @param name
	 * @param value
	 */
	public JSDebugVariable(IDebugTarget target, String qualifier, String name, IValue value) {
		this(target, qualifier, name, value, 0);
	}

	/**
	 * JSDebugVariable
	 * 
	 * @param target
	 * @param qualifier
	 * @param name
	 * @param flags
	 */
	public JSDebugVariable(IDebugTarget target, String qualifier, String name, int flags) {
		this(target, qualifier, name, null, flags);
	}

	/**
	 * JSDebugVariable
	 * 
	 * @param target
	 * @param qualifier
	 * @param name
	 * @param value
	 * @param flags
	 */
	public JSDebugVariable(IDebugTarget target, String qualifier, String name, IValue value, int flags) {
		super(target);
		this.qualifier = qualifier;
		this.name = name;
		this.value = value;
		this.flags = flags;
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue() throws DebugException {
		return value;
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() throws DebugException {
		return name;
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return value.getReferenceTypeName();
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	public void setValue(String expression) throws DebugException {
		Object result = ((JSDebugTarget) getDebugTarget()).evaluateExpression(expression, this);
		if (result instanceof IValue) {
			result = ((JSDebugTarget) getDebugTarget()).setValue(this, (IValue) result);
			if (result instanceof IValue) {
				value = (IValue) result;
				fireChangeEvent(DebugEvent.CONTENT);
				return;
			}
		}
		if (result instanceof String[]) {
			throwDebugException(((String[]) result)[0]);
		} else {
			throwDebugException(StringUtil.EMPTY);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	public void setValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		value = null;
		throwNotImplemented();
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		return (flags & FLAGS_MODIFIABLE) != 0;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	public boolean verifyValue(String expression) throws DebugException {
		Object result = ((JSDebugTarget) getDebugTarget()).evaluateExpression(expression, this);
		if (result instanceof IValue) {
			return true;
		} else if (result == null) {
			throwDebugException("unknown behaviour"); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		throwNotImplemented();
		return false;
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
	 * @see com.aptana.debug.core.model.IJSVariable#isLocal()
	 */
	public boolean isLocal() throws DebugException {
		return (flags & FLAGS_LOCAL) != 0;
	}

	/**
	 * @see com.aptana.debug.core.model.IJSVariable#isException()
	 */
	public boolean isException() throws DebugException {
		return (flags & FLAGS_EXCEPTION) != 0;
	}

	/**
	 * @see com.aptana.debug.core.model.IJSVariable#isArgument()
	 */
	public boolean isArgument() throws DebugException {
		return (flags & FLAGS_ARGUMENT) != 0;
	}

	/**
	 * @see com.aptana.debug.core.model.IJSVariable#isConst()
	 */
	public boolean isConst() throws DebugException {
		return (flags & FLAGS_CONST) != 0;
	}

	/**
	 * @see com.aptana.debug.core.model.IJSVariable#isTopLevel()
	 */
	public boolean isTopLevel() throws DebugException {
		return (flags & FLAGS_TOPLEVEL) != 0;
	}

	/**
	 * @see com.aptana.debug.core.model.IJSVariable#getFullName()
	 */
	public String getFullName() {
		String fullname = getQualifier();
		int index = fullname.indexOf('.');
		if (index >= 0) {
			fullname = fullname.substring(index + 1);
		}
		return fullname;
	}
}
