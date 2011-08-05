/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class FileTemplateNode extends BaseNode
{
	private enum Property
	{
		NAME("Name"), //$NON-NLS-1$
		PATH("Path"), //$NON-NLS-1$
		SCOPE("Scope"), //$NON-NLS-1$
		FILE_TYPE("File Type"); //$NON-NLS-1$

		private String header;

		private Property(String header)
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final Image FILE_TEMPLATE_ICON = ScriptingUIPlugin.getImage("icons/template.png"); //$NON-NLS-1$
	private TemplateElement _fileTemplate;

	/**
	 * SnippetNode
	 * 
	 * @param fileTemplate
	 */
	public FileTemplateNode(TemplateElement fileTemplate)
	{
		this._fileTemplate = fileTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return FILE_TEMPLATE_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._fileTemplate.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		List<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();

		for (Property p : EnumSet.allOf(Property.class))
		{
			result.add(new PropertyDescriptor(p, p.getHeader()));
		}

		return result.toArray(new IPropertyDescriptor[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id instanceof Property)
		{
			switch ((Property) id)
			{
				case NAME:
					result = this._fileTemplate.getDisplayName();
					break;

				case PATH:
					result = this._fileTemplate.getPath();
					break;

				case SCOPE:
					String scope = this._fileTemplate.getScope();

					result = (scope != null && scope.length() > 0) ? scope : "all"; //$NON-NLS-1$
					break;

				case FILE_TYPE:
					result = this._fileTemplate.getFiletype();
					break;
			}
		}

		return result;
	}
}
