/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;

import com.aptana.core.build.IProblem;

/**
 * Annotation representing an <code>IProblem</code>.
 */
public class ProblemAnnotation extends Annotation implements IAnnotationPresentation, IQuickFixableAnnotation
{

	private static final String ERROR_ANNOTATION_TYPE = "com.aptana.editor.common.error"; //$NON-NLS-1$
	private static final String WARNING_ANNOTATION_TYPE = "com.aptana.editor.common.warning"; //$NON-NLS-1$
	private static final String INFO_ANNOTATION_TYPE = "com.aptana.editor.common.info"; //$NON-NLS-1$
	private static final String TASK_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.task"; //$NON-NLS-1$

	/**
	 * The layer in which task problem annotations are located.
	 */
	private static final int TASK_LAYER;
	/**
	 * The layer in which info problem annotations are located.
	 */
	private static final int INFO_LAYER;
	/**
	 * The layer in which warning problem annotations representing are located.
	 */
	private static final int WARNING_LAYER;
	/**
	 * The layer in which error problem annotations representing are located.
	 */
	private static final int ERROR_LAYER;

	static
	{
		AnnotationPreferenceLookup lookup = EditorsUI.getAnnotationPreferenceLookup();
		TASK_LAYER = computeLayer(TASK_ANNOTATION_TYPE, lookup);
		INFO_LAYER = computeLayer(INFO_ANNOTATION_TYPE, lookup);
		WARNING_LAYER = computeLayer(WARNING_ANNOTATION_TYPE, lookup);
		ERROR_LAYER = computeLayer(ERROR_ANNOTATION_TYPE, lookup);
	}

	private static int computeLayer(String annotationType, AnnotationPreferenceLookup lookup)
	{
		Annotation annotation = new Annotation(annotationType, false, null);
		AnnotationPreference preference = lookup.getAnnotationPreference(annotation);
		if (preference != null)
		{
			return preference.getPresentationLayer() + 1;
		}
		else
		{
			return IAnnotationAccessExtension.DEFAULT_LAYER + 1;
		}
	}

	private static Image fgTaskImage;
	private static Image fgInfoImage;
	private static Image fgWarningImage;
	private static Image fgErrorImage;
	private static boolean fgImagesInitialized = false;

	private IProblem fProblem;
	private Image fImage;
	private boolean fImageInitialized = false;
	private int fLayer = IAnnotationAccessExtension.DEFAULT_LAYER;
	private boolean fIsQuickFixable;
	private boolean fIsQuickFixableStateSet = false;
	private String markerId;

	public ProblemAnnotation(String markerId, IProblem problem)
	{
		this.markerId = markerId;
		fProblem = problem;

		if (fProblem.isTask())
		{
			setType(TASK_ANNOTATION_TYPE);
			fLayer = TASK_LAYER;
		}
		else if (fProblem.isWarning())
		{
			setType(WARNING_ANNOTATION_TYPE);
			fLayer = WARNING_LAYER;
		}
		else if (fProblem.isError())
		{
			setType(ERROR_ANNOTATION_TYPE);
			fLayer = ERROR_LAYER;
		}
		else
		{
			setType(INFO_ANNOTATION_TYPE);
			fLayer = INFO_LAYER;
		}
	}

	public int getLayer()
	{
		return fLayer;
	}

	private void initializeImage()
	{
		if (!fImageInitialized)
		{
			initializeImages();
			String type = getType();
			if (TASK_ANNOTATION_TYPE.equals(type))
			{
				fImage = fgTaskImage;
			}
			else if (INFO_ANNOTATION_TYPE.equals(type))
			{
				fImage = fgInfoImage;
			}
			else if (WARNING_ANNOTATION_TYPE.equals(type))
			{
				fImage = fgWarningImage;
			}
			else if (ERROR_ANNOTATION_TYPE.equals(type))
			{
				fImage = fgErrorImage;
			}
			fImageInitialized = true;
		}
	}

	private void initializeImages()
	{
		if (fgImagesInitialized)
		{
			return;
		}

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		fgTaskImage = sharedImages.getImage(SharedImages.IMG_OBJS_TASK_TSK);
		fgInfoImage = sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		fgWarningImage = sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		fgErrorImage = sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

		fgImagesInitialized = true;
	}

	public void paint(GC gc, Canvas canvas, Rectangle r)
	{
		initializeImage();
		if (fImage != null)
		{
			ImageUtilities.drawImage(fImage, gc, canvas, r, SWT.CENTER, SWT.TOP);
		}
	}

	@Override
	public String getText()
	{
		return fProblem.getMessage();
	}

	public void setQuickFixable(boolean state)
	{
		fIsQuickFixable = state;
		fIsQuickFixableStateSet = true;
	}

	public boolean isQuickFixableStateSet()
	{
		return fIsQuickFixableStateSet;
	}

	public boolean isQuickFixable()
	{
		Assert.isTrue(isQuickFixableStateSet());
		return fIsQuickFixable;
	}

	public String getMarkerId()
	{
		return markerId;
	}
}