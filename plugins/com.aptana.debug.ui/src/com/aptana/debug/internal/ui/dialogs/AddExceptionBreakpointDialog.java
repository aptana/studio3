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
package com.aptana.debug.internal.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSExceptionBreakpoint;
import com.aptana.js.debug.core.model.JSDebugModel;

/**
 * @author Max Stepanov
 */
public class AddExceptionBreakpointDialog extends JSTypeSelectionDialog
{
	/**
	 * AddExceptionBreakpointDialog
	 * 
	 * @param parent
	 */
	public AddExceptionBreakpointDialog(Shell parent)
	{
		super(parent);
		setFilter("*Error*"); //$NON-NLS-1$
	}

	/** TODO: addition exception handling properties (caught/uncaught) */

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		if (createBreakpoint())
		{
			super.okPressed();
		}
	}

	private boolean createBreakpoint()
	{
		final Object[] selected = getSelectedElements();
		if (selected.length != 1)
		{
			return false;
		}

		new Job(Messages.AddExceptionBreakpointDialog_AddJavaScriptExceptionBreakpoint)
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					IResource resource = null;
					if (resource == null)
					{
						resource = ResourcesPlugin.getWorkspace().getRoot();
					}
					IJSExceptionBreakpoint breakpoint = JSDebugModel.createExceptionBreakpoint(resource,
							(String) selected[0]);
					final List<IBreakpoint> list = new ArrayList<IBreakpoint>(1);
					list.add(breakpoint);
					Runnable r = new Runnable()
					{
						public void run()
						{
							IViewPart part = DebugUiPlugin.getActivePage().findView(
									IDebugUIConstants.ID_BREAKPOINT_VIEW);
							if (part instanceof IDebugView)
							{
								Viewer viewer = ((IDebugView) part).getViewer();
								if (viewer instanceof StructuredViewer)
								{
									StructuredViewer sv = (StructuredViewer) viewer;
									sv.setSelection(new StructuredSelection(list), true);
								}
							}
						}
					};
					DebugUiPlugin.getStandardDisplay().asyncExec(r);
					return Status.OK_STATUS;
				}
				catch (CoreException e)
				{
					updateStatus(e.getStatus());
					return Status.CANCEL_STATUS;
				}
			}

		}.schedule();
		return true;
	}
}
