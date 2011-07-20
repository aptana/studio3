/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.QualifiedContentType;

public class EditorPropertySource implements IPropertySource
{
	// Property Descriptors
	static protected IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[6];

	static
	{
		PropertyDescriptor descriptor;

		descriptor = new PropertyDescriptor(IEditorPropertyConstants.CONTENT_TYPE_KEY,
				IEditorPropertyConstants.CONTENT_TYPE_LABEL);
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(IEditorPropertyConstants.DEBUG_INFO_CATEGORY);
		propertyDescriptors[0] = descriptor;

		descriptor = new PropertyDescriptor(IEditorPropertyConstants.SCOPE_KEY, IEditorPropertyConstants.SCOPE_LABEL);
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(IEditorPropertyConstants.DEBUG_INFO_CATEGORY);
		propertyDescriptors[1] = descriptor;

		descriptor = new PropertyDescriptor(IEditorPropertyConstants.TEXT_KEY, IEditorPropertyConstants.TEXT_LABEL);
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(IEditorPropertyConstants.DEBUG_INFO_CATEGORY);
		propertyDescriptors[2] = descriptor;

		descriptor = new PropertyDescriptor(IEditorPropertyConstants.OFFSET_START_KEY,
				IEditorPropertyConstants.OFFSET_START_LABEL);
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(IEditorPropertyConstants.DEBUG_INFO_CATEGORY);
		propertyDescriptors[3] = descriptor;

		descriptor = new PropertyDescriptor(IEditorPropertyConstants.OFFSET_END_KEY,
				IEditorPropertyConstants.OFFSET_END_LABEL);
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(IEditorPropertyConstants.DEBUG_INFO_CATEGORY);
		propertyDescriptors[4] = descriptor;

		descriptor = new PropertyDescriptor(IEditorPropertyConstants.LENGTH_KEY, IEditorPropertyConstants.LENGTH_LABEL);
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(IEditorPropertyConstants.DEBUG_INFO_CATEGORY);
		propertyDescriptors[5] = descriptor;

	}

	private final IAdaptable adaptableObject;

	public EditorPropertySource(IAdaptable adaptableObject)
	{
		this.adaptableObject = adaptableObject;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object key)
	{
		if (!(adaptableObject instanceof AdaptableTextSelection))
		{
			return null;
		}

		try
		{
			ISourceViewer viewer = ((AdaptableTextSelection) adaptableObject).getViewer();
			IDocument document = viewer.getDocument();
			int offset = ((AdaptableTextSelection) adaptableObject).getOffset();
			int length = ((AdaptableTextSelection) adaptableObject).getLength();
			QualifiedContentType contentType = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getContentType(document, offset);
			String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(viewer, offset);

			if (key.equals(IEditorPropertyConstants.CONTENT_TYPE_KEY) && contentType.getPartCount() > 0)
			{
				return contentType.getParts()[0];
			}
			else if (key.equals(IEditorPropertyConstants.SCOPE_KEY))
			{
				return scope;
			}
			else if (key.equals(IEditorPropertyConstants.TEXT_KEY))
			{
				return document.get(offset, length);
			}
			else if (key.equals(IEditorPropertyConstants.OFFSET_START_KEY))
			{
				return offset;
			}
			else if (key.equals(IEditorPropertyConstants.OFFSET_END_KEY))
			{
				return offset + length;
			}
			else if (key.equals(IEditorPropertyConstants.LENGTH_KEY))
			{
				return length;
			}
		}
		catch (Exception e)
		{
		}
		return null;
	}

	public Object getEditableValue()
	{
		return null;
	}

	public boolean isPropertySet(Object id)
	{
		return false;
	}

	public void resetPropertyValue(Object id)
	{
	}

	public void setPropertyValue(Object id, Object value)
	{
	}

}
