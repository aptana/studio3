/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.reconciler;

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

import com.aptana.core.util.EclipseUtil;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 *
 */
public class CommonPresentationReconciler extends PresentationReconciler {

	private static final int ITERATION_PARTITION_LIMIT = 10000;
	private static final int BACKGROUND_RECONCILE_DELAY = 1000;
	private static final int ITERATION_DELAY = 50;
	
	private ITextViewer textViewer;
	private IRegion delayedRegion;
	private Job job;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.PresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	@Override
	public void install(ITextViewer viewer) {
		super.install(viewer);
		delayedRegion = null;
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
		delayedRegion = null;
		textViewer = null;
		super.uninstall();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.PresentationReconciler#createPresentation(org.eclipse.jface.text.IRegion, org.eclipse.jface.text.IDocument)
	 */
	@Override
	protected TextPresentation createPresentation(IRegion damage, IDocument document) {
		try {
			return createPresentation(damage, document, new NullProgressMonitor());
		} finally {
			triggerDelayedCreatePresentation();
		}
	}
	
	protected TextPresentation createPresentation(IRegion damage, IDocument document, IProgressMonitor monitor) {
		try {
			TextPresentation presentation = new TextPresentation(damage, ITERATION_PARTITION_LIMIT*5);
			ITypedRegion[] partitioning = TextUtilities.computePartitioning(document, getDocumentPartitioning(), damage.getOffset(), damage.getLength(), false);
			int limit = Math.min(ITERATION_PARTITION_LIMIT, partitioning.length);
			for (int i = 0; i < limit; ++i) {
				ITypedRegion r = partitioning[i];
				IPresentationRepairer repairer = getRepairer(r.getType());
				if (monitor.isCanceled()) {
					return null;
				}
				if (repairer != null) {
					repairer.createPresentation(presentation, r);
				}
			}
			delayedExclude(damage.getOffset(), partitioning[limit-1].getOffset()+partitioning[limit-1].getLength());
			if (limit < partitioning.length) {
				int offset = partitioning[limit].getOffset();
				delayedInclude(offset, damage.getOffset() + damage.getLength());
			}
			return presentation;
		} catch (BadLocationException e) {
			return null;
		}
	}

	private synchronized void delayedInclude(int startOffset, int endOffset) {
		if (delayedRegion != null) {
			int offset = Math.min(delayedRegion.getOffset(), startOffset);
			int length = Math.max(delayedRegion.getOffset()+delayedRegion.getLength(), endOffset) - offset;
			delayedRegion = new Region(offset, length);
		} else {
			delayedRegion = new Region(startOffset, endOffset-startOffset);
		}
	}

	private synchronized void delayedExclude(int startOffset, int endOffset) {
		if (delayedRegion != null) {
			if (delayedRegion.getOffset() >= startOffset) {
				int length = delayedRegion.getLength() - (endOffset-delayedRegion.getOffset());
				delayedRegion = length > 0 ? new Region(endOffset, length) : null;				
			} else if (delayedRegion.getOffset()+delayedRegion.getLength() <= endOffset) {
				int length = startOffset - delayedRegion.getOffset();
				delayedRegion = length > 0 ? new Region(delayedRegion.getOffset(), length) : null;				
			}
		}
	}

	private void processDamage(IRegion damage, IDocument document, IProgressMonitor monitor) {
		if (damage != null && damage.getLength() > 0) {
			final TextPresentation[] presentation = new TextPresentation[1];
			synchronized (getLockObject(document)) {
				presentation[0] = createPresentation(damage, document, monitor);
			}
			if (presentation[0] != null) {
				UIUtils.getDisplay().syncExec(new Runnable() {
					public void run() {
						if (textViewer != null) {
							StyledText widget = textViewer.getTextWidget();
							if (widget != null && !widget.isDisposed()) {
								textViewer.changeTextPresentation(presentation[0], false);
							}
						}
					}
				});
			}
		}
	}

	private synchronized void triggerDelayedCreatePresentation() {
		if (job != null) {
			job.cancel();
		} else {
			job = new Job("Delayed Presentation Reconciler") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IDocument document = textViewer.getDocument();
					IRegion damage = delayedRegion;
					while (damage != null && !monitor.isCanceled()) {
						processDamage(damage, document, monitor);
						System.gc();
						try {
							Thread.sleep(ITERATION_DELAY);
						} catch (InterruptedException e) {
							break;
						}
						synchronized (CommonPresentationReconciler.this) {
							damage = delayedRegion;
						}
					}
					return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
				}
			};
			job.setPriority(Job.DECORATE);
			job.setSystem(!EclipseUtil.showSystemJobs());
		}
		if (delayedRegion != null) {
			job.schedule(BACKGROUND_RECONCILE_DELAY);
		}
	}

	private static Object getLockObject(Object object) {
		if (object instanceof ISynchronizable) {
			Object lock = ((ISynchronizable) object).getLockObject();
			if (lock != null) {
				return lock;
			}
		}
		return object;
	}

}
