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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.editor.common.CommonEditorPlugin;

public class ValidationManager implements IValidationManager
{

	private IDocument fDocument;
	private Object fResource;
	private URI fResourceUri;
	private List<IValidationItem> fItems;

	public ValidationManager()
	{
		fItems = new ArrayList<IValidationItem>();
	}

	public void dispose()
	{
		fDocument = null;
		fResource = null;
		fResourceUri = null;
		fItems.clear();
	}

	public void setDocument(IDocument document)
	{
		fDocument = document;
	}

	/**
	 * Sets the resource the file service is currently handling.
	 * 
	 * @param resource
	 *            should either be an {IResource} for workspace resource or {IUniformResource} for external resource
	 */
	public void setResource(Object resource)
	{
		fResource = resource;
		if (fResource instanceof IResource)
		{
			fResourceUri = ((IResource) fResource).getLocationURI();
		}
		else if (fResource instanceof IUniformResource)
		{
			fResourceUri = ((IUniformResource) fResource).getURI();
		}
	}

	public void validate(String source, String language)
	{
		fItems.clear();

		List<ValidatorReference> validatorRefs = ValidatorLoader.getInstance().getValidators(language);
		// using the first one for now
		// TODO: change to match the user selection in preferences
		if (!validatorRefs.isEmpty() && fResourceUri != null)
		{
			validatorRefs.get(0).getValidator().validate(source, fResourceUri, this);
			update(fItems.toArray(new IValidationItem[fItems.size()]));
		}
	}

	public void addError(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		addItem(IMarker.SEVERITY_ERROR, message, lineNumber, lineOffset, length, sourcePath);
	}

	public void addWarning(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		addItem(IMarker.SEVERITY_WARNING, message, lineNumber, lineOffset, length, sourcePath);
	}

	public List<IValidationItem> getItems()
	{
		return Collections.unmodifiableList(fItems);
	}

	private void addItem(int severity, String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		int charLineOffset = 0;
		if (fDocument != null)
		{
			try
			{
				charLineOffset = fDocument.getLineOffset(lineNumber - 1);
			}
			catch (BadLocationException e)
			{
			}
		}
		int offset = charLineOffset + lineOffset;
		fItems.add(new ValidationItem(severity, message, offset, length, lineNumber, sourcePath.toString()));
	}

	private void update(final IValidationItem[] items)
	{
		// Performance fix: schedules the error handling as a single workspace update so that we don't trigger a
		// bunch of resource updated events while problem markers are being added to the file.
		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{

			public void run(IProgressMonitor monitor)
			{
				updateValidation(items);
			}
		};

		try
		{
			ResourcesPlugin.getWorkspace().run(runnable, getMarkerRule(fResource), IWorkspace.AVOID_UPDATE,
					new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(Messages.ProjectFileValidationListener_ERR_UpdateMarkers, e);
		}
	}

	private synchronized void updateValidation(IValidationItem[] items)
	{
		if (fResource == null)
		{
			return;
		}

		IResource workspaceResource = null;
		IUniformResource externalResource = null;
		boolean isExternal = false;
		if (fResource instanceof IResource)
		{
			workspaceResource = (IResource) fResource;
		}
		else if (fResource instanceof IUniformResource)
		{
			externalResource = (IUniformResource) fResource;
			isExternal = true;
		}
		else
		{
			// invalid source
			return;
		}

		try
		{
			String markerType = IMarkerConstants.PROBLEM_MARKER;
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
				marker.setAttributes(item.createMarkerAttributes());
			}
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(e);
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
