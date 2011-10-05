/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.text.MessageFormat;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Max Stepanov
 */
public class JSDebugValue extends JSDebugElement implements IValue {

	private final String threadId;
	private final String qualifier;
	private final String typeName;
	private boolean hasVariables;
	private final String valueString;
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
	public JSDebugValue(IDebugTarget target, String threadId, String qualifier, String typeName, boolean hasVariables,
			String valueString) {
		super(target);
		this.threadId = threadId;
		this.qualifier = qualifier;
		this.typeName = typeName;
		this.hasVariables = hasVariables;
		this.valueString = valueString;
	}

	/*
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return typeName;
	}

	/*
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException {
		return valueString != null ? valueString : MessageFormat.format("'''{'{0}}", typeName); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return true;
	}

	/*
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		if (hasVariables) {
			getVariables0();
		}
		return variables != null ? variables : new IVariable[0];
	}

	/*
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		return hasVariables;
	}

	/* package */String getQualifier() {
		return qualifier;
	}

	/* package */String getThreadId() {
		return threadId;
	}

	private void getVariables0() throws DebugException {
		if (variables == null) {
			variables = ((JSDebugTarget) getDebugTarget()).loadVariables(threadId, qualifier);
			hasVariables = variables != null && variables.length > 0;
		}
	}
}
