/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.IFoldingCommandIds;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextOperationAction;

/**
 * Code folding action group.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FoldingActionsGroup extends ActionGroup
{

	private TextOperationAction collapseAction;
	private TextOperationAction expandAction;
	private TextOperationAction collapseAllAction;
	private TextOperationAction expandAllAction;

	/**
	 * Constructs a new FoldingActionsGroup with a given text editor.
	 * 
	 * @param textEditor
	 */
	public FoldingActionsGroup(ITextEditor textEditor)
	{
		// Initialize the actions.
		collapseAction = new TextOperationAction(Messages.getResourceBundle(),
				"Folding.Collapse.", textEditor, ProjectionViewer.COLLAPSE, true); //$NON-NLS-1$
		collapseAction.setActionDefinitionId(IFoldingCommandIds.FOLDING_COLLAPSE);
		textEditor.setAction(IFoldingCommandIds.FOLDING_COLLAPSE, collapseAction);

		expandAction = new TextOperationAction(Messages.getResourceBundle(),
				"Folding.Expand.", textEditor, ProjectionViewer.EXPAND, true); //$NON-NLS-1$
		expandAction.setActionDefinitionId(IFoldingCommandIds.FOLDING_EXPAND);
		textEditor.setAction(IFoldingCommandIds.FOLDING_EXPAND, expandAction);

		collapseAllAction = new TextOperationAction(Messages.getResourceBundle(),
				"Folding.CollapseAll.", textEditor, ProjectionViewer.COLLAPSE_ALL, true); //$NON-NLS-1$
		collapseAllAction.setActionDefinitionId(IFoldingCommandIds.FOLDING_COLLAPSE_ALL);
		textEditor.setAction(IFoldingCommandIds.FOLDING_COLLAPSE_ALL, collapseAllAction);

		expandAllAction = new TextOperationAction(Messages.getResourceBundle(),
				"Folding.ExpandAll.", textEditor, ProjectionViewer.EXPAND_ALL, true); //$NON-NLS-1$
		expandAllAction.setActionDefinitionId(IFoldingCommandIds.FOLDING_EXPAND_ALL);
		textEditor.setAction(IFoldingCommandIds.FOLDING_EXPAND_ALL, expandAllAction);
	}

	/**
	 * Fill a menu with a actions.
	 * 
	 * @param menuManager
	 *            The menu manager to add to
	 */
	public void fillMenu(IMenuManager menuManager)
	{
		update();
		menuManager.add(expandAction);
		menuManager.add(collapseAction);
		menuManager.add(expandAllAction);
		menuManager.add(collapseAllAction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
	 */
	public void updateActionBars()
	{
		update();
	}

	/**
	 * Update all the actions
	 */
	private void update()
	{
		collapseAction.update();
		expandAction.update();
		collapseAllAction.update();
		expandAllAction.update();
	}
}
