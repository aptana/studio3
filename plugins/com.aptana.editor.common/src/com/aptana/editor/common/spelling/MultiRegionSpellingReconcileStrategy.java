/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.spelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingAnnotation;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;

/**
 * @author Max Stepanov
 *
 */
public class MultiRegionSpellingReconcileStrategy extends SpellingReconcileStrategy {

	private final String documentPartitioning;
	private final Collection<String> contentTypes;
	private IRegion currentRegion;

	/**
	 * @param viewer
	 * @param spellingService
	 */
	public MultiRegionSpellingReconcileStrategy(ISourceViewer viewer, SpellingService spellingService, String documentPartitioning, Collection<String> contentTypes) {
		super(viewer, spellingService);
		this.documentPartitioning = documentPartitioning;
		this.contentTypes = contentTypes;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#createSpellingProblemCollector()
	 */
	@Override
	protected ISpellingProblemCollector createSpellingProblemCollector() {
		IAnnotationModel model = getAnnotationModel();
		if (model == null) {
			return null;
		}
		return new SpellingProblemCollector(model);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#initialReconcile()
	 */
	@Override
	public void initialReconcile() {
		for (ITypedRegion region : computePartitioning(0, getDocument().getLength())) {
			reconcile(region);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	@Override
	public void reconcile(IRegion region) {
		if (getAnnotationModel() == null) {
			return;
		}
		try {
			currentRegion = region;
			if (region instanceof ITypedRegion && !contentTypes.contains(((ITypedRegion) region).getType())) {
				ISpellingProblemCollector collector = createSpellingProblemCollector();
				collector.beginCollecting();
				collector.endCollecting();
			} else {
				super.reconcile(region);
			}
		} finally {
			currentRegion = null;
		}
	}

	private String getDocumentPartitioning()
	{
		return documentPartitioning;
	}

	/**
	 * Computes and returns the partitioning for the given region of the input document
	 * of the reconciler's connected text viewer.
	 *
	 * @param offset the region offset
	 * @param length the region length
	 * @return the computed partitioning
	 * @since 3.0
	 */
	private ITypedRegion[] computePartitioning(int offset, int length)
	{
		ITypedRegion[] regions = null;
		try
		{
			regions = TextUtilities.computePartitioning(getDocument(), getDocumentPartitioning(), offset, length, false);
		} catch (BadLocationException x)
		{
			regions = new TypedRegion[0];
		}
		return regions;
	}

	/**
	 * Spelling problem collector.
	 */
	private class SpellingProblemCollector implements ISpellingProblemCollector {

		private IAnnotationModel fAnnotationModel;
		private Map<Annotation, Position> fAddAnnotations;
		private Object fLockObject;

		/**
		 * Initializes this collector with the given annotation model.
		 *
		 * @param annotationModel
		 *            the annotation model
		 */
		public SpellingProblemCollector(IAnnotationModel annotationModel) {
			Assert.isLegal(annotationModel != null);
			fAnnotationModel = annotationModel;
			if (fAnnotationModel instanceof ISynchronizable) {
				fLockObject = ((ISynchronizable) fAnnotationModel).getLockObject();
			} else {
				fLockObject = fAnnotationModel;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#accept(org.eclipse.ui.texteditor.spelling.SpellingProblem)
		 */
		public void accept(SpellingProblem problem) {
			fAddAnnotations.put(new SpellingAnnotation(problem), new Position(problem.getOffset(), problem.getLength()));
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#beginCollecting()
		 */
		public void beginCollecting() {
			fAddAnnotations = new HashMap<Annotation, Position>();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#endCollecting()
		 */
		@SuppressWarnings("rawtypes")
		public void endCollecting() {

			List<Annotation> toRemove = new ArrayList<Annotation>();

			synchronized (fLockObject) {
				if (fAnnotationModel instanceof IAnnotationModelExtension2 && currentRegion != null) {
					for (Iterator iter = ((IAnnotationModelExtension2) fAnnotationModel).getAnnotationIterator(currentRegion.getOffset(), currentRegion.getLength(), true, true); iter.hasNext(); ) {
						Annotation annotation = (Annotation) iter.next();
						if (SpellingAnnotation.TYPE.equals(annotation.getType())) {
							toRemove.add(annotation);
						}
					}
				} else {
					for (Iterator iter = fAnnotationModel.getAnnotationIterator(); iter.hasNext(); ) {
						Annotation annotation = (Annotation) iter.next();
						if (SpellingAnnotation.TYPE.equals(annotation.getType())) {
							Position pos = currentRegion != null ? fAnnotationModel.getPosition(annotation) : null;
							if (currentRegion == null || pos.overlapsWith(currentRegion.getOffset(), currentRegion.getLength())) {
								toRemove.add(annotation);
							}
						}
					}
				}
				Annotation[] annotationsToRemove = (Annotation[]) toRemove.toArray(new Annotation[toRemove.size()]);

				if (fAnnotationModel instanceof IAnnotationModelExtension) {
					((IAnnotationModelExtension) fAnnotationModel).replaceAnnotations(annotationsToRemove, fAddAnnotations);
				} else {
					for (int i = 0; i < annotationsToRemove.length; i++)
						fAnnotationModel.removeAnnotation(annotationsToRemove[i]);
					for (Iterator iter = fAddAnnotations.keySet().iterator(); iter.hasNext(); ) {
						Annotation annotation = (Annotation) iter.next();
						fAnnotationModel.addAnnotation(annotation, (Position) fAddAnnotations.get(annotation));
					}
				}
			}

			fAddAnnotations = null;
		}
	}

}
