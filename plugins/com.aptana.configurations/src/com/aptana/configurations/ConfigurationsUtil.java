/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.core.logging.IdeLog;

/**
 * A utility class for the configurations.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ConfigurationsUtil
{

	/**
	 * Evaluates an enablement expression.
	 * 
	 * @param expression
	 * @return The evaluation result. In case the expression is <code>null</code>, the evaluation returns
	 *         <code>true</code>.
	 */
	public static boolean evaluateEnablement(Expression expression)
	{
		if (expression == null)
		{
			return true;
		}
		SafeEvaluator evaluator = new SafeEvaluator(expression);
		SafeRunner.run(evaluator);
		return evaluator.getResult();
	}

	/*
	 * Does a safe evaluation of the 'enablement' expression.
	 */
	private static class SafeEvaluator implements ISafeRunnable
	{

		private Expression expression;
		private EvaluationResult result;

		SafeEvaluator(Expression expression)
		{
			this.expression = expression;
		}

		/**
		 * Returns the result of the expression evaluation.
		 */
		public boolean getResult()
		{
			return EvaluationResult.TRUE.equals(result);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.ISafeRunnable#handleException(java.lang.Throwable)
		 */
		public void handleException(Throwable exception)
		{
			IdeLog.logWarning(ConfigurationsPlugin.getDefault(),
					"Error while evaluating a configuration processor element.", exception); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.ISafeRunnable#run()
		 */
		public void run() throws Exception
		{
			result = expression.evaluate(getApplicationContext());
		}

		/**
		 * Returns the application's {@link IEvaluationContext}
		 * 
		 * @return The application evaluation context
		 */
		private IEvaluationContext getApplicationContext()
		{
			IEvaluationService es = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);
			return es == null ? null : es.getCurrentState();
		}
	}
}
