/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;

import com.aptana.editor.common.IFoldingEditor;

public class AbstractFoldingEditor extends AbstractDecoratedTextEditor implements IFoldingEditor
{

	private Map<ProjectionAnnotation, Position> oldAnnotations = new HashMap<ProjectionAnnotation, Position>(3);

	/**
	 * AbstractFoldingEditor
	 */
	public AbstractFoldingEditor()
	{
		super();
	}

	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		ProjectionSupport projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		projectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
		projectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
		projectionSupport.install();

		viewer.doOperation(ProjectionViewer.TOGGLE);
	}

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IFoldingEditor#updateFoldingStructure(java.util.List)
	 */
	public void updateFoldingStructure(List<Position> positions)
	{
		// The map we'll use to overwrite oldAnnotations with later
		Map<ProjectionAnnotation, Position> newAnnotationMap = new HashMap<ProjectionAnnotation, Position>();
		// The map of brand new positions
		Map<ProjectionAnnotation, Position> toAdd = new HashMap<ProjectionAnnotation, Position>();
		for (Position position : positions)
		{
			ProjectionAnnotation annotation = findAnnotationWithPosition(position);
			if (annotation == null)
			{
				// this is actually a brand new position, throw it on toAdd
				annotation = new ProjectionAnnotation();
				toAdd.put(annotation, position);
			}
			newAnnotationMap.put(annotation, position);
		}

		List<ProjectionAnnotation> toDelete = findDeletedAnnotations(newAnnotationMap);
		ProjectionAnnotationModel model = getAnnotationModel();
		if (model != null)
		{
			model.modifyAnnotations(toDelete.toArray(new ProjectionAnnotation[toDelete.size()]), toAdd,
					new ProjectionAnnotation[0]);
		}
		oldAnnotations = newAnnotationMap;
	}

	protected ProjectionAnnotationModel getAnnotationModel()
	{
		ISourceViewer viewer = getSourceViewer();
		if (viewer instanceof ProjectionViewer)
		{
			return ((ProjectionViewer) viewer).getProjectionAnnotationModel();
		}
		return null;
	}

	/**
	 * Traverses our last map of saved annotations and checks for any there were in there, but are not in our new map.
	 * That comprises the set of annotations to be deleted.
	 * 
	 * @param newAnnotationMap
	 * @return
	 */
	private List<ProjectionAnnotation> findDeletedAnnotations(Map<ProjectionAnnotation, Position> newAnnotationMap)
	{
		List<ProjectionAnnotation> toDelete = new ArrayList<ProjectionAnnotation>();
		if (oldAnnotations != null)
		{
			for (ProjectionAnnotation old : oldAnnotations.keySet())
			{
				if (!newAnnotationMap.containsKey(old)) // old isn't in new set, needs to be deleted
				{
					toDelete.add(old);
				}
			}
		}
		return toDelete;
	}

	private ProjectionAnnotation findAnnotationWithPosition(Position position)
	{
		ProjectionAnnotationModel model = getAnnotationModel();
		if (model != null)
		{
			Position oldPosition;
			for (Map.Entry<ProjectionAnnotation, Position> oldEntry : oldAnnotations.entrySet())
			{
				oldPosition = model.getPosition(oldEntry.getKey());
				if (oldPosition == null)
				{
					continue;
				}
				if (position.equals(oldPosition))
				{
					return oldEntry.getKey();
				}
			}
		}
		return null;
	}

	/**
	 * This code auto-refreshes files that are out of synch when we first open them. This is a bit of a hack that looks
	 * to see if it seems we're out of sync and the file isn't open yet. If it is already open, we call super so it pops
	 * a dialog asking if you want to update the file contents.
	 */
	@Override
	protected void handleEditorInputChanged()
	{
		final IDocumentProvider provider = getDocumentProvider();
		if (provider == null)
		{
			// fix for http://dev.eclipse.org/bugs/show_bug.cgi?id=15066
			close(false);
			return;
		}

		final IEditorInput input = getEditorInput();
		boolean wasActivated = true;
		try
		{
			Field f = AbstractTextEditor.class.getDeclaredField("fHasBeenActivated"); //$NON-NLS-1$
			f.setAccessible(true);
			wasActivated = (Boolean) f.get(this);
		}
		catch (Exception e1)
		{
			// ignore
		}
		if (!wasActivated && !provider.isDeleted(input))
		{
			try
			{
				if (provider instanceof IDocumentProviderExtension)
				{
					IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;
					extension.synchronize(input);
				}
				else
				{
					doSetInput(input);
				}
				return;
			}
			catch (CoreException e)
			{
				// ignore
			}
		}
		super.handleEditorInputChanged();
	}

	@Override
	public void dispose()
	{
		try
		{
			oldAnnotations = null;
		}
		finally
		{
			super.dispose();
		}
	}
}
