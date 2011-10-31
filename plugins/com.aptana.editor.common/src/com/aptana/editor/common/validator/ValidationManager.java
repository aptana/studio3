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
import java.util.Collections;
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
import org.eclipse.osgi.util.NLS;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;

public class ValidationManager implements IValidationManager
{

	private FileService fFileService;
	private IDocument fDocument;
	private Object fResource;
	private URI fResourceUri;
	private String fCurrentContentType;
	private IParseState fParseState;
	// the nested languages that need to be validated as well
	private Set<String> fNestedLanguages;
	private Map<String, List<IValidationItem>> fExistingItemsByType;

	private IPropertyChangeListener fPropertyListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			String property = event.getProperty();
			if (fCurrentContentType != null)
			{
				if (getSelectedValidatorsPrefKey(fCurrentContentType).equals(property)
						|| getFilterExpressionsPrefKey(fCurrentContentType).equals(property)
						|| getParseErrorEnabledPrefKey(fCurrentContentType).equals(property))
				{
					// re-validate
					validate(fDocument.get(), fCurrentContentType);
				}
			}
		}
	};

	public ValidationManager(FileService fileService)
	{
		if (fileService == null)
		{
			throw new IllegalArgumentException(Messages.ValidationManager_FileServiceNonNull);
		}
		fFileService = fileService;
		fParseState = fileService.getParseState();
		fNestedLanguages = new HashSet<String>();
		fExistingItemsByType = new HashMap<String, List<IValidationItem>>();
		CommonEditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fPropertyListener);
	}

	public void dispose()
	{
		fDocument = null;
		fResource = null;
		fResourceUri = null;
		fParseState = null;
		fExistingItemsByType.clear();
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

		Map<String, List<IValidationItem>> allItems = new HashMap<String, List<IValidationItem>>();
		List<ValidatorReference> validatorRefs = getValidatorRefs(contentType);
		if (!validatorRefs.isEmpty())
		{
			for (ValidatorReference validatorRef : validatorRefs)
			{
				if (fResourceUri == null)
				{
					continue;
				}
				List<IValidationItem> newItems = validatorRef.getValidator().validate(source, fResourceUri, this);

				String type = validatorRef.getMarkerType();
				List<IValidationItem> items = allItems.get(type);
				if (items == null)
				{
					items = Collections.synchronizedList(new ArrayList<IValidationItem>());
					allItems.put(type, items);
				}
				items.addAll(newItems);

				// checks nested languages
				for (String nestedLanguage : fNestedLanguages)
				{
					processNestedLanguage(nestedLanguage, allItems);
				}
			}
		}
		// needs to update the markers regardless if any validator is selected
		update(allItems);
	}

	private void processNestedLanguage(String nestedLanguage, Map<String, List<IValidationItem>> itemsByType)
	{
		List<ValidatorReference> validatorRefs = getValidatorRefs(nestedLanguage);
		for (ValidatorReference validatorRef : validatorRefs)
		{
			IValidator validator = validatorRef.getValidator();
			IParseNode rootAST = fFileService.getParseResult();
			List<IValidationItem> newItems = new ArrayList<IValidationItem>();

			if (rootAST == null)
			{
				continue;
			}

			processASTForNestedLanguage(rootAST, nestedLanguage, validator, newItems);

			String type = validatorRef.getMarkerType();
			List<IValidationItem> items = itemsByType.get(type);
			if (items == null)
			{
				items = Collections.synchronizedList(new ArrayList<IValidationItem>());
				itemsByType.put(type, items);
			}
			items.addAll(newItems);
		}
	}

	private void processASTForNestedLanguage(IParseNode node, String language, IValidator validator,
			List<IValidationItem> items)
	{
		if (node == null)
		{
			return;
		}
		if (node.getLanguage().equals(language))
		{
			if (!node.isEmpty())
			{
				try
				{
					String source = fDocument.get(node.getStartingOffset(), node.getLength());
					ParseState parseState = new ParseState();
					parseState.setEditState(source, null, 0, 0);
					setParseState(parseState);
					ParserPoolFactory.parse(language, parseState);
					List<IValidationItem> newItems = validator.validate(source, fResourceUri, this);
					int lines = fDocument.getLineOfOffset(node.getStartingOffset());
					for (IValidationItem item : newItems)
					{
						((ValidationItem) item).setLineNumber(lines + item.getLineNumber());
						((ValidationItem) item).setOffset(node.getStartingOffset() + item.getOffset());
						items.add(item);
					}
					setParseState(fFileService.getParseState());
				}
				catch (Exception e)
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

	public IValidationItem createError(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
	{
		return addItem(IMarker.SEVERITY_ERROR, message, lineNumber, lineOffset, length, sourcePath);
	}

	public IValidationItem createWarning(String message, int lineNumber, int lineOffset, int length, URI sourcePath)
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

	public List<IValidationItem> getValidationItems()
	{
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		Set<String> types = fExistingItemsByType.keySet();
		for (String type : types)
		{
			items.addAll(fExistingItemsByType.get(type));
		}
		return items;
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
			IdeLog.logError(CommonEditorPlugin.getDefault(), Messages.ProjectFileValidationListener_ERR_UpdateMarkers,
					e);
		}
	}

	private synchronized void updateValidation(Map<String, List<IValidationItem>> itemsByType)
	{
		IResource workspaceResource = null;
		IUniformResource externalResource = null;
		boolean isExternal = false;
		if (fResource instanceof IResource)
		{
			workspaceResource = (IResource) fResource;
			if (!workspaceResource.exists())
			{
				// no need to update the marker when the resource no longer exists
				return;
			}
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

		// checks each marker type that we had items for to see if we need to completely delete the markers of this type
		// and re-add or if we need only to add the new items that didn't exist before
		Set<String> markerTypes = fExistingItemsByType.keySet();
		List<IValidationItem> oldItems, newItems;
		Set<String> markerTypesInNewOnly = new HashSet<String>(itemsByType.keySet());
		for (String markerType : markerTypes)
		{
			oldItems = fExistingItemsByType.get(markerType);
			newItems = itemsByType.get(markerType);
			List<IValidationItem> itemsInNewOnly = new ArrayList<IValidationItem>();
			markerTypesInNewOnly.remove(markerType);

			// checks if each item in the old list still exists in the new one; if so, we don't need to delete the old
			// markers
			boolean needDelete = false;
			if (newItems == null)
			{
				needDelete = true;
			}
			else
			{
				itemsInNewOnly.addAll(newItems);
				for (IValidationItem item : oldItems)
				{
					if (newItems.contains(item))
					{
						itemsInNewOnly.remove(item);
					}
					else
					{
						needDelete = true;
						break;
					}
				}
			}

			try
			{
				if (needDelete)
				{
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
					if (newItems != null)
					{
						addMarkers(newItems, markerType, isExternal, workspaceResource, externalResource);
					}
				}
				else
				{
					// just needs to add the items that didn't exist before
					addMarkers(itemsInNewOnly, markerType, isExternal, workspaceResource, externalResource);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}
		}

		// now checks for the new marker types that didn't exist previously
		for (String markerType : markerTypesInNewOnly)
		{
			try
			{
				addMarkers(itemsByType.get(markerType), markerType, isExternal, workspaceResource, externalResource);
			}
			catch (CoreException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}
		}
		fExistingItemsByType = itemsByType;
	}

	private void addMarkers(List<IValidationItem> items, String markerType, boolean isExternal,
			IResource workspaceResource, IUniformResource externalResource) throws CoreException
	{
		for (IValidationItem item : items)
		{
			IMarker marker;
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

	private static String getParseErrorEnabledPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.PARSE_ERROR_ENABLED; //$NON-NLS-1$
	}

	public IParseState getParseState()
	{
		return fParseState;
	}

	private void setParseState(IParseState parseState)
	{
		fParseState = parseState;
	}

	public void addParseErrors(List<IValidationItem> items, String language)
	{

		IParseState parseState = getParseState();

		if (parseState == null
				|| fDocument == null
				|| !CommonEditorPlugin.getDefault().getPreferenceStore()
						.getBoolean(getParseErrorEnabledPrefKey(language)))
		{
			return;
		}

		for (IParseError parseError : parseState.getErrors())
		{
			try
			{
				if (parseError.getSeverity() == IParseError.Severity.ERROR)
				{
					items.add(createError(parseError.getMessage(),
							fDocument.getLineOfOffset(parseError.getOffset()) + 1, parseError.getOffset(), 0,
							fResourceUri));
				}
				else
				{
					items.add(createWarning(parseError.getMessage(),
							fDocument.getLineOfOffset(parseError.getOffset()) + 1, parseError.getOffset(), 0,
							fResourceUri));
				}

			}
			catch (BadLocationException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(),
						NLS.bind("Error finding line on given offset : {0}", parseError.getOffset() + 1), e); //$NON-NLS-1$
			}
		}

	}

	public static boolean hasErrorOrWarningOnLine(List<IValidationItem> items, int line)
	{
		if (items == null)
		{
			return false;
		}

		for (IValidationItem item : items)
		{
			if (item.getLineNumber() == line)
			{
				return true;
			}
		}

		return false;
	}
}
