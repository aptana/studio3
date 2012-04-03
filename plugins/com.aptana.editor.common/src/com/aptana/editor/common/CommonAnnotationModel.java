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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;

/**
 * Annotation Model for {@link IProblem}s. This model is used to draw annotations on the editor for
 * problems/tasks/warnings foudn during reconcile.
 * 
 * @author cwilliams
 */
public class CommonAnnotationModel extends ResourceMarkerAnnotationModel
{

	private List<ProblemAnnotation> fGeneratedAnnotations;
	private IProgressMonitor fProgressMonitor;

	public CommonAnnotationModel(IResource resource)
	{
		super(resource);
		fGeneratedAnnotations = new ArrayList<ProblemAnnotation>();
	}

	/**
	 * Signals the end of problem reporting.
	 * 
	 * @param map
	 *            the map of Marker types to collection of "markers/problems" to report
	 */
	public void reportProblems(Map<String, Collection<IProblem>> map)
	{
		if (fProgressMonitor != null && fProgressMonitor.isCanceled())
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
			if (fGeneratedAnnotations.size() > 0)
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

							if (fProgressMonitor != null && fProgressMonitor.isCanceled())
							{
								break;
							}

							Position position = generatePosition(problem);
							if (position != null)
							{
								try
								{
									ProblemAnnotation annotation = new ProblemAnnotation(problem);
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

	public void setProgressMonitor(IProgressMonitor monitor)
	{
		fProgressMonitor = monitor;
	}
}
