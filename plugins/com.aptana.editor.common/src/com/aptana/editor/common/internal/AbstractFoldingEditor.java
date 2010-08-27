package com.aptana.editor.common.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import com.aptana.editor.common.IFoldingEditor;

public class AbstractFoldingEditor extends AbstractDecoratedTextEditor implements IFoldingEditor
{

	private ProjectionAnnotationModel annotationModel;
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

		annotationModel = viewer.getProjectionAnnotationModel();
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
		annotationModel.modifyAnnotations(toDelete.toArray(new ProjectionAnnotation[toDelete.size()]), toAdd,
				new ProjectionAnnotation[0]);
		oldAnnotations = newAnnotationMap;
	}

	/**
	 * Traverses our last map of saved annotations and checks for any there were in there, but are not in our new map.
	 * That comprises the set of annotations to be deleted.
	 * 
	 * @param newAnnotationMap
	 * @return
	 */
	protected List<ProjectionAnnotation> findDeletedAnnotations(Map<ProjectionAnnotation, Position> newAnnotationMap)
	{
		List<ProjectionAnnotation> toDelete = new ArrayList<ProjectionAnnotation>();
		for (ProjectionAnnotation old : oldAnnotations.keySet())
		{
			if (!newAnnotationMap.containsKey(old)) // old isn't in new set, needs to be deleted
			{
				toDelete.add(old);
			}
		}
		return toDelete;
	}

	protected ProjectionAnnotation findAnnotationWithPosition(Position position)
	{
		for (Map.Entry<ProjectionAnnotation, Position> oldEntry : oldAnnotations.entrySet())
		{
			Position oldPosition = annotationModel.getPosition(oldEntry.getKey());
			if (oldPosition == null)
			{
				continue;
			}
			if (position.equals(oldPosition))
			{
				return oldEntry.getKey();
			}
		}
		return null;
	}
}
