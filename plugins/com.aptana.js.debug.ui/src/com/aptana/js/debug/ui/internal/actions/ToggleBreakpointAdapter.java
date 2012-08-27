/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.js.debug.ui.internal.actions;

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
import com.aptana.core.util.EclipseUtil;
import com.aptana.js.debug.core.model.JSDebugModel;

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
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
	{
		return selection instanceof ITextSelection;
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
				// UnifiedEditor unifiedEditor = (UnifiedEditor)
				// part.getAdapter(UnifiedEditor.class);
				// if(unifiedEditor != null) {
				// String language =
				// unifiedEditor.getLanguageAtOffset(((ITextSelection)selection).getOffset());
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
