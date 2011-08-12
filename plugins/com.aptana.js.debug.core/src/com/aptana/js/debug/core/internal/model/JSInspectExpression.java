/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;

import com.aptana.js.debug.core.model.IJSInspectExpression;

/**
 * @author Max Stepanov
 */
public class JSInspectExpression extends PlatformObject implements IJSInspectExpression {

	private IWatchExpressionResult fResult;
	private String fExpression;
	private IValue fValue;
	private IDebugEventSetListener debugEventSetListener;

	/**
	 * JSInspectExpression
	 * 
	 * @param expression
	 * @param value
	 */
	public JSInspectExpression(String expression, IValue value) {
		super();
		fExpression = expression;
		fValue = value;
		DebugPlugin.getDefault().addDebugEventListener(debugEventSetListener = new DebugEventSetListener());
	}

	/**
	 * JSInspectExpression
	 * 
	 * @param result
	 */
	public JSInspectExpression(IWatchExpressionResult result) {
		this(result.getExpressionText(), result.getValue());
		fResult = result;
	}

	/**
	 * @see org.eclipse.debug.core.model.IErrorReportingExpression#hasErrors()
	 */
	public boolean hasErrors() {
		return fResult != null && fResult.hasErrors();
	}

	/**
	 * @see org.eclipse.debug.core.model.IErrorReportingExpression#getErrorMessages()
	 */
	public String[] getErrorMessages() {
		return getErrorMessages(fResult);
	}

	/**
	 * @see org.eclipse.debug.core.model.IExpression#getExpressionText()
	 */
	public String getExpressionText() {
		return fExpression;
	}

	/**
	 * @see org.eclipse.debug.core.model.IExpression#getValue()
	 */
	public IValue getValue() {
		return fValue;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		if (fValue != null) {
			return fValue.getDebugTarget();
		}
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.model.IExpression#dispose()
	 */
	public void dispose() {
		DebugPlugin.getDefault().removeDebugEventListener(debugEventSetListener);
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return getDebugTarget().getModelIdentifier();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}

	/**
	 * getErrorMessages
	 * 
	 * @param result
	 * @return String[]
	 */
	private static String[] getErrorMessages(IWatchExpressionResult result) {
		if (result == null) {
			return new String[0];
		}
		String[] messages = result.getErrorMessages();
		if (messages.length > 0) {
			return messages;
		}
		DebugException exception = result.getException();
		if (exception != null) {
			return new String[] { exception.getMessage() };
		}
		return new String[0];
	}

	private class DebugEventSetListener implements IDebugEventSetListener {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
		 */
		public void handleDebugEvents(DebugEvent[] events) {
			for (DebugEvent event : events) {
				if (event.getKind() == DebugEvent.TERMINATE && event.getSource().equals(getDebugTarget())) {
					DebugPlugin.getDefault().getExpressionManager().removeExpression(JSInspectExpression.this);
				}
			}
		}

	}

}
