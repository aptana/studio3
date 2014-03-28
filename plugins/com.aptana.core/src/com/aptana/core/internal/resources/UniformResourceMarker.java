/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.resources;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.internal.resources.IMarkerSetElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;

/**
 * @author Max Stepanov
 */
@SuppressWarnings({ "restriction", "rawtypes" })
public class UniformResourceMarker extends PlatformObject implements IUniformResourceMarker
{

	private IUniformResource resource;
	private long id;

	/**
	 * UniformResourceMarker
	 * 
	 * @param resource
	 * @param id
	 */
	public UniformResourceMarker(IUniformResource resource, long id)
	{
		super();
		this.resource = resource;
		this.id = id;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#delete()
	 */
	public void delete() throws CoreException
	{
		getMarkerManager().removeMarker(resource, id);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#exists()
	 */
	public boolean exists()
	{
		return getInfo() != null;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getAttribute(attributeName);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, int)
	 */
	public int getAttribute(String attributeName, int defaultValue)
	{
		MarkerInfo info = getInfo();
		if (info == null)
		{
			return defaultValue;
		}
		Object value = info.getAttribute(attributeName);
		if (value instanceof Integer)
		{
			return ((Integer) value).intValue();
		}
		return defaultValue;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, java.lang.String)
	 */
	public String getAttribute(String attributeName, String defaultValue)
	{
		MarkerInfo info = getInfo();
		if (info == null)
		{
			return defaultValue;
		}
		Object value = info.getAttribute(attributeName);
		if (value instanceof String)
		{
			return (String) value;
		}
		return defaultValue;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, boolean)
	 */
	public boolean getAttribute(String attributeName, boolean defaultValue)
	{
		MarkerInfo info = getInfo();
		if (info == null)
		{
			return defaultValue;
		}
		Object value = info.getAttribute(attributeName);
		if (value instanceof Boolean)
		{
			return ((Boolean) value).booleanValue();
		}
		return defaultValue;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttributes()
	 */
	@SuppressWarnings("unchecked")
	public Map getAttributes() throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getAttributes();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttributes(java.lang.String[])
	 */
	public Object[] getAttributes(String[] attributeNames) throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getAttributes(attributeNames);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getCreationTime()
	 */
	public long getCreationTime() throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getCreationTime();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getId()
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getResource()
	 */
	public IResource getResource()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * @see com.aptana.ide.core.resources.IUniformResourceMarker#getUniformResource()
	 */
	public IUniformResource getUniformResource()
	{
		return resource;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getType()
	 */
	public String getType() throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getType();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#isSubtypeOf(java.lang.String)
	 */
	public boolean isSubtypeOf(String superType) throws CoreException
	{
		return getMarkerManager().isSubtype(getType(), superType);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, int)
	 */
	public void setAttribute(String attributeName, int value) throws CoreException
	{
		setAttribute(attributeName, Integer.valueOf(value));
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object value) throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		boolean validate = getMarkerManager().isPersistent(info);
		info.setAttribute(attributeName, value, validate);
		IMarkerSetElement[] changes = new IMarkerSetElement[] { new MarkerDelta(IResourceDelta.CHANGED, resource, info) };
		getMarkerManager().changedMarkers(resource, changes);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, boolean)
	 */
	public void setAttribute(String attributeName, boolean value) throws CoreException
	{
		setAttribute(attributeName, value ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttributes(java.lang.String[], java.lang.Object[])
	 */
	public void setAttributes(String[] attributeNames, Object[] values) throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		boolean validate = getMarkerManager().isPersistent(info);
		info.setAttributes(attributeNames, values, validate);
		IMarkerSetElement[] changes = new IMarkerSetElement[] { new MarkerDelta(IResourceDelta.CHANGED, resource, info) };
		getMarkerManager().changedMarkers(resource, changes);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttributes(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void setAttributes(Map attributes) throws CoreException
	{
		MarkerInfo info = getInfo();
		checkInfo(info);
		boolean validate = getMarkerManager().isPersistent(info);
		info.setAttributes(attributes, validate);
		IMarkerSetElement[] changes = new IMarkerSetElement[] { new MarkerDelta(IResourceDelta.CHANGED, resource, info) };
		getMarkerManager().changedMarkers(resource, changes);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof UniformResourceMarker))
		{
			return false;
		}
		return (resource.equals(((UniformResourceMarker) obj).resource) && id == ((UniformResourceMarker) obj).id);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return (int) id + resource.hashCode();
	}

	/**
	 * getInfo
	 * 
	 * @return MarkerInfo
	 */
	private MarkerInfo getInfo()
	{
		return getMarkerManager().findMarkerInfo(resource, id);
	}

	private static MarkerManager getMarkerManager()
	{
		return MarkerManager.getInstance();
	}

	/**
	 * Checks the given marker info to ensure that it is not null. Throws an exception if it is.
	 */
	private void checkInfo(MarkerInfo info) throws CoreException
	{
		if (info == null)
		{
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK,
					Messages.UniformResourceMarker_UniformResourceMarketInfoNull, null));
		}
	}
}
