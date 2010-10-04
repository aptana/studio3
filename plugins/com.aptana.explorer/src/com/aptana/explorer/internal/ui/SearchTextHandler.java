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
package com.aptana.explorer.internal.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handles the Cut, Copy, Paste and SelectAll commands in the search in project text field.
 *
 * @author schitale
 */
public class SearchTextHandler extends AbstractHandler implements IExecutableExtension
{
	private String commandId;

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (commandId != null)
		{
			EvaluationContext evaluationContext = (EvaluationContext) event.getApplicationContext();
			Object focusControlObject = evaluationContext.getVariable(ISources.ACTIVE_FOCUS_CONTROL_NAME);
			if (focusControlObject instanceof Text)
			{
				Text focusControl = (Text) focusControlObject;
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				if (commandId.equals(ActionFactory.CUT.create(window).getActionDefinitionId()))
				{
					focusControl.cut();
				}
				else if (commandId.equals(ActionFactory.COPY.create(window).getActionDefinitionId()))
				{
					focusControl.copy();
				}
				else if (commandId.equals(ActionFactory.PASTE.create(window).getActionDefinitionId()))
				{
					focusControl.paste();
				}
				else if (commandId.equals(ActionFactory.SELECT_ALL.create(window).getActionDefinitionId()))
				{
					focusControl.selectAll();
				}
			}
		}
		return null;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		// The data is really just a string (i.e., the commandId).
		commandId = data.toString();
	}

}
