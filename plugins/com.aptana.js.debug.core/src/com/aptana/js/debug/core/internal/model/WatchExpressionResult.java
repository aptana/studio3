/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;

/**
 * @author Max Stepanov
 */
public class WatchExpressionResult implements IWatchExpressionResult {

	private final IValue value;
	private final String expression;
	private final String[] errorMessages;
	private final DebugException exception;

	/**
	 * WatchExpressionResult
	 * 
	 * @param expression
	 * @param value
	 */
	public WatchExpressionResult(String expression, IValue value) {
		this.expression = expression;
		this.value = value;
		this.errorMessages = null;
		this.exception = null;
	}

	/**
	 * WatchExpressionResult
	 * 
	 * @param expression
	 * @param exception
	 * @param errorMessages
	 */
	public WatchExpressionResult(String expression, DebugException exception, String[] errorMessages) {
		this.expression = expression;
		this.exception = exception;
		this.errorMessages = errorMessages;
		this.value = null;
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getValue()
	 */
	public IValue getValue() {
		return value;
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionResult#hasErrors()
	 */
	public boolean hasErrors() {
		return exception != null || (errorMessages != null && errorMessages.length > 0);
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getErrorMessages()
	 */
	public String[] getErrorMessages() {
		return errorMessages;
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getExpressionText()
	 */
	public String getExpressionText() {
		return expression;
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getException()
	 */
	public DebugException getException() {
		return exception;
	}
}
