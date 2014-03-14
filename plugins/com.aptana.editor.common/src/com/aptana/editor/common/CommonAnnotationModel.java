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
import java.util.Map.Entry;

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
 * problems/tasks/warnings found during reconcile.
 * 
 * @author cwilliams
 */
public class CommonAnnotationModel extends ResourceMarkerAnnotationModel implements ICommonAnnotationModel
{

	private List<ProblemAnnotation> fGeneratedAnnotations;

	public CommonAnnotationModel(IResource resource)
	{
		super(resource);
		fGeneratedAnnotations = new ArrayList<ProblemAnnotation>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ICommonAnnotationModel#reportProblems(java.util.Map)
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
				for (Entry<String, Collection<IProblem>> entry : map.entrySet())
				{
					Collection<IProblem> problems = entry.getValue();
					if (CollectionsUtil.isEmpty(problems))
					{
						continue;
					}
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
								String markerId = entry.getKey();
								ProblemAnnotation annotation = new ProblemAnnotation(markerId, problem);
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
}
