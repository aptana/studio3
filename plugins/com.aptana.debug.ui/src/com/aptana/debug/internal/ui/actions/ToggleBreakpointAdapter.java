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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.core.resources.IUniformResource;
import com.aptana.debug.core.model.JSDebugModel;

/**
 * @author Max Stepanov
 */
public class ToggleBreakpointAdapter implements IToggleBreakpointsTarget
{
	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleLineBreakpoints(final IWorkbenchPart part, final ISelection selection) throws CoreException
	{
		Job job = new Job(Messages.ToggleBreakpointAdapter_ToggleLineBreakpoint)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				if (selection instanceof ITextSelection)
				{
					if (monitor.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					IEditorPart editorPart = (IEditorPart) part;
					IEditorInput editorInput = editorPart.getEditorInput();
					IResource resource = (IResource) editorInput.getAdapter(IFile.class);
					IUniformResource uniformResource = null;
					if (resource == null)
					{
						uniformResource = (IUniformResource) editorInput.getAdapter(IUniformResource.class);
						if (uniformResource == null)
						{
							return Status.CANCEL_STATUS;
						}
					}
					ITextSelection textSelection = (ITextSelection) selection;
					int lineNumber = textSelection.getStartLine() + 1;
					ILineBreakpoint breakpoint;
					if (resource != null)
					{
						breakpoint = JSDebugModel.lineBreakpointExists(resource, lineNumber);
					}
					else
					{
						breakpoint = JSDebugModel.lineBreakpointExists(uniformResource, lineNumber);
					}
					try
					{
						if (breakpoint != null)
						{
							breakpoint.delete();
						}
						else if (canToggleLineBreakpoint(part, selection))
						{
							if (resource != null)
							{
								JSDebugModel.createLineBreakpoint(resource, lineNumber);
							}
							else
							{
								JSDebugModel.createLineBreakpoint(uniformResource, lineNumber);
							}
						}
					}
					catch (CoreException e)
					{
						return e.getStatus();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
	{
		return (selection instanceof ITextSelection);
	}

	private boolean canToggleLineBreakpoint(IWorkbenchPart part, ISelection selection)
	{
		if (selection instanceof ITextSelection)
		{
			IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
			Object resource = editorInput.getAdapter(IFile.class);
			if (resource == null)
			{
				resource = editorInput.getAdapter(IUniformResource.class);
			}
			if (resource != null)
			{
				int lineNumber = ((ITextSelection) selection).getStartLine() + 1;
				if (resource instanceof IFile)
				{
					if (JSDebugModel.lineBreakpointExists((IResource) resource, lineNumber) != null)
					{
						return true;
					}
				}
				else
				{
					if (JSDebugModel.lineBreakpointExists((IUniformResource) resource, lineNumber) != null)
					{
						return true;
					}
				}
				/**
				 * Commented by Ingo request due need to apply some fix to #PHP-15 (It's not possible to set breakpoints
				 * in PHP editor)
				 */
				// UnifiedEditor unifiedEditor = (UnifiedEditor) part.getAdapter(UnifiedEditor.class);
				// if(unifiedEditor != null) {
				// String language = unifiedEditor.getLanguageAtOffset(((ITextSelection)selection).getOffset());
				// if(language != null && !language.equals("text/javascript") //$NON-NLS-1$
				// && !language.equals("text/jscomment")) //$NON-NLS-1$
				// {
				// return false;
				// }
				// }
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
