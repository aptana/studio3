/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.IFilter;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.build.ReconcileContext;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonAnnotationModel;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;

public class CommonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension,
		IBatchReconcilingStrategy, IDisposableReconcilingStrategy
{

	/**
	 * The editor we're operating on.
	 */
	private volatile AbstractThemeableEditor fEditor;
	/**
	 * The working copy we're operating on.
	 */
	private IDocument fDocument;

	private boolean fInitialReconcileDone;
	private IProgressMonitor fMonitor;
	/**
	 * The folder that calculates folding positions for this editor.
	 */
	private IFoldingComputer folder;

	/**
	 * Any access to fPositions should obtain this lock.
	 */
	private final Object fPositionsLock = new Object();

	/**
	 * Code Folding.
	 */
	private Map<ProjectionAnnotation, Position> fPositions = new HashMap<ProjectionAnnotation, Position>();

	private IPropertyListener propertyListener = new IPropertyListener()
	{
		public void propertyChanged(Object source, int propId)
		{
			if (propId == IEditorPart.PROP_INPUT)
			{
				reconcile(false, true);
			}
		}
	};

	public CommonReconcilingStrategy(AbstractThemeableEditor editor)
	{
		fEditor = editor;
		fEditor.addPropertyListener(propertyListener);
	}

	public void dispose()
	{
		if (fEditor != null)
		{
			fEditor.removePropertyListener(propertyListener);
			fEditor = null;
		}
		synchronized (fPositionsLock)
		{
			fPositions.clear();
		}
	}

	protected AbstractThemeableEditor getEditor()
	{
		return fEditor;
	}

	protected IDocument getDocument()
	{
		return fDocument;
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
	protected void calculatePositions(boolean initialReconcile, IProgressMonitor monitor, IParseRootNode ast)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}

		// Folding...

		try
		{
			Map<ProjectionAnnotation, Position> positions = folder.emitFoldingRegions(initialReconcile, monitor, ast);
			synchronized (fPositionsLock)
			{
				fPositions = positions;
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		// If we had all positions we shouldn't probably listen to cancel, but we may have exited emitFoldingRegions
		// early because of cancel...
		if (monitor != null && monitor.isCanceled() || !shouldUpdatePositions(folder))
		{
			return;
		}

		updatePositions();
	}

	/**
	 * A hook that can prevent folding position update when needed.
	 * 
	 * @param folder
	 * @return <code>true</code> by default. Subclasses may override.
	 */
	protected boolean shouldUpdatePositions(IFoldingComputer folder)
	{
		return true;
	}

	// Delete all the positions in the document
	protected void clearPositions(IProgressMonitor monitor)
	{
		if (monitor != null && monitor.isCanceled())
		{
			return;
		}
		// clear folding positions
		synchronized (fPositionsLock)
		{
			fPositions.clear();
		}
	}

	/**
	 * Update the folding positions in the document
	 */
	protected void updatePositions()
	{
		AbstractThemeableEditor editor = fEditor;
		if (editor != null)
		{
			HashMap<ProjectionAnnotation, Position> positions;
			synchronized (fPositionsLock)
			{
				// Create a copy to pass to updateFoldingStructure, as it may take more time there.
				positions = new HashMap<ProjectionAnnotation, Position>(fPositions);
			}
			editor.updateFoldingStructure(positions);
		}
	}

	private void reconcile(boolean initialReconcile)
	{
		reconcile(initialReconcile, false);
	}

	private void reconcile(boolean initialReconcile, boolean force)
	{
		SubMonitor monitor = SubMonitor.convert(fMonitor, 100);

		IParseRootNode ast = null;
		AbstractThemeableEditor editor = fEditor;
		if (editor != null)
		{
			ast = editor.getAST();

			// The call to get the ast can get a long time, so, let's check our field again.
			editor = fEditor;
			if (editor != null)
			{
				editor.refreshOutline(ast);
			}
		}
		monitor.worked(5);

		// FIXME only do folding and validation when the source was changed
		if (editor != null && editor.isFoldingEnabled())
		{
			calculatePositions(initialReconcile, monitor.newChild(20), ast);
		}
		else
		{
			synchronized (fPositionsLock)
			{
				fPositions.clear();
			}
			updatePositions();
		}
		monitor.setWorkRemaining(75);

		if (monitor.isCanceled())
		{
			return;
		}

		runParticipants(monitor.newChild(75));
	}

	public Map<ProjectionAnnotation, Position> getPositions()
	{
		synchronized (fPositionsLock)
		{
			return new HashMap<ProjectionAnnotation, Position>(fPositions);
		}
	}

	/**
	 * Runs through the {@link IBuildParticipant}s that apply to this editor's underlying file.
	 * 
	 * @param monitor
	 */
	private void runParticipants(IProgressMonitor monitor)
	{
		AbstractThemeableEditor editor = fEditor;
		if (editor == null)
		{
			return;
		}
		// if file is in the workspace, check if it's valid.
		// (We only want to build files that exist and aren't derived or team private!)
		// Otherwise it's an external file, so just assume it is.
		IFile file = getFile();
		if (file != null && ResourceUtil.shouldIgnore(file))
		{
			return;
		}

		// Grab the list of participants that apply to the editor's content type.
		List<IBuildParticipant> participants = getBuildParticipantManager().getBuildParticipants(
				editor.getContentType());
		if (CollectionsUtil.isEmpty(participants))
		{
			return;
		}

		// Now filter based on enablement preferences...
		participants = filterToEnabled(participants);
		if (CollectionsUtil.isEmpty(participants))
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, (participants.size() * 12) + 10);
		ReconcileContext context = createContext();
		if (context == null)
		{
			return;
		}
		for (IBuildParticipant participant : participants)
		{
			participant.buildStarting(context.getProject(), IncrementalProjectBuilder.INCREMENTAL_BUILD,
					sub.newChild(1));
		}
		for (IBuildParticipant participant : participants)
		{
			participant.buildFile(context, sub.newChild(10));
		}
		for (IBuildParticipant participant : participants)
		{
			participant.buildEnding(sub.newChild(1));
		}
		reportProblems(context, sub.newChild(10));
		sub.done();
	}

	protected IBuildParticipantManager getBuildParticipantManager()
	{
		return BuildPathCorePlugin.getDefault().getBuildParticipantManager();
	}

	/**
	 * Creates and returns a {@link ReconcileContext}.
	 * 
	 * @return A {@link ReconcileContext}.
	 */
	protected ReconcileContext createContext()
	{
		AbstractThemeableEditor editor = fEditor;
		if (editor == null)
		{
			return null;
		}
		IFile file = getFile();
		if (file != null)
		{
			return new ReconcileContext(editor.getContentType(), file, fDocument.get())
			{
				@Override
				public IParseRootNode getAST() throws CoreException
				{
					if (fEditor != null)
					{
						return fEditor.getAST();
					}
					return super.getAST();
				}

				@Override
				public Collection<IParseError> getParseErrors()
				{
					if (fEditor != null)
					{
						ParseResult pr = fEditor.getParseResult();
						if (pr != null)
						{
							return pr.getErrors();
						}
					}
					return super.getParseErrors();
				}
			};
		}

		return new ReconcileContext(editor.getContentType(), EditorUtil.getURI(editor), fDocument.get())
		{
			@Override
			public IParseRootNode getAST() throws CoreException
			{
				if (fEditor != null)
				{
					return fEditor.getAST();
				}
				return super.getAST();
			}
		};
	}

	private List<IBuildParticipant> filterToEnabled(List<IBuildParticipant> participants)
	{
		return CollectionsUtil.filter(participants, new IFilter<IBuildParticipant>()
		{
			public boolean include(IBuildParticipant item)
			{
				return item != null && item.isEnabled(BuildType.RECONCILE);
			}
		});
	}

	/**
	 * Reports problems found in reconcile to the annotation model so we can draw them on the editor without creating
	 * markers on the underlying resource.
	 * 
	 * @param context
	 * @param monitor
	 */
	private void reportProblems(ReconcileContext context, IProgressMonitor monitor)
	{
		AbstractThemeableEditor editor = fEditor;
		if (editor == null)
		{
			return;
		}

		IDocumentProvider docProvider = editor.getDocumentProvider();
		if (docProvider == null)
		{
			return;
		}

		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput == null)
		{
			return;
		}

		IAnnotationModel model = docProvider.getAnnotationModel(editorInput);
		if (!(model instanceof ICommonAnnotationModel))
		{
			return;
		}

		ICommonAnnotationModel caModel = (ICommonAnnotationModel) model;
		// Now report them all to the annotation model!
		caModel.reportProblems(context.getProblems(), monitor);
	}

	protected IFile getFile()
	{
		AbstractThemeableEditor editor = fEditor;
		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

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
