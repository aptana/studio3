/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;

import com.aptana.core.util.EclipseUtil;

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
			EclipseUtil.setSystemForJob(this);
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
					new DebugEvent[] { new DebugEvent(WatchExpressionDelegate.this, DebugEvent.SUSPEND,
							DebugEvent.EVALUATION_IMPLICIT) });
			return Status.OK_STATUS;
		}
	}

	/*
	 * @see org.eclipse.debug.core.model.IWatchExpressionDelegate#evaluateExpression(java.lang.String,
	 * org.eclipse.debug.core.model.IDebugElement, org.eclipse.debug.core.model.IWatchExpressionListener)
	 */
	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) {
		ISuspendResume suspendResume = context.getDebugTarget();
		if (context instanceof ISuspendResume) {
			suspendResume = (ISuspendResume) context;
		}
		if (suspendResume.isSuspended()) {
			IDebugTarget target = context.getDebugTarget();
			if (target instanceof JSDebugTarget) {
				Job job = new EvaluationJob((JSDebugTarget) target, expression, context, listener);
				job.schedule();
				return;
			}
		}
		listener.watchEvaluationFinished(null);
	}
}
