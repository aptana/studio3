/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.ReconcileContext;
import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonAnnotationModel;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.parsing.ast.IParseNode;

public class CommonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension,
		IBatchReconcilingStrategy
{

	/**
	 * The editor we're operating on.
	 */
	private AbstractThemeableEditor fEditor;
	private boolean fInitialReconcileDone;

	private IProgressMonitor fMonitor;

	/**
	 * The folder that calculates folding positions for this editor.
	 */
	private IFoldingComputer folder;
	/**
	 * Code Folding.
	 */
	private Map<ProjectionAnnotation, Position> fPositions = new HashMap<ProjectionAnnotation, Position>();

	/**
	 * The working copy we're operating on.
	 */
	private IDocument fDocument;

	/**
	 * Flag used to auto-expand outlines to 2nd level on first open.
	 */
	private boolean autoExpanded;

	public CommonReconcilingStrategy(AbstractThemeableEditor editor)
	{
		fEditor = editor;
		fEditor.addPropertyListener(new IPropertyListener()
		{

			public void propertyChanged(Object source, int propId)
			{
				if (propId == IEditorPart.PROP_INPUT)
				{
					reconcile(false, true);
				}
			}
		});
	}

	protected AbstractThemeableEditor getEditor()
	{
		return fEditor;
	}

	public void reconcile(IRegion partition)
	{
		// we can't do incremental yet
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{
		// we can't do incremental yet
	}

	public void setDocument(IDocument document)
	{
		folder = createFoldingComputer(document);
		fDocument = document;
		autoExpanded = false;
	}

	protected IFoldingComputer createFoldingComputer(IDocument document)
	{
		return fEditor.createFoldingComputer(document);
	}

	public void initialReconcile()
	{
		if (fInitialReconcileDone)
		{
			return;
		}
		reconcile(true);
		fInitialReconcileDone = true;
	}

	public void setProgressMonitor(IProgressMonitor monitor)
	{
		fMonitor = monitor;
	}

	// FIXME Can folding be made into a build participant?
	protected void calculatePositions(boolean initialReconcile, IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		// Folding...

		try
		{
			synchronized (fPositions)
			{
				fPositions.clear();
				fPositions = folder.emitFoldingRegions(initialReconcile, monitor);
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		// If we had all positions we shouldn't probably listen to cancel, but we may have exited emitFoldingRegions
		// early because of cancel...
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		updatePositions();
	}

	// Delete all the positions in the document
	protected void clearPositions(IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}
		// clear folding positions
		synchronized (fPositions)
		{
			fPositions.clear();
		}
	}

	/**
	 * Update the folding positions in the document
	 */
	protected void updatePositions()
	{
		fEditor.updateFoldingStructure(fPositions);
	}

	private void reconcile(boolean initialReconcile)
	{
		reconcile(initialReconcile, false);
	}

	private void reconcile(boolean initialReconcile, boolean force)
	{
		// Refresh the outline...
		// TODO Does this need to be run in asyncExec here?
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (fEditor.hasOutlinePageCreated())
				{
					IParseNode node = fEditor.getAST();

					if (node != null)
					{
						CommonOutlinePage page = fEditor.getOutlinePage();
						page.refresh();
						if (!autoExpanded)
						{
							page.expandToLevel(2);
							autoExpanded = true;
						}
					}
				}
			}
		});

		// FIXME only do folding and validation when the source was changed
		if (fEditor.isFoldingEnabled())
		{
			calculatePositions(initialReconcile, fMonitor);
		}
		else
		{
			synchronized (fPositions)
			{
				fPositions.clear();
			}
			updatePositions();
		}
		if (fMonitor != null && fMonitor.isCanceled())
		{
			return;
		}
		runParticipants();

	}

	private void runParticipants()
	{
		// FIXME Fold the validation into build participants...
		final IFile file = getFile();
		if (file == null)
		{
			return;
		}

		String contentTypeId = fEditor.getContentType();
		ReconcileContext context = new ReconcileContext(contentTypeId, file, fDocument.get());
		IBuildParticipantManager manager = BuildPathCorePlugin.getDefault().getBuildParticipantManager();
		List<IBuildParticipant> participants = manager.getBuildParticipants(contentTypeId);
		if (participants != null && !participants.isEmpty())
		{
			SubMonitor sub = SubMonitor.convert(fMonitor, participants.size());
			for (IBuildParticipant participant : participants)
			{
				participant.buildFile(context, sub.newChild(1));
			}
			sub.done();
		}

		reportProblems(context);
	}

	/**
	 * Reports problems found in reconcile to the annotation model so we can draw them on the editor without creating
	 * markers on the underlying resource.
	 * 
	 * @param context
	 */
	private void reportProblems(ReconcileContext context)
	{
		IAnnotationModel model = fEditor.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
		if (model instanceof CommonAnnotationModel)
		{
			CommonAnnotationModel caModel = (CommonAnnotationModel) model;
			caModel.setProgressMonitor(fMonitor);

			// Collect all the problems into a single collection...
			Map<String, Collection<IProblem>> mapProblems = context.getProblems();
			Collection<IProblem> problems = new ArrayList<IProblem>();
			for (Collection<IProblem> blah : mapProblems.values())
			{
				problems.addAll(blah);
			}
			// Now report them all to the annotation model!
			caModel.reportProblems(problems);

			caModel.setProgressMonitor(null);
		}
	}

	private IFile getFile()
	{
		if (fEditor != null)
		{
			IEditorInput editorInput = fEditor.getEditorInput();

			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				return fileEditorInput.getFile();
			}
		}

		return null;
	}

	public void fullReconcile()
	{
		reconcile(false);
	}
}
