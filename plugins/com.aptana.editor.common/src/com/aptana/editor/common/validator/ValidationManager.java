/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.parsing.ast.IParseNode;

public class ValidationManager implements IValidationManager
{

	private FileService fFileService;
	private IDocument fDocument;
	private Object fResource;
	private URI fResourceUri;
	private String fCurrentContentType;
	// the nested languages that need to be validated as well
	private Set<String> fNestedLanguages;
	private Map<String, List<IValidationItem>> fItemsByType;

	private IPropertyChangeListener fPropertyListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			String property = event.getProperty();
			if (fCurrentContentType != null)
			{
				if (getSelectedValidatorsPrefKey(fCurrentContentType).equals(property)
						|| getFilterExpressionsPrefKey(fCurrentContentType).equals(property))
				{
					// re-validate
					validate(fDocument.get(), fCurrentContentType);
				}
			}
		}
	};

	public ValidationManager(FileService fileService)
	{
		fFileService = fileService;
		fNestedLanguages = new HashSet<String>();
		fItemsByType = new HashMap<String, List<IValidationItem>>();
		CommonEditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fPropertyListener);
	}

	public void dispose()
	{
		fDocument = null;
		fResource = null;
		fResourceUri = null;
		fItemsByType.clear();
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

	public void validate(String source, String contentType)
	{
		fCurrentContentType = contentType;

		Collection<List<IValidationItem>> values = fItemsByType.values();
		for (List<IValidationItem> items : values)
		{
			items.clear();
		}

		List<ValidatorReference> validatorRefs = getValidatorRefs(contentType);
		for (ValidatorReference validatorRef : validatorRefs)
		{
			if (fResourceUri == null)
			{
				continue;
			}
			List<IValidationItem> newItems = validatorRef.getValidator().validate(source, fResourceUri, this);
			String type = validatorRef.getMarkerType();
			List<IValidationItem> items = fItemsByType.get(type);
			if (items == null)
			{
				items = new ArrayList<IValidationItem>();
				fItemsByType.put(type, items);
			}
			items.addAll(newItems);

			// checks nested languages
			for (String nestedLanguage : fNestedLanguages)
			{
				processNestedLanguage(nestedLanguage, fItemsByType);
			}
		}
		update(fItemsByType);
	}

	private void processNestedLanguage(String nestedLanguage, Map<String, List<IValidationItem>> itemsByType)
	{
		List<ValidatorReference> validatorRefs = getValidatorRefs(nestedLanguage);
		for (ValidatorReference validatorRef : validatorRefs)
		{
			IValidator validator = validatorRef.getValidator();
			IParseNode rootAST = fFileService.getParseResult();
			List<IValidationItem> newItems = new ArrayList<IValidationItem>();
			processASTForNestedLanguage(rootAST, nestedLanguage, validator, newItems);

			String type = validatorRef.getMarkerType();
			List<IValidationItem> items = itemsByType.get(type);
			if (items == null)
			{
				items = new ArrayList<IValidationItem>();
				itemsByType.put(type, items);
			}
			items.addAll(newItems);
		}
	}

	private void processASTForNestedLanguage(IParseNode node, String language, IValidator validator,
			List<IValidationItem> items)
	{
		if (node.getLanguage().equals(language))
		{
			if (!node.isEmpty())
			{
				try
				{
					String source = fDocument.get(node.getStartingOffset(), node.getLength());
					List<IValidationItem> newItems = validator.validate(source, fResourceUri, this);
					int lines = fDocument.getLineOfOffset(node.getStartingOffset());
					for (IValidationItem item : newItems)
					{
						((ValidationItem) item).setLineNumber(lines + item.getLineNumber());
						((ValidationItem) item).setOffset(node.getStartingOffset() + item.getOffset());
						items.add(item);
					}
				}
				catch (BadLocationException e)
				{
				}
			}
		}
		else
		{
			IParseNode[] children = node.getChildren();
			for (IParseNode child : children)
			{
				processASTForNestedLanguage(child, language, validator, items);
			}
		}
	}

	public IValidationItem addError(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		return addItem(IMarker.SEVERITY_ERROR, message, lineNumber, lineOffset, length, sourcePath);
	}

	public IValidationItem addWarning(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		return addItem(IMarker.SEVERITY_WARNING, message, lineNumber, lineOffset, length, sourcePath);
	}

	public void addNestedLanguage(String language)
	{
		fNestedLanguages.add(language);
	}

	public boolean isIgnored(String message, String language)
	{
		String list = CommonEditorPlugin.getDefault().getPreferenceStore()
				.getString(getFilterExpressionsPrefKey(language));
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

	private IValidationItem addItem(int severity, String message, int lineNumber, int lineOffset, int length,
			URI sourcePath)
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
		return new ValidationItem(severity, message, offset, length, lineNumber, sourcePath.toString());
	}

	private void update(final Map<String, List<IValidationItem>> itemsByType)
	{
		// Performance fix: schedules the error handling as a single workspace update so that we don't trigger a
		// bunch of resource updated events while problem markers are being added to the file.
		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{

			public void run(IProgressMonitor monitor)
			{
				updateValidation(itemsByType);
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

	private synchronized void updateValidation(Map<String, List<IValidationItem>> itemsByType)
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

		Set<String> markerTypes = itemsByType.keySet();
		List<IValidationItem> items;
		for (String markerType : markerTypes)
		{
			try
			{
				// deletes the old markers
				if (isExternal)
				{
					MarkerUtils.deleteMarkers(externalResource, markerType, true);
					// this is to remove "Aptana Problem" markers
					if (!markerType.equals(IMarkerConstants.PROBLEM_MARKER))
					{
						MarkerUtils.deleteMarkers(externalResource, IMarkerConstants.PROBLEM_MARKER, true);
					}
				}
				else
				{
					workspaceResource.deleteMarkers(markerType, true, IResource.DEPTH_INFINITE);
					// this is to remove "Aptana Problem" markers
					if (!markerType.equals(IMarkerConstants.PROBLEM_MARKER))
					{
						workspaceResource
								.deleteMarkers(IMarkerConstants.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
					}
				}

				// adds the new ones
				items = itemsByType.get(markerType);
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
	}

	private static List<ValidatorReference> getValidatorRefs(String contentType)
	{
		List<ValidatorReference> result = new ArrayList<ValidatorReference>();

		List<ValidatorReference> validatorRefs = ValidatorLoader.getInstance().getValidators(contentType);
		String list = CommonEditorPlugin.getDefault().getPreferenceStore()
				.getString(getSelectedValidatorsPrefKey(contentType));
		if (StringUtil.isEmpty(list))
		{
			// by default uses the first validator that supports the content type
			if (validatorRefs.size() > 0)
			{
				result.add(validatorRefs.get(0));
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
						result.add(validator);
						break;
					}
				}
			}
		}
		return result;
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
