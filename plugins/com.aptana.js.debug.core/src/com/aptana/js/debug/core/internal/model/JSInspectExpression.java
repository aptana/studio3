/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

		/* (non-Javadoc)
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
