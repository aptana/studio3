/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.properties;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.properties.PropertySheetPage;

public class CommonEditorPropertySheetPage extends PropertySheetPage
{

	private final ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener()
	{
		public void selectionChanged(SelectionChangedEvent event)
		{
			textSelectionChanged((ITextSelection) event.getSelection());
		}
	};

	private final IPartListener partListener = new IPartListener()
	{

		public void partOpened(IWorkbenchPart part)
		{
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
			removePostSelectionListener();
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partActivated(IWorkbenchPart part)
		{
		}
	};

	private AbstractTextEditor sourcePart;
	private ISourceViewer viewer;

	public CommonEditorPropertySheetPage(ISourceViewer viewer)
	{
		this.viewer = viewer;
	}

	private ISourceViewer getViewer()
	{
		return viewer;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		removePostSelectionListener();
		if (selection instanceof ITextSelection && part instanceof AbstractTextEditor)
		{
			selection = new StructuredSelection(new AdaptableTextSelection(getViewer(), (ITextSelection) selection));
			sourcePart = (AbstractTextEditor) part;
			((IPostSelectionProvider) sourcePart.getSelectionProvider())
					.addPostSelectionChangedListener(selectionChangedListener);
			sourcePart.getSite().getPage().addPartListener(partListener);
		}
		super.selectionChanged(part, selection);
	}

	private void textSelectionChanged(ITextSelection textSelection)
	{
		if (sourcePart != null)
		{
			ISelection selection = new StructuredSelection(new AdaptableTextSelection(getViewer(), textSelection));
			super.selectionChanged(sourcePart, selection);
		}
	}

	private void removePostSelectionListener()
	{
		if (sourcePart != null)
		{
			((IPostSelectionProvider) sourcePart.getSelectionProvider())
					.removePostSelectionChangedListener(selectionChangedListener);
			sourcePart = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.PropertySheetPage#dispose()
	 */
	@Override
	public void dispose()
	{
		if (sourcePart != null)
		{
			sourcePart.getSite().getPage().removePartListener(partListener);
		}
		removePostSelectionListener();
		super.dispose();
	}

}
