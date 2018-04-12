/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.model.BaseElement;
import com.aptana.index.core.ui.views.IPropertyInformation;

abstract class BaseElementPropertySource<V extends BaseElement, P extends Enum<P> & IPropertyInformation<V>> implements
		IPropertySource
{

	private BaseElement element;

	public BaseElementPropertySource(BaseElement adaptableObject)
	{
		this.element = adaptableObject;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue()
	{
		return null;
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
	 * @return Set
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
			result = ((IPropertyInformation<V>) id).getPropertyValue(getModelElement());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private V getModelElement()
	{
		return (V) element;
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value)
	{
	}
}
