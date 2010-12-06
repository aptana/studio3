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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;

public class ValidationManager implements IValidationManager
{

	private IDocument fDocument;
	private Object fResource;
	private URI fResourceUri;
	private List<IValidationItem> fItems;
	private String fCurrentLanguage;

	private IPropertyChangeListener fPropertyListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			String property = event.getProperty();
			if (fCurrentLanguage != null)
			{
				if (getSelectedValidatorsPrefKey(fCurrentLanguage).equals(property)
						|| getFilterExpressionsPrefKey(fCurrentLanguage).equals(property))
				{
					// re-validate
					validate(fDocument.get(), fCurrentLanguage);
				}
			}
		}
	};

	public ValidationManager()
	{
		fItems = new ArrayList<IValidationItem>();
		CommonEditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fPropertyListener);
	}

	public void dispose()
	{
		fDocument = null;
		fResource = null;
		fResourceUri = null;
		fItems.clear();
		CommonEditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(fPropertyListener);
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
		fCurrentLanguage = language;

		if (fResourceUri != null)
		{
			List<ValidatorReference> validatorRefs = ValidatorLoader.getInstance().getValidators(language);
			String list = CommonEditorPlugin.getDefault().getPreferenceStore()
					.getString(getSelectedValidatorsPrefKey(fCurrentLanguage));
			if (StringUtil.isEmpty(list))
			{
				// by default use the first validator that supports the language
				if (validatorRefs.size() > 0)
				{
					validatorRefs.get(0).getValidator().validate(source, fResourceUri, this);
				}
			}
			else
			{
				String[] selectedValidators = list.split(","); //$NON-NLS-1$
				for (String name : selectedValidators)
				{
					for (ValidatorReference validator : validatorRefs)
					{
						if (validator.getName().equals(name))
						{
							validator.getValidator().validate(source, fResourceUri, this);
							break;
						}
					}
				}
			}
		}
		update(fItems.toArray(new IValidationItem[fItems.size()]));
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

	public boolean isIgnored(String message, String language)
	{
		String list = CommonEditorPlugin.getDefault().getPreferenceStore()
				.getString(getFilterExpressionsPrefKey(fCurrentLanguage));
		if (!StringUtil.isEmpty(list))
		{
			String[] expressions = list.split("####"); //$NON-NLS-1$
			for (String expression : expressions)
			{
				if (message.matches(expression))
				{
					return true;
				}
			}
		}
		return false;
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

	private static String getSelectedValidatorsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.SELECTED_VALIDATORS; //$NON-NLS-1$
	}

	private static String getFilterExpressionsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.FILTER_EXPRESSIONS; //$NON-NLS-1$
	}
}
