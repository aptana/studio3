/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.debug.ui.DebugUITools;

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.JSDebugModel;

/**
 * @author Max Stepanov
 */
public class InspectAction extends WatchAction implements IWatchExpressionListener {
	/**
	 * @see com.aptana.js.debug.ui.internal.actions.WatchAction#createExpression(java.lang.String)
	 */
	protected void createExpression(String expressionText) {
		IAdaptable object = DebugUITools.getDebugContext();
		IDebugElement context = null;
		if (object instanceof IDebugElement) {
			context = (IDebugElement) object;
		} else if (object instanceof ILaunch) {
			context = ((ILaunch) object).getDebugTarget();
		}
		if (context != null) {
			IWatchExpressionDelegate delegate = DebugPlugin.getDefault().getExpressionManager()
					.newWatchExpressionDelegate(context.getModelIdentifier());
			delegate.evaluateExpression(expressionText, context, this);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IWatchExpressionListener#watchEvaluationFinished(org.eclipse.debug.core.model.IWatchExpressionResult)
	 */
	public void watchEvaluationFinished(final IWatchExpressionResult result) {
		if (DebugUiPlugin.getDefault() == null) {
			return;
		}
		if (result.getValue() != null || result.hasErrors()) {
			DebugUiPlugin.getStandardDisplay().syncExec(new Runnable() {
				public void run() {
					displayResult(result);
				}
			});
		}
	}

	/**
	 * displayResult
	 * 
	 * @param result
	 */
	protected void displayResult(IWatchExpressionResult result) {
		DebugPlugin.getDefault().getExpressionManager().addExpression(JSDebugModel.createInspectExpression(result));
		showExpressionsView();
	}
}
