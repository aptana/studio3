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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonEditorSystemProperties;
import com.aptana.editor.common.IDebugScopes;
import com.aptana.editor.common.Regions;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class CommonPresentationReconciler extends PresentationReconciler
{
	private int iterationPartitionLimit = 4000;
	private int backgroundReconcileDelay = 2000;
	private int iterationDelay = 500;
	private int minimalVisibleLength = 20000;

	private ITextViewer textViewer;
	private Regions delayedRegions = new Regions();
	private IRegion viewerVisibleRegion;
	private Job job;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.PresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	@Override
	public void install(ITextViewer viewer)
	{
		super.install(viewer);
		delayedRegions.clear();
		textViewer = viewer;
		iterationPartitionLimit = Integer.getInteger(
				ICommonEditorSystemProperties.RECONCILER_ITERATION_PARTITION_LIMIT, iterationPartitionLimit);
		backgroundReconcileDelay = Integer.getInteger(ICommonEditorSystemProperties.RECONCILER_BACKGROUND_DELAY,
				backgroundReconcileDelay);
		iterationDelay = Integer.getInteger(ICommonEditorSystemProperties.RECONCILER_ITERATION_DELAY, iterationDelay);
		minimalVisibleLength = Integer.getInteger(ICommonEditorSystemProperties.RECONCILER_MINIMAL_VISIBLE_LENGTH,
				minimalVisibleLength);
		if (IdeLog.isTraceEnabled(CommonEditorPlugin.getDefault(), IDebugScopes.PRESENTATION))
		{
			IdeLog.logTrace(
					CommonEditorPlugin.getDefault(),
					MessageFormat
							.format("Reconciling process set for partition limit of {0} partitions, background delay of {1}ms, iteration delay of {2}ms, and minimal visible length of {3} lines", //$NON-NLS-1$
									iterationPartitionLimit, backgroundReconcileDelay, iterationDelay,
									minimalVisibleLength), IDebugScopes.PRESENTATION);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.PresentationReconciler#uninstall()
	 */
	@Override
	public void uninstall()
	{
		if (job != null)
		{
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
		if (IdeLog.isInfoEnabled(CommonEditorPlugin.getDefault(), IDebugScopes.PRESENTATION))
		{
			IdeLog.logInfo(
					CommonEditorPlugin.getDefault(),
					MessageFormat
							.format("Initiating presentation reconciling for region at offset {0}, length {1} in document of length {2}", //$NON-NLS-1$
									damage.getOffset(), damage.getLength(), document.getLength()),
					IDebugScopes.PRESENTATION);
		}
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

	private TextPresentation createPresentation(IRegion damage, IDocument document, IProgressMonitor monitor)
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
			TextPresentation presentation = new TextPresentation(damage, iterationPartitionLimit * 5);
			ITypedRegion[] partitioning = TextUtilities.computePartitioning(document, getDocumentPartitioning(),
					damageOffset, damageLength, false);
			if (partitioning.length == 0)
			{
				return presentation;
			}
			int limit = Math.min(iterationPartitionLimit, partitioning.length);
			int processingLength = partitioning[limit - 1].getOffset() + partitioning[limit - 1].getLength()
					- damageOffset;
			if (EclipseUtil.showSystemJobs())
			{
				monitor.subTask(MessageFormat.format(
						"processing region at offset {0}, length {1} in document of length {2}", damageOffset, //$NON-NLS-1$
						processingLength, document.getLength()));
			}

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
				monitor.worked(r.getLength());
			}

			synchronized (this)
			{
				delayedRegions.remove(new Region(damageOffset, processingLength));
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
			final TextPresentation[] presentation = new TextPresentation[1];
			synchronized (getLockObject(document))
			{
				presentation[0] = createPresentation(damage, document, monitor);
			}
			if (presentation[0] != null)
			{
				UIUtils.getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						ITextViewer viewer = textViewer;
						if (viewer != null)
						{
							try
							{
								StyledText widget = viewer.getTextWidget();
								if (widget != null && !widget.isDisposed())
								{
									viewer.changeTextPresentation(presentation[0], true);
								}
								// save visible region here since UI thread access required
								int topOffset = viewer.getTopIndexStartOffset();
								int length = viewer.getBottomIndexEndOffset() - topOffset;
								viewerVisibleRegion = new Region(topOffset, Math.max(length, minimalVisibleLength));
							}
							catch (Exception e)
							{
								IdeLog.logWarning(CommonEditorPlugin.getDefault(),
										"Problem with processing text presentation: " + e.getMessage()); //$NON-NLS-1$
							}
						}
					}
				});
			}
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
			// TODO Is there any reason this needs to be a job? can we just use a Thread?
			job = new Job("Delayed Presentation Reconciler") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					int priority = Thread.currentThread().getPriority();
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					IDocument document = textViewer != null ? textViewer.getDocument() : null;
					if (document == null)
					{
						return Status.CANCEL_STATUS;
					}
					monitor.beginTask("Reconciling document", document.getLength()); //$NON-NLS-1$
					while (textViewer != null && !monitor.isCanceled())
					{
						IRegion damage = nextDamagedRegion();
						if (damage == null || monitor.isCanceled() || textViewer == null)
						{
							break;
						}
						processDamage(damage, textViewer.getDocument(), monitor);
						try
						{
							Thread.sleep(iterationDelay);
						}
						catch (InterruptedException e)
						{
							break;
						}
					}
					monitor.done();
					Thread.currentThread().setPriority(priority);
					return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
				}
			};
			job.setPriority(Job.DECORATE);
			EclipseUtil.setSystemForJob(job);
		}
		if (!delayedRegions.isEmpty())
		{
			job.schedule(backgroundReconcileDelay);
		}
	}

	private IRegion nextDamagedRegion()
	{
		if (viewerVisibleRegion == null && textViewer != null)
		{
			UIUtils.getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					ITextViewer viewer = textViewer;
					if (viewer == null)
					{
						return;
					}
					int topOffset = viewer.getTopIndexStartOffset();
					int length = viewer.getBottomIndexEndOffset() - topOffset;
					viewerVisibleRegion = new Region(topOffset, Math.max(length, minimalVisibleLength));
				}
			});
		}
		synchronized (this)
		{
			if (delayedRegions.isEmpty())
			{
				return null;
			}
			if (viewerVisibleRegion != null)
			{
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
