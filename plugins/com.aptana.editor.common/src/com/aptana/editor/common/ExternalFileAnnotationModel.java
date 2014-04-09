/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;

import com.aptana.core.build.IProblem;
import com.aptana.core.resources.FileStoreUniformResource;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceChangeEvent;
import com.aptana.core.resources.IUniformResourceChangeListener;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.resources.MarkerUtils;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;

public class ExternalFileAnnotationModel extends AbstractMarkerAnnotationModel implements ICommonAnnotationModel
{

	private List<ProblemAnnotation> fGeneratedAnnotations;

	private IUniformResource resource;
	private IUniformResourceChangeListener resourceChangeListener;

	ExternalFileAnnotationModel(IFileStore fileStore)
	{
		resource = new FileStoreUniformResource(fileStore);
		resourceChangeListener = new ResourceChangeListener();
		fGeneratedAnnotations = new ArrayList<ProblemAnnotation>();
	}

	@Override
	protected IMarker[] retrieveMarkers()
	{
		return MarkerUtils.findMarkers(resource, IMarker.MARKER, true);
	}

	@Override
	protected void deleteMarkers(final IMarker[] markers) throws CoreException
	{
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor) throws CoreException
			{
				for (int i = 0; i < markers.length; ++i)
				{
					markers[i].delete();
				}
			}
		}, null, IWorkspace.AVOID_UPDATE, null);
	}

	@Override
	protected void listenToMarkerChanges(boolean listen)
	{
		if (listen)
		{
			MarkerUtils.addResourceChangeListener(resourceChangeListener);
		}
		else
		{
			MarkerUtils.removeResourceChangeListener(resourceChangeListener);
		}
	}

	@Override
	protected boolean isAcceptable(IMarker marker)
	{
		return (marker instanceof IUniformResourceMarker)
				&& resource.equals(((IUniformResourceMarker) marker).getUniformResource());
	}

	/**
	 * Updates this model to the given marker deltas.
	 * 
	 * @param markerDeltas
	 *            the array of marker deltas
	 */
	private void update(IMarkerDelta[] markerDeltas)
	{
		if (markerDeltas.length == 0)
		{
			return;
		}

		for (IMarkerDelta delta : markerDeltas)
		{
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					addMarkerAnnotation(delta.getMarker());
					break;
				case IResourceDelta.REMOVED:
					removeMarkerAnnotation(delta.getMarker());
					break;
				case IResourceDelta.CHANGED:
					modifyMarkerAnnotation(delta.getMarker());
					break;
				default:
					break;
			}
		}
		fireModelChanged();
	}

	/**
	 * Signals the end of problem reporting.
	 * 
	 * @param map
	 *            the map of Marker types to collection of "markers/problems" to report
	 */
	public void reportProblems(Map<String, Collection<IProblem>> map, IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		boolean temporaryProblemsChanged = false;

		// Forcibly remove marker annotations of any particular type we're managing now that we've reconciled...
		try
		{
			IMarker[] markers = retrieveMarkers();
			if (!ArrayUtil.isEmpty(markers))
			{
				for (IMarker marker : markers)
				{
					if (map.containsKey(marker.getType()))
					{
						removeMarkerAnnotation(marker);
					}
				}
			}
		}
		catch (CoreException e)
		{
			// ignore
		}

		synchronized (getLockObject())
		{
			if (!CollectionsUtil.isEmpty(fGeneratedAnnotations))
			{
				temporaryProblemsChanged = true;
				removeAnnotations(fGeneratedAnnotations, false, true);
				fGeneratedAnnotations.clear();
			}

			if (!CollectionsUtil.isEmpty(map))
			{
				for (Collection<IProblem> problems : map.values())
				{
					if (!CollectionsUtil.isEmpty(problems))
					{
						for (IProblem problem : problems)
						{

							if (monitor != null && monitor.isCanceled())
							{
								break;
							}

							Position position = generatePosition(problem);
							if (position != null)
							{
								try
								{
									ProblemAnnotation annotation = new ProblemAnnotation(null, problem);
									addAnnotation(annotation, position, false);
									fGeneratedAnnotations.add(annotation);

									temporaryProblemsChanged = true;
								}
								catch (BadLocationException x)
								{
									// ignore invalid position
								}
							}
						}
					}
				}
			}
		}

		if (temporaryProblemsChanged)
		{
			fireModelChanged();
		}
	}

	private Position generatePosition(IProblem problem)
	{
		int start = problem.getOffset();
		if (start < 0)
		{
			return new Position(0);
		}
		int length = problem.getLength();
		if (length < 0)
		{
			return null;
		}

		return new Position(start, length);
	}

	private class ResourceChangeListener implements IUniformResourceChangeListener
	{

		public void resourceChanged(IUniformResourceChangeEvent event)
		{
			if (resource.equals(event.getResource()))
			{
				update(event.getMarkerDeltas());
			}
		}
	};
}