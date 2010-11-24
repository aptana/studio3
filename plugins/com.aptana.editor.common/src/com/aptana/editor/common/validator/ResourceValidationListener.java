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
package com.aptana.editor.common.validator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * A validation listener to handle both workspace and external resources.
 * 
 * @author Ingo Muschenetz
 * @author Michael Xia
 */
public class ResourceValidationListener implements IValidationListener
{

	public ResourceValidationListener()
	{
	}

	public void validationChanged(final Object source, final IValidationItem[] items)
	{
		// Performance fix: schedules the error handling as a single workspace update so that we don't trigger a
		// bunch of resource updated events while problem markers are being added to the file.
		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{

			public void run(IProgressMonitor monitor)
			{
				updateValidation(source, items);
			}
		};

		try
		{
			ResourcesPlugin.getWorkspace().run(runnable, getMarkerRule(source), IWorkspace.AVOID_UPDATE,
					new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(Messages.ProjectFileValidationListener_ERR_UpdateMarkers, e);
		}
	}

	private void updateValidation(Object source, IValidationItem[] items)
	{
		synchronized (this) // prevents simultaneous error updates on the same file
		{
			if (source == null)
			{
				return;
			}

			IResource workspaceResource = null;
			IUniformResource externalResource = null;
			boolean isExternal = false;
			if (source instanceof IResource)
			{
				workspaceResource = (IResource) source;
			}
			else if (source instanceof IUniformResource)
			{
				externalResource = (IUniformResource) source;
				isExternal = true;
			}
			else
			{
				// invalid source
				return;
			}

			try
			{
				String markerType = IValidationConstants.PROBLEM_MARKER;
				// deletes the old markers
				if (isExternal)
				{
					MarkerUtils.deleteMarkers(externalResource, markerType, true);
				}
				else
				{
					workspaceResource.deleteMarkers(markerType, true, IResource.DEPTH_INFINITE);
				}

				// adds the new ones
				IMarker marker;
				for (IValidationItem item : items)
				{
					if (isExternal)
					{
						marker = MarkerUtils.createMarker(externalResource, null, markerType);
						// don't persist on external file
						marker.setAttribute(IMarker.TRANSIENT, true);
					}
					else
					{
						marker = workspaceResource.createMarker(markerType);
					}
					marker.setAttribute(IMarker.SEVERITY, item.getSeverity());
					marker.setAttribute(IMarker.CHAR_START, item.getOffset());
					marker.setAttribute(IMarker.CHAR_END, item.getOffset() + item.getLength());
					marker.setAttribute(IMarker.MESSAGE, item.getMessage());
					marker.setAttribute(IMarker.LINE_NUMBER, item.getLineNumber());
				}
			}
			catch (CoreException e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
	}

	private static ISchedulingRule getMarkerRule(Object resource)
	{
		if (resource instanceof IResource)
		{
			return ResourcesPlugin.getWorkspace().getRuleFactory().markerRule((IResource) resource);
		}
		return null;
	}
}
