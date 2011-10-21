/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.reconciler;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.swt.custom.StyledText;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.Regions;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 *
 */
public class CommonPresentationReconciler extends PresentationReconciler {

	private static final int ITERATION_PARTITION_LIMIT = 10000;
	private static final int BACKGROUND_RECONCILE_DELAY = 1000;
	private static final int ITERATION_DELAY = 50;
	private static final int MINIMAL_VISIBLE_LENGTH = 20000;
	
	private ITextViewer textViewer;
	private Regions delayedRegions = new Regions();
	private IRegion viewerVisibleRegion;
	private Job job;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.PresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	@Override
	public void install(ITextViewer viewer) {
		super.install(viewer);
		delayedRegions.clear();
		textViewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.PresentationReconciler#uninstall()
	 */
	@Override
	public void uninstall() {
		if (job != null) {
			job.cancel();
			job = null;
		}
		delayedRegions.clear();
		textViewer = null;
		super.uninstall();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.presentation.PresentationReconciler#createPresentation(org.eclipse.jface.text.IRegion,
	 * org.eclipse.jface.text.IDocument)
	 */
	@Override
	protected TextPresentation createPresentation(IRegion damage, IDocument document)
	{
		synchronized (this)
		{
			delayedRegions.append(damage);
		}
		try
		{
			return createPresentation(nextDamagedRegion(), document, new NullProgressMonitor());
		}
		finally
		{
			triggerDelayedCreatePresentation();
		}
	}

	protected TextPresentation createPresentation(IRegion damage, IDocument document, IProgressMonitor monitor)
	{
		try
		{
			int damageOffset = damage.getOffset();
			int damageLength = damage.getLength();
			if (damageOffset + damageLength > document.getLength())
			{
				int adjustedLength = document.getLength() - damageOffset;
				synchronized (this)
				{
					delayedRegions.remove(new Region(document.getLength(), damageLength - adjustedLength));
				}
				if (adjustedLength <= 0)
				{
					return null;
				}
				damageLength = adjustedLength;
			}
			TextPresentation presentation = new TextPresentation(damage, ITERATION_PARTITION_LIMIT * 5);
			ITypedRegion[] partitioning = TextUtilities.computePartitioning(document, getDocumentPartitioning(),
					damageOffset, damageLength, false);
			if (partitioning.length == 0)
			{
				return presentation;
			}
			int limit = Math.min(ITERATION_PARTITION_LIMIT, partitioning.length);

			for (int i = 0; i < limit; ++i)
			{
				ITypedRegion r = partitioning[i];
				IPresentationRepairer repairer = getRepairer(r.getType());
				if (monitor.isCanceled())
				{
					return null;
				}
				if (repairer != null)
				{
					repairer.createPresentation(presentation, r);
				}
			}

			synchronized (this)
			{
				delayedRegions.remove(new Region(damageOffset, partitioning[limit - 1].getOffset()
						+ partitioning[limit - 1].getLength() - damageOffset));
				if (limit < partitioning.length)
				{
					int offset = partitioning[limit].getOffset();
					delayedRegions.append(new Region(offset, damageOffset + damageLength - offset));
				}
			}
			return presentation;
		}
		catch (BadLocationException e)
		{
			return null;
		}
	}

	private void processDamage(IRegion damage, IDocument document, IProgressMonitor monitor)
	{
		if (damage != null && damage.getLength() > 0)
		{
			SubMonitor sub = SubMonitor.convert(monitor, MessageFormat.format(
					"Processing region at offset {0}, length {1} in document of length {2}", damage.getOffset(), //$NON-NLS-1$
					damage.getLength(), document.getLength()), 2);
			final TextPresentation[] presentation = new TextPresentation[1];
			synchronized (getLockObject(document))
			{
				presentation[0] = createPresentation(damage, document, sub);
			}
			sub.worked(1);
			if (presentation[0] != null)
			{
				UIUtils.getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						if (textViewer != null)
						{
							StyledText widget = textViewer.getTextWidget();
							if (widget != null && !widget.isDisposed())
							{
								textViewer.changeTextPresentation(presentation[0], false);
							}
							// save visible region here since UI thread access required
							int topOffset = textViewer.getTopIndexStartOffset();
							int length = textViewer.getBottomIndexEndOffset() - topOffset;
							viewerVisibleRegion = new Region(topOffset, Math.max(length, MINIMAL_VISIBLE_LENGTH));
						}
					}
				});
			}
			sub.done();
		}
	}

	private synchronized void triggerDelayedCreatePresentation()
	{
		if (job != null)
		{
			job.cancel();
		}
		else
		{
			job = new Job("Delayed Presentation Reconciler") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					while (!monitor.isCanceled())
					{
						IRegion damage = nextDamagedRegion();
						if (damage == null || monitor.isCanceled()) {
							break;
						}
						processDamage(damage, textViewer.getDocument(), monitor);
						System.gc();
						try
						{
							Thread.sleep(ITERATION_DELAY);
						}
						catch (InterruptedException e)
						{
							break;
						}
					}
					return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
				}
			};
			job.setPriority(Job.DECORATE);
			job.setSystem(!EclipseUtil.showSystemJobs());
		}
		if (!delayedRegions.isEmpty())
		{
			job.schedule(BACKGROUND_RECONCILE_DELAY);
		}
	}

	private IRegion nextDamagedRegion() {
		if (viewerVisibleRegion == null) {
			UIUtils.getDisplay().syncExec(new Runnable() {
				public void run() {
					if (textViewer == null) {
						return;
					}
					int topOffset = textViewer.getTopIndexStartOffset();
					int length = textViewer.getBottomIndexEndOffset() - topOffset;
					viewerVisibleRegion = new Region(topOffset, Math.max(length, MINIMAL_VISIBLE_LENGTH));
				}
			});
		}
		synchronized (this)
		{
			if (delayedRegions.isEmpty())
			{
				return null;
			}
			if (viewerVisibleRegion != null) {
			IRegion visible = delayedRegions.overlap(viewerVisibleRegion);
			viewerVisibleRegion = null;
			if (visible != null)
			{
				return visible;
			}
			}
			return delayedRegions.iterator().next();
		}
	}

	private static Object getLockObject(Object object)
	{
		if (object instanceof ISynchronizable)
		{
			Object lock = ((ISynchronizable) object).getLockObject();
			if (lock != null)
			{
				return lock;
			}
		}
		return object;
	}

}
