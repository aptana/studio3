/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.model.IJSVariable;

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

	private final String threadId;
	private final String qualifier;
	private final String name;
	private IValue value;
	/* package */int flags;

	/**
	 * JSDebugVariable
	 * 
	 * @param target
	 * @param qualifier
	 * @param name
	 * @param value
	 */
	public JSDebugVariable(IDebugTarget target, String threadId, String qualifier, String name, IValue value) {
		this(target, threadId, qualifier, name, value, 0);
	}

	/**
	 * JSDebugVariable
	 * 
	 * @param target
	 * @param qualifier
	 * @param name
	 * @param flags
	 */
	public JSDebugVariable(IDebugTarget target, String threadId, String qualifier, String name, int flags) {
		this(target, threadId, qualifier, name, null, flags);
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
	public JSDebugVariable(IDebugTarget target, String threadId, String qualifier, String name, IValue value, int flags) {
		super(target);
		this.threadId = threadId;
		this.qualifier = qualifier;
		this.name = name;
		this.value = value;
		this.flags = flags;
	}

	/*
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue() throws DebugException {
		return value;
	}

	/*
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() throws DebugException {
		return name;
	}

	/*
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return value.getReferenceTypeName();
	}

	/*
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	/*
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

	/*
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	public void setValue(IValue value) throws DebugException {
		throwNotImplemented();
	}

	/*
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		return (flags & FLAGS_MODIFIABLE) != 0;
	}

	/*
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

	/*
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		throwNotImplemented();
		return false;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSVariable#isLocal()
	 */
	public boolean isLocal() throws DebugException {
		return (flags & FLAGS_LOCAL) != 0;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSVariable#isException()
	 */
	public boolean isException() throws DebugException {
		return (flags & FLAGS_EXCEPTION) != 0;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSVariable#isArgument()
	 */
	public boolean isArgument() throws DebugException {
		return (flags & FLAGS_ARGUMENT) != 0;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSVariable#isConst()
	 */
	public boolean isConst() throws DebugException {
		return (flags & FLAGS_CONST) != 0;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSVariable#isTopLevel()
	 */
	public boolean isTopLevel() throws DebugException {
		return (flags & FLAGS_TOPLEVEL) != 0;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSVariable#getFullName()
	 */
	public String getFullName() {
		String fullname = getQualifier();
		int index = fullname.indexOf('.');
		if (index >= 0) {
			fullname = fullname.substring(index + 1);
		}
		return fullname;
	}

	/* package */String getQualifier() {
		return qualifier;
	}

	/* package */String getThreadId() {
		return threadId;
	}

}
