/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		getAnnotationModel().modifyAnnotations(toDelete.toArray(new ProjectionAnnotation[toDelete.size()]), toAdd,
				new ProjectionAnnotation[0]);
		oldAnnotations = newAnnotationMap;
	}

	protected ProjectionAnnotationModel getAnnotationModel()
	{
		return ((ProjectionViewer) getSourceViewer()).getProjectionAnnotationModel();
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
		for (ProjectionAnnotation old : oldAnnotations.keySet())
		{
			if (!newAnnotationMap.containsKey(old)) // old isn't in new set, needs to be deleted
			{
				toDelete.add(old);
			}
		}
		return toDelete;
	}

	private ProjectionAnnotation findAnnotationWithPosition(Position position)
	{
		for (Map.Entry<ProjectionAnnotation, Position> oldEntry : oldAnnotations.entrySet())
		{
			Position oldPosition = getAnnotationModel().getPosition(oldEntry.getKey());
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
