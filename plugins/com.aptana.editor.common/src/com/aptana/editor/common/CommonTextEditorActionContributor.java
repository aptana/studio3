package com.aptana.editor.common;

import org.eclipse.jface.action.ContributionItem;
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
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.scripting.commands.EditorCommandsMenuContributor;

/**
 * This provides the action contributions for Aptana Editors.
 * 
 * @author schitale
 *
 */
public class CommonTextEditorActionContributor extends BasicTextEditorActionContributor {

	private CommandsMenuContributionItem commandsMenuContributionItem;

	public CommonTextEditorActionContributor() {
	}

	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		commandsMenuContributionItem = new CommandsMenuContributionItem();
		statusLineManager.add(commandsMenuContributionItem);
		super.contributeToStatusLine(statusLineManager);
	}
	
	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		if (part instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) part;
			if (commandsMenuContributionItem != null) {
				commandsMenuContributionItem.setTextEditor(textEditor);
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
				@Override
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
					
					EditorCommandsMenuContributor.fill(menu, textEditor);
					menu.setVisible(true);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			
			menuToolItem.addDisposeListener(new DisposeListener() {
				@Override
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
}
