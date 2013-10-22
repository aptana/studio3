/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.navigator.CommonNavigator;

public class RemoteNavigatorView extends CommonNavigator
{

	public static final String ID = "com.aptana.ui.io.remoteview"; //$NON-NLS-1$

	@Override
	public void createPartControl(Composite aParent)
	{
		super.createPartControl(aParent);

		getCommonViewer().setLabelProvider(
				new FileNavigatorDecoratingLabelProvider(getNavigatorContentService().createCommonLabelProvider()));
		getCommonViewer().setComparer(new FileSystemElementComparer());
		ColumnViewerToolTipSupport.enableFor(getCommonViewer());

		final Tree tree = getCommonViewer().getTree();
		tree.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseDown(MouseEvent e)
			{
				if (tree.getItem(new Point(e.x, e.y)) == null)
				{
					tree.deselectAll();
					tree.notifyListeners(SWT.Selection, new Event());
				}
			}
		});
	}

	public void setSelection(Object[] selectionPath)
	{
		if (selectionPath == null || selectionPath.length == 0)
		{
			return;
		}

		DeferredTreeSelectionExpander selectionExpander = (DeferredTreeSelectionExpander) getCommonViewer().getData(
				FileTreeContentProvider.SELECTION_EXPANDER_KEY);
		if (selectionExpander != null)
		{
			selectionExpander.setSelection(new TreePath(selectionPath));
		}
	}
}
