/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.resources;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.IMarkerSetElement;
import org.eclipse.core.internal.resources.MarkerTypeDefinitionCache;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceChangeListener;

/**
 * @author Max Stepanov
 */
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public final class MarkerManager
{

	private static final MarkerInfo[] NO_MARKER_INFO = new MarkerInfo[0];
	private static MarkerManager instance;

	private MarkerTypeDefinitionCache cache = new MarkerTypeDefinitionCache();
	private long nextMarkerId = 0;
	private Map resources = new HashMap();
	private ListenerList listeners = new ListenerList();
	private IMarker rootMarker;
	private Map currentDeltas = null;
	private Object lock = new Object();

	/**
	 * getInstance
	 * 
	 * @return MarkerManager
	 */
	public static MarkerManager getInstance()
	{
		if (instance == null)
		{
			synchronized (MarkerManager.class)
			{
				if (instance == null)
				{
					instance = new MarkerManager();
				}
			}
		}
		return instance;
	}

	private MarkerManager()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new IResourceChangeListener()
		{
			public void resourceChanged(IResourceChangeEvent event)
			{
				handleResourceChanged();
			}
		}, IResourceChangeEvent.PRE_BUILD);
	}

	/**
	 * findMarkerInfo
	 * 
	 * @param resource
	 * @param id
	 * @return MarkerInfo
	 */
	MarkerInfo findMarkerInfo(IUniformResource resource, long id)
	{
		ResourceInfo info = getResourceInfo(resource);
		if (info == null)
		{
			return null;
		}
		MarkerSet markers = info.getMarkers(false);
		if (markers == null)
		{
			return null;
		}
		return (MarkerInfo) markers.get(id);
	}

	/**
	 * add
	 * 
	 * @param resource
	 * @param marker
	 * @throws CoreException
	 */
	public void add(IUniformResource resource, MarkerInfo marker) throws CoreException
	{
		ResourceInfo info = getResourceInfo(resource);
		if (info == null)
		{
			info = createResourceInfo(resource);
		}
		MarkerSet markers = info.getMarkers(true);
		if (markers == null)
		{
			markers = new MarkerSet(1);
		}

		basicAdd(resource, markers, marker);
		if (!markers.isEmpty())
		{
			info.setMarkers(markers);
		}

		IMarkerSetElement[] changes = new IMarkerSetElement[1];
		changes[0] = new MarkerDelta(IResourceDelta.ADDED, resource, marker);
		changedMarkers(resource, changes);
	}

	/**
	 * isPersistent
	 * 
	 * @param info
	 * @return boolean
	 */
	public boolean isPersistent(MarkerInfo info)
	{
		if (!cache.isPersistent(info.getType()))
		{
			return false;
		}
		Object isTransient = info.getAttribute(IMarker.TRANSIENT);
		return isTransient == null || !(isTransient instanceof Boolean) || !((Boolean) isTransient).booleanValue();
	}

	/**
	 * removeMarker
	 * 
	 * @param resource
	 * @param id
	 * @throws CoreException
	 */
	void removeMarker(IUniformResource resource, long id) throws CoreException
	{
		MarkerInfo marker = findMarkerInfo(resource, id);
		if (marker == null)
		{
			return;
		}
		ResourceInfo info = getResourceInfo(resource);
		MarkerSet markers = info.getMarkers(true);
		if (markers != null)
		{
			int size = markers.size();
			markers.remove(marker);
			info.setMarkers(markers.size() == 0 ? null : markers);
			if (markers.size() != size)
			{
				/* TODO: store persistent marker state */
				IMarkerSetElement[] changes = new IMarkerSetElement[] { new MarkerDelta(IResourceDelta.REMOVED,
						resource, marker) };
				changedMarkers(resource, changes);
			}
		}
		else
		{
			IdeLog.logInfo(CorePlugin.getDefault(), MessageFormat.format(
					"Could not remove the marker with the id {0}. The resource-info returned a null marker-set.", id)); //$NON-NLS-1$
		}
	}

	/**
	 * changedMarkers
	 * 
	 * @param resource
	 * @param changes
	 * @throws CoreException
	 */
	void changedMarkers(IUniformResource resource, IMarkerSetElement[] changes) throws CoreException
	{
		if (changes == null || changes.length == 0)
		{
			return;
		}
		URI uri = resource.getURI();
		synchronized (lock)
		{
			if (currentDeltas == null)
			{
				currentDeltas = new HashMap();
			}
			MarkerSet previousChanges = (MarkerSet) currentDeltas.get(uri);
			MarkerSet result = MarkerDelta.merge(previousChanges, changes);
			if (result.size() == 0)
			{
				currentDeltas.remove(uri);
			}
			else
			{
				currentDeltas.put(uri, result);
			}
		}

		if (getRootMarker() != null)
		{
			getRootMarker().setAttribute("updateId", getRootMarker().getAttribute("updateId", 0) + 1); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private synchronized IMarker getRootMarker()
	{
		if (rootMarker == null)
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			try
			{
				rootMarker = workspace.getRoot().createMarker(
						"com.aptana.ide.internal.core.resources.ExternalResourcesMarker"); //$NON-NLS-1$
			}
			catch (CoreException e)
			{
				IdeLog.logError(CorePlugin.getDefault(), e);
			}
		}
		return rootMarker;
	}

	/**
	 * isSubtype
	 * 
	 * @param type
	 * @param superType
	 * @return boolean
	 */
	boolean isSubtype(String type, String superType)
	{
		return cache.isSubtype(type, superType);
	}

	/**
	 * findMarkersInfo
	 * 
	 * @param resource
	 * @param type
	 * @param includeSubtypes
	 * @return MarkerInfo[]
	 */
	public MarkerInfo[] findMarkersInfo(IUniformResource resource, String type, boolean includeSubtypes)
	{
		ResourceInfo info = getResourceInfo(resource);
		if (info == null)
		{
			return NO_MARKER_INFO;
		}

		MarkerSet markers = info.getMarkers(false);
		if (markers == null)
		{
			return NO_MARKER_INFO;
		}

		IMarkerSetElement[] elements = markers.elements();
		ArrayList result = new ArrayList(elements.length);
		for (int i = 0; i < elements.length; ++i)
		{
			MarkerInfo marker = (MarkerInfo) elements[i];
			if (type == null)
			{
				result.add(marker);
			}
			else
			{
				if (includeSubtypes)
				{
					if (isSubtype(marker.getType(), type))
					{
						result.add(marker);
					}
				}
				else
				{
					if (marker.getType().equals(type))
					{
						result.add(marker);
					}
				}
			}
		}
		if (result.size() == 0)
		{
			return NO_MARKER_INFO;
		}
		return (MarkerInfo[]) result.toArray(new MarkerInfo[result.size()]);
	}

	private ResourceInfo getResourceInfo(IUniformResource resource)
	{
		return (ResourceInfo) resources.get(resource.getURI());
	}

	private ResourceInfo createResourceInfo(IUniformResource resource)
	{
		ResourceInfo info = new ResourceInfo();
		resources.put(resource.getURI(), info);
		return info;
	}

	private void basicAdd(IUniformResource resource, MarkerSet markers, MarkerInfo newMarker) throws CoreException
	{
		if (newMarker.getId() != MarkerInfo.UNDEFINED_ID)
		{
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK,
					Messages.MarkerManager_MarkerIDIsDefined, null));
		}
		newMarker.setId(nextMarkerId());
		markers.add(newMarker);
		/* TODO: store persistent marker state */
	}

	private long nextMarkerId()
	{
		return nextMarkerId++;
	}

	/**
	 * addResourceChangeListener
	 * 
	 * @param listener
	 */
	public void addResourceChangeListener(IUniformResourceChangeListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * removeResourceChangeListener
	 * 
	 * @param listener
	 */
	public void removeResourceChangeListener(IUniformResourceChangeListener listener)
	{
		listeners.remove(listener);
	}

	private void handleResourceChanged()
	{
		if (currentDeltas == null)
		{
			return;
		}
		MarkerSet[] markers;
		synchronized (lock)
		{
			markers = (MarkerSet[]) currentDeltas.values().toArray(new MarkerSet[currentDeltas.size()]);
			currentDeltas = null;
		}
		Object[] list = listeners.getListeners();
		for (int j = 0; j < markers.length; ++j)
		{
			IMarkerDelta[] deltas = new IMarkerDelta[markers[j].size()];
			markers[j].copyInto(deltas);
			IUniformResource resource = null;
			if (deltas.length > 0 && deltas[0] instanceof MarkerDelta)
			{
				resource = ((MarkerDelta) deltas[0]).getUniformResource();
			}
			UniformResourceChangeEvent event = new UniformResourceChangeEvent(this, resource, deltas);
			for (int i = 0; i < list.length; ++i)
			{
				try
				{
					((IUniformResourceChangeListener) list[i]).resourceChanged(event);
				}
				catch (Exception e)
				{
					IdeLog.logError(CorePlugin.getDefault(), e);
				}
			}
		}
	}
}
