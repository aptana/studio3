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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.AbstractBaseTextEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IFoldingEditor;
import com.aptana.index.core.IndexFilesOfProjectJob;
import com.aptana.index.core.RemoveIndexOfFilesOfProjectJob;

public class AbstractFoldingEditor extends AbstractBaseTextEditor implements IFoldingEditor
{

	/**
	 * AbstractFoldingEditor
	 */
	public AbstractFoldingEditor()
	{

		// Hack because we cannot override org.eclipse.ui.texteditor.AbstractTextEditor.getSelectionChangedListener().
		ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener()
		{

			private Runnable fRunnable = new Runnable()
			{
				public void run()
				{
					ISourceViewer sourceViewer = getSourceViewer();
					// check whether editor has not been disposed yet
					if (sourceViewer != null && sourceViewer.getDocument() != null)
					{
						updateSelectionDependentActions();
					}
				}
			};

			private Display fDisplay;

			public void selectionChanged(SelectionChangedEvent event)
			{
				Display current = Display.getCurrent();
				if (current != null)
				{
					// Don't execute asynchronously if we're in a thread that has a display.
					// Fix for: https://jira.appcelerator.org/browse/APSTUD-3061 (the rationale
					// is that the actions were not being enabled because they were previously
					// updated in an async call).
					// Ideally, if this is really the root case of that issue, this code should
					// be provided back to Eclipse.org as part of the bug:
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=368354
					// but just patching getSelectionChangedListener() properly.
					fRunnable.run();
				}
				else
				{
					if (fDisplay == null)
					{
						fDisplay = getSite().getShell().getDisplay();
					}
					fDisplay.asyncExec(fRunnable);
				}
				handleCursorPositionChanged();
			}
		};

		try
		{
			// Hack to change private field fSelectionChangedListener
			Field field = AbstractTextEditor.class.getDeclaredField("fSelectionChangedListener"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(this, selectionChangedListener);
		}
		catch (Exception e)
		{
			// Should not really happen, but let's not fail if it happens.
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}

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

	private static final Object lockUpdateFoldingStructure = new Object();

	//@formatter:off
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IFoldingEditor#updateFoldingStructure(java.util.Map) 
	 * 
	 * Note: this object was previously synchronized on the editor and could enter in a deadlock in 
	 * a race condition because of that.
	 * 
	 * Summary: 
	 *  - here, we'd lock the editor here and when updating the positions we'd lock the document
	 * 	- in a document change, we'd lock the document, fire a dirty state, which would try to do
	 *    an enableSanityChecking() which is synchronized on the editor and would lead into a deadlock.
	 * 
	 * @see: APSTUD-7330 Deadlock when updating folding structure.
	 */
	//@formatter:on
	public void updateFoldingStructure(Map<ProjectionAnnotation, Position> annotations)
	{
		synchronized (lockUpdateFoldingStructure)
		{
			List<Annotation> deletions = new ArrayList<Annotation>();
			Collection<Position> additions = annotations.values();
			ProjectionAnnotationModel currentModel = getAnnotationModel();
			if (currentModel == null)
			{
				return;
			}
			for (@SuppressWarnings("rawtypes")
			Iterator iter = currentModel.getAnnotationIterator(); iter.hasNext();)
			{
				Object annotation = iter.next();
				if (annotation instanceof ProjectionAnnotation)
				{
					Position position = currentModel.getPosition((Annotation) annotation);
					if (additions.contains(position))
					{
						additions.remove(position);
					}
					else
					{
						deletions.add((Annotation) annotation);
					}
				}
			}
			if (annotations.size() != 0 || deletions.size() != 0)
			{
				currentModel.modifyAnnotations(deletions.toArray(new Annotation[deletions.size()]), annotations, null);
			}
		}
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
			catch (Exception e)
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
			// force re-indexing of the underlying file since reconciler has modified index entries for it.
			if (isDirty())
			{
				final IFile file = getFile();
				if (file != null)
				{
					new Job("Update index") //$NON-NLS-1$
					{

						@Override
						protected IStatus run(IProgressMonitor monitor)
						{
							SubMonitor sub = SubMonitor.convert(monitor, 100);
							// Wipe and re-index the file
							IProject project = file.getProject();
							Set<IFile> files = CollectionsUtil.newSet(file);
							if (sub.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}
							// we're already in a job, so just run these subjobs in-line rather than schedule them.
							new RemoveIndexOfFilesOfProjectJob(project, files).run(sub.newChild(10));
							new IndexFilesOfProjectJob(project, files).run(sub.newChild(90));

							sub.done();
							return Status.OK_STATUS;
						}
					}.schedule();
				}
			}
		}
		finally
		{
			try
			{
				super.dispose();
			}
			catch (Exception e)
			{
				// ignores
			}
		}
	}

	private IFile getFile()
	{
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			return fileEditorInput.getFile();
		}
		return null;
	}

}
