/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

abstract class BaseNode<P extends Enum<P> & IPropertyInformation<? extends BaseNode<P>>> implements IBundleViewNode,
		IPropertySource
{
	protected Object[] NO_OBJECTS = new Object[0]; // $codepro.audit.disable reusableImmutables

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.IBundleViewNode#getActions()
	 */
	public Action[] getActions()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.IBundleViewNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return NO_OBJECTS;
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
	 * @see com.aptana.scripting.ui.views.IBundleViewNode#getImage()
	 */
	public Image getImage()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.IBundleViewNode#getLabel()
	 */
	public String getLabel()
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
			result.add(new PropertyDescriptor(p, p.getHeader()));
		}

		return result.toArray(new IPropertyDescriptor[result.size()]);
	}

	/**
	 * getPropertyInfoSet
	 * 
	 * @return Set
	 */
	protected abstract Set<P> getPropertyInfoSet();

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
			result = ((IPropertyInformation<BaseNode<P>>) id).getPropertyValue(this);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.IBundleViewNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return false;
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
