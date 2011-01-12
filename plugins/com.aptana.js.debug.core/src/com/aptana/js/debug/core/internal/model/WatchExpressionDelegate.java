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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;

/**
 * @author Max Stepanov
 */
public class WatchExpressionDelegate implements IWatchExpressionDelegate {
	/**
	 * Evaluation job
	 */
	private final class EvaluationJob extends Job {
		
		private JSDebugTarget target;
		private String expression;
		private IDebugElement context;
		private IWatchExpressionListener listener;

		/**
		 * EvaluationJob
		 * 
		 * @param target
		 * @param expression
		 * @param context
		 * @param listener
		 */
		public EvaluationJob(JSDebugTarget target, String expression, IDebugElement context,
				IWatchExpressionListener listener) {
			super(Messages.WatchExpressionDelegate_ExpressionEvaluation);
			setSystem(true);
			this.target = target;
			this.expression = expression;
			this.context = context;
			this.listener = listener;
		}

		protected IStatus run(IProgressMonitor monitor) {
			IWatchExpressionResult watchResult = null;
			try {
				Object result = ((JSDebugTarget) target).evaluateExpression(expression, context);
				if (result instanceof IValue) {
					watchResult = new WatchExpressionResult(expression, (IValue) result);
				} else if (result instanceof String[]) {
					watchResult = new WatchExpressionResult(expression, null, (String[]) result);
				}
			} catch (DebugException e) {
				watchResult = new WatchExpressionResult(expression, e, null);
			}
			listener.watchEvaluationFinished(watchResult);
			DebugPlugin.getDefault().fireDebugEventSet(
					new DebugEvent[] {
							new DebugEvent(WatchExpressionDelegate.this, DebugEvent.SUSPEND, DebugEvent.EVALUATION_IMPLICIT) });
			return Status.OK_STATUS;
		}
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionDelegate#evaluateExpression(java.lang.String,
	 *      org.eclipse.debug.core.model.IDebugElement,
	 *      org.eclipse.debug.core.model.IWatchExpressionListener)
	 */
	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) {
		IDebugTarget target = context.getDebugTarget();
		if (target.isSuspended()) {
			if (target instanceof JSDebugTarget) {
				Job job = new EvaluationJob((JSDebugTarget) target, expression, context, listener);
				job.schedule();
				return;
			}
		}
		listener.watchEvaluationFinished(null);
	}
}
