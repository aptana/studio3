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

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.debug.internal.ui.dialogs.HitCountDialog;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;

/**
 * @author Max Stepanov
 */
public class BreakpointHitCountAction implements IObjectActionDelegate
{
	private IStructuredSelection selection;
	
	private IInputValidator inputValidator = new IInputValidator() {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
		 */
		public String isValid(String newText)
		{
			int value = -1;
			try
			{
				value = Integer.valueOf(newText.trim()).intValue();
			}
			catch (NumberFormatException e)
			{
			}
			if (value < 1)
			{
				return Messages.BreakpointHitCountAction_HitCountPositiveInteger;
			}
			return null;
		}
	};


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = null;
		if (selection instanceof IStructuredSelection)
		{
			this.selection = (IStructuredSelection) selection;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@SuppressWarnings("rawtypes")
	public void run(IAction action)
	{
		if (selection != null && !selection.isEmpty())
		{
			for (Iterator i = selection.iterator(); i.hasNext(); )
			{
				IJSLineBreakpoint breakpoint = (IJSLineBreakpoint) i.next();
				try
				{
					int oldValue = breakpoint.getHitCount();
					int newValue = showDialog(breakpoint);
					if (newValue != -1)
					{
						if (oldValue == 0 && newValue == 0)
						{
							break;
						}
						breakpoint.setHitCount(newValue);
					}
				}
				catch (CoreException ce)
				{
					DebugUiPlugin.errorDialog(Messages.BreakpointHitCountAction_ExceptionAttemptingToSetHitCount, ce);
				}
			}
		}
	}

	private int showDialog(IJSLineBreakpoint breakpoint)
	{
		int currentHitCount = 0;
		try
		{
			currentHitCount = breakpoint.getHitCount();
		}
		catch (CoreException e)
		{
			DebugUiPlugin.log(e);
		}
		String initialValue = currentHitCount > 0 ? Integer.toString(currentHitCount) : "1"; //$NON-NLS-1$;
		
		HitCountDialog dlg = new HitCountDialog(DebugUiPlugin.getActiveWorkbenchShell(),
				Messages.BreakpointHitCountAction_SetBreakpointHitCount,
				Messages.BreakpointHitCountAction_EnterNewHitCountForBreakpoint,
				initialValue, inputValidator);
		if (dlg.open() != Window.OK)
		{
			return -1;
		}
		if (dlg.isHitCountEnabled())
		{
			return Integer.parseInt(dlg.getValue().trim());
		}
		return 0;
	}

}
