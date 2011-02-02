/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import com.aptana.editor.common.actions.FoldingActionsGroup;

/**
 * This provides the action contributions for Aptana Editors.
 * 
 * @author schitale
 *
 */
public class CommonTextEditorActionContributor extends TextEditorActionContributor {

	private CommandsMenuContributionItem commandsMenuContributionItem;

	private StatusLineContributionItem inputPositionStatsContributionItem;

	public CommonTextEditorActionContributor() {
	}

	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		commandsMenuContributionItem = new CommandsMenuContributionItem();
		statusLineManager.add(commandsMenuContributionItem);
		super.contributeToStatusLine(statusLineManager);

		inputPositionStatsContributionItem =
			new StatusLineContributionItem(ITextEditorActionConstants.STATUS_CATEGORY_INPUT_POSITION, true, 24);
		IContributionItem[] contributionItems = statusLineManager.getItems();
		for (int i = 0; i < contributionItems.length; i++) {
			IContributionItem contributionItem = contributionItems[i];
			String id = contributionItem.getId();
			if (ITextEditorActionConstants.STATUS_CATEGORY_INPUT_POSITION.equals(id)) {
				statusLineManager.remove(contributionItem);
				statusLineManager.add(inputPositionStatsContributionItem);
			}
		}
	}
	
	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		if (part instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) part;
			if (commandsMenuContributionItem != null) {
				commandsMenuContributionItem.setTextEditor(textEditor);
			}
			if (inputPositionStatsContributionItem != null) {
				inputPositionStatsContributionItem.setActionHandler(getAction(textEditor, ITextEditorActionConstants.GOTO_LINE));
				ITextEditorExtension extension= (ITextEditorExtension) textEditor;
				extension.setStatusField(inputPositionStatsContributionItem, ITextEditorActionConstants.STATUS_CATEGORY_INPUT_POSITION);
			}
			if (part instanceof AbstractThemeableEditor)
			{
				FoldingActionsGroup foldingActions = ((AbstractThemeableEditor) part).getFoldingActionsGroup();
				if (foldingActions != null)
				{
					foldingActions.updateActionBars();
				}
			}
		}
	}
	
	private class CommandsMenuContributionItem extends ContributionItem {
		private ITextEditor textEditor;
		private ToolItem menuToolItem;

		public CommandsMenuContributionItem() {
		}
		
		@Override
		public void fill(Composite parent) {
			Label sep= new Label(parent, SWT.SEPARATOR);
			StatusLineLayoutData data= new StatusLineLayoutData();
			data.heightHint= getHeightHint(parent);
			sep.setLayoutData(data);

			ToolBarManager toolBarManager = new ToolBarManager(SWT.HORIZONTAL | SWT.FLAT);
			final ToolBar toolBar = toolBarManager.createControl(parent);;
			data= new StatusLineLayoutData();
			toolBar.setLayoutData(data);
			
			final Menu menu = new Menu(toolBar);

			menuToolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
			menuToolItem.setImage(CommonEditorPlugin.getDefault().getImageFromImageRegistry(CommonEditorPlugin.COMMAND));
			menuToolItem.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					if (menu.isDisposed()) {
						return;
					}
					MenuItem[] items = menu.getItems();
					for (MenuItem menuItem : items) {
						if (menuItem.isDisposed()) {
							continue;
						}
						menuItem.dispose();
					}
					
					IEditorCommandsMenuContributor contributor = getEditorCommandsMenuContributor();
					if (contributor != null) {
						contributor.fill(menu, textEditor);
					}
					menu.setVisible(true);
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			
			menuToolItem.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					menuToolItem = null;					
				}
			});
			
			updateState();
		}
		
		/**
		 * Returns the height hint for this separator.
		 *
		 * @param control the root control of this label
		 * @return the height hint
		 */
		private int getHeightHint(Composite control) {
			GC gc= new GC(control);
			gc.setFont(control.getFont());
			int height = gc.getFontMetrics().getHeight();
			gc.dispose();
			return height;
		}
		
		void setTextEditor(ITextEditor textEditor) {
			this.textEditor = textEditor;
			updateState();
		}
		
		private void updateState() {
			if (menuToolItem != null && !menuToolItem.isDisposed()) {
				menuToolItem.setEnabled(textEditor instanceof AbstractThemeableEditor);
			}
		}
	}
	
	private IEditorCommandsMenuContributor getEditorCommandsMenuContributor() {
		IEditorCommandsMenuContributor adapter = (IEditorCommandsMenuContributor) Platform.getAdapterManager().getAdapter(this, IEditorCommandsMenuContributor.class);
		if (adapter == null && Platform.getAdapterManager().hasAdapter(this, IEditorCommandsMenuContributor.class.getName())) {
			adapter = (IEditorCommandsMenuContributor) Platform.getAdapterManager().loadAdapter(this, IEditorCommandsMenuContributor.class.getName());
		}
		return adapter;
	}
}
