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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import com.aptana.core.build.IProblem;

/**
 * Annotation Model for {@link IProblem}s.
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
	 * @param reportedProblems
	 *            the problems to report
	 */
	public void reportProblems(Collection<IProblem> reportedProblems)
	{
		if (fProgressMonitor != null && fProgressMonitor.isCanceled())
		{
			return;
		}

		boolean temporaryProblemsChanged = false;

		// Forcibly remove marker annotations now that we've reconciled...
		try
		{
			IMarker[] markers = retrieveMarkers();
			for (IMarker marker : markers)
			{
				removeMarkerAnnotation(marker);
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

			if (reportedProblems != null)
			{

				for (IProblem problem : reportedProblems)
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
