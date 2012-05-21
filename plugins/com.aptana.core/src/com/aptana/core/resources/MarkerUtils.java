/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.internal.resources.MarkerInfo;
import com.aptana.core.internal.resources.MarkerManager;
import com.aptana.core.internal.resources.UniformResourceMarker;
import com.aptana.core.util.ArrayUtil;

/**
 * @author Max Stepanov
 */
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public final class MarkerUtils
{

	private static final IMarker[] NO_MARKERS = new IMarker[0];

	private MarkerUtils()
	{
	}

	/**
	 * createMarker
	 * 
	 * @param resource
	 * @param attributes
	 * @param markerType
	 * @return IMarker
	 * @throws CoreException
	 */
	public static IMarker createMarker(IUniformResource resource, Map attributes, String markerType)
			throws CoreException
	{
		MarkerInfo info = new MarkerInfo();
		info.setType(markerType);
		info.setCreationTime(System.currentTimeMillis());
		if (attributes != null)
		{
			info.setAttributes(attributes, getMarkerManager().isPersistent(info));
		}
		getMarkerManager().add(resource, info);
		return new UniformResourceMarker(resource, info.getId());
	}

	/**
	 * findMarkers
	 * 
	 * @param resource
	 * @param type
	 * @param includeSubtypes
	 * @return IMarker[]
	 */
	public static IMarker[] findMarkers(IUniformResource resource, String type, boolean includeSubtypes)
	{
		MarkerManager markerManager = getMarkerManager();
		MarkerInfo[] list = markerManager.findMarkersInfo(resource, type, includeSubtypes);
		if (ArrayUtil.isEmpty(list))
		{
			return NO_MARKERS;
		}

		IMarker[] result = new IMarker[list.length];
		for (int i = 0; i < list.length; ++i)
		{
			result[i] = new UniformResourceMarker(resource, list[i].getId());
		}
		return result;
	}

	/**
	 * Delete all Markers with the given type.
	 * 
	 * @param resource
	 * @param type
	 * @param includeSubtypes
	 * @return IMarker[]
	 * @throws CoreException
	 *             with a multi-status problems in case some markers where not successfully deleted.
	 */
	public static void deleteMarkers(IUniformResource resource, String type, boolean includeSubtypes)
			throws CoreException
	{
		IMarker[] toDelete = findMarkers(resource, type, includeSubtypes);
		MultiStatus status = new MultiStatus(CorePlugin.PLUGIN_ID, 0, "Errors deleting markers", null); //$NON-NLS-1$
		for (IMarker marker : toDelete)
		{
			try
			{
				marker.delete();
			}
			catch (CoreException e)
			{
				status.add(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}
		if (status.getChildren().length > 0)
		{
			throw new CoreException(status);
		}
	}

	private static MarkerManager getMarkerManager()
	{
		return MarkerManager.getInstance();
	}

	/**
	 * addResourceChangeListener
	 * 
	 * @param listener
	 */
	public static void addResourceChangeListener(IUniformResourceChangeListener listener)
	{
		getMarkerManager().addResourceChangeListener(listener);
	}

	/**
	 * removeResourceChangeListener
	 * 
	 * @param listener
	 */
	public static void removeResourceChangeListener(IUniformResourceChangeListener listener)
	{
		getMarkerManager().removeResourceChangeListener(listener);
	}

	/**
	 * Sets char end.
	 * 
	 * @param attributes
	 *            - attributes.
	 * @param charEnd
	 *            - char end.
	 */
	public static void setCharEnd(Map attributes, int charEnd)
	{
		attributes.put(IMarker.CHAR_END, Integer.valueOf(charEnd));
	}

	/**
	 * Sets char end.
	 * 
	 * @param attributes
	 *            - attributes.
	 * @param charEnd
	 *            - char end.
	 */
	public static void setCharStart(Map attributes, int charStart)
	{
		attributes.put(IMarker.CHAR_START, Integer.valueOf(charStart));
	}

	/**
	 * Sets message.
	 * 
	 * @param attributes
	 *            - attributes.
	 * @param message
	 *            - message.
	 */
	public static void setMessage(Map attributes, String message)
	{
		attributes.put(IMarker.MESSAGE, message);
	}

	/**
	 * Sets line number.
	 * 
	 * @param attributes
	 *            - attributes.
	 * @param line
	 *            - line number.
	 */
	public static void setLineNumber(Map attributes, int line)
	{
		attributes.put(IMarker.LINE_NUMBER, Integer.valueOf(line));
	}
}
