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
package com.aptana.debug.internal.ui.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.debug.ui.DebugUITools;

import com.aptana.debug.core.model.JSInspectExpression;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class InspectAction extends WatchAction implements IWatchExpressionListener
{
	/**
	 * @see com.aptana.debug.internal.ui.actions.WatchAction#createExpression(java.lang.String)
	 */
	protected void createExpression(String expressionText)
	{
		IAdaptable object = DebugUITools.getDebugContext();
		IDebugElement context = null;
		if (object instanceof IDebugElement)
		{
			context = (IDebugElement) object;
		}
		else if (object instanceof ILaunch)
		{
			context = ((ILaunch) object).getDebugTarget();
		}
		if (context != null)
		{
			IWatchExpressionDelegate delegate = DebugPlugin.getDefault().getExpressionManager()
					.newWatchExpressionDelegate(context.getModelIdentifier());
			delegate.evaluateExpression(expressionText, context, this);
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.IWatchExpressionListener#watchEvaluationFinished(org.eclipse.debug.core.model.IWatchExpressionResult)
	 */
	public void watchEvaluationFinished(final IWatchExpressionResult result)
	{
		if (DebugUiPlugin.getDefault() == null)
		{
			return;
		}
		if (result.getValue() != null || result.hasErrors())
		{
			DebugUiPlugin.getStandardDisplay().syncExec(new Runnable()
			{
				public void run()
				{
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
	protected void displayResult(IWatchExpressionResult result)
	{
		DebugPlugin.getDefault().getExpressionManager().addExpression(new JSInspectExpression(result));
		showExpressionsView();
	}
}
