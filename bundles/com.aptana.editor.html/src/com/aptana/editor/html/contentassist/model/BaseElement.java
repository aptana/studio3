/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexDocument;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * BaseElement
 */
public abstract class BaseElement<P extends Enum<P> & IPropertyInformation<? extends BaseElement<P>>> implements
		Convertible, IndexDocument, IPropertySource
{
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$

	private String _name;
	private String _description;
	private List<String> _documents;

	/**
	 * addDocument
	 * 
	 * @param document
	 */
	public void addDocument(String document)
	{
		if (document != null && document.length() > 0)
		{
			if (this._documents == null)
			{
				this._documents = new ArrayList<String>();
			}

			this._documents.add(document);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setName(StringUtil.getStringValue(object.get(NAME_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
	}

	/**
	 * getDescription
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getDocuments
	 * 
	 * @return
	 */
	public List<String> getDocuments()
	{
		return CollectionsUtil.getListValue(this._documents);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue()
	{
		return null;
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return StringUtil.getStringValue(this._name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		List<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();

		for (P p : getPropertyInfoSet())
		{
			PropertyDescriptor descriptor = new PropertyDescriptor(p, p.getHeader());
			String category = p.getCategory();

			if (!StringUtil.isEmpty(category))
			{
				descriptor.setCategory(category);
			}

			result.add(descriptor);
		}

		return result.toArray(new IPropertyDescriptor[result.size()]);
	}

	/**
	 * getPropertyInfoSet
	 * 
	 * @return
	 */
	protected Set<P> getPropertyInfoSet()
	{
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id instanceof IPropertyInformation)
		{
			result = ((IPropertyInformation<BaseElement<P>>) id).getPropertyValue(this);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id)
	{
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setName
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(NAME_PROPERTY, this.getName());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
	}
}
