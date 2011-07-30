/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.ThemeRule;

public class EditorPropertySource implements IPropertySource
{
	// Property Descriptors
	static protected IPropertyDescriptor[] propertyDescriptors;

	static
	{

		PropertyDescriptor descriptor1 = new PropertyDescriptor(IEditorPropertyConstants.CONTENT_TYPE_KEY,
				IEditorPropertyConstants.CONTENT_TYPE_LABEL);
		descriptor1.setAlwaysIncompatible(true);
		descriptor1.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		PropertyDescriptor descriptor2 = new PropertyDescriptor(IEditorPropertyConstants.SCOPE_KEY,
				IEditorPropertyConstants.SCOPE_LABEL);
		descriptor2.setAlwaysIncompatible(true);
		descriptor2.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		PropertyDescriptor descriptor3 = new PropertyDescriptor(IEditorPropertyConstants.TEXT_KEY,
				IEditorPropertyConstants.TEXT_LABEL);
		descriptor3.setAlwaysIncompatible(true);
		descriptor3.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		PropertyDescriptor descriptor4 = new PropertyDescriptor(IEditorPropertyConstants.OFFSET_START_KEY,
				IEditorPropertyConstants.OFFSET_START_LABEL);
		descriptor4.setAlwaysIncompatible(true);
		descriptor4.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		PropertyDescriptor descriptor5 = new PropertyDescriptor(IEditorPropertyConstants.OFFSET_END_KEY,
				IEditorPropertyConstants.OFFSET_END_LABEL);
		descriptor5.setAlwaysIncompatible(true);
		descriptor5.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		PropertyDescriptor descriptor6 = new PropertyDescriptor(IEditorPropertyConstants.LENGTH_KEY,
				IEditorPropertyConstants.LENGTH_LABEL);
		descriptor6.setAlwaysIncompatible(true);
		descriptor6.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		PropertyDescriptor descriptor7 = new PropertyDescriptor(IEditorPropertyConstants.THEME_KEY,
				IEditorPropertyConstants.THEME_LABEL);
		descriptor7.setAlwaysIncompatible(true);
		descriptor7.setCategory(IEditorPropertyConstants.EDITOR_INFO_CATEGORY);

		propertyDescriptors = new IPropertyDescriptor[] { descriptor1, descriptor2, descriptor3, descriptor4,
				descriptor5, descriptor6, descriptor7 };

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
			else if (key.equals(IEditorPropertyConstants.THEME_KEY))
			{
				Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
				Collection<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
				List<ThemeRule> rules = theme.getTokens();

				if (rules != null)
				{
					for (ThemeRule rule : rules)
					{
						if (!rule.isSeparator())
						{
							selectors.add(rule.getScopeSelector());
						}
					}
					IScopeSelector matchingSelector = ScopeSelector.bestMatch(selectors, scope);
					for (ThemeRule rule : rules)
					{
						if (matchingSelector.equals(rule.getScopeSelector()))
						{
							return rule;
						}
					}

				}
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
