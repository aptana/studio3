package com.aptana.editor.common.scripting.commands;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.DocumentContentTypeManager;
import com.aptana.editor.common.QualifiedContentType;
import com.aptana.editor.common.tmp.ContentTypeTranslation;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.MenuElement;

public class EditorCommandsMenuContributor extends ContributionItem {

	public EditorCommandsMenuContributor() {
	}

	public EditorCommandsMenuContributor(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		super.fill(menu, index);
		
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);
		if (evaluationService != null) {
			IEvaluationContext currentState = evaluationService.getCurrentState();
			Object activePart = currentState.getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof AbstractThemeableEditor) {
				AbstractThemeableEditor abstractThemeableEditor = (AbstractThemeableEditor) activePart;
				IDocument document = abstractThemeableEditor.getDocumentProvider().getDocument(abstractThemeableEditor.getEditorInput());
				ITextSelection selection = (ITextSelection) abstractThemeableEditor.getSelectionProvider().getSelection();
				MenuElement[] menusFromScope;
				try {
					menusFromScope = BundleManager.getInstance().getMenusFromScope(getContentTypeAtOffset(document, selection.getOffset()));
					buildMenu(menu, menusFromScope, abstractThemeableEditor);
				} catch (BadLocationException e) {
				}
			}
		}
	}
	
	private void buildMenu(Menu menu, MenuElement[] menusFromScope, final ITextEditor textEditor) {
		for (MenuElement menuForScope : menusFromScope) {
			if (menuForScope.isHierarchicalMenu()) {
				MenuItem menuItemForMenuForScope = new MenuItem(menu, SWT.CASCADE);
				menuItemForMenuForScope.setText(menuForScope.getDisplayName());
				
				Menu menuForMenuForScope = new Menu(menu);
				menuItemForMenuForScope.setMenu(menuForMenuForScope);
				
				// Recursive
				buildMenu(menuForMenuForScope, menuForScope.getChildren(), textEditor);
			} else if (menuForScope.isSeparator()) {
				new MenuItem(menu, SWT.SEPARATOR);
			} else {
				final CommandElement command = menuForScope.getCommand();
				MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText(menuForScope.getDisplayName());
				menuItem.setImage(CommonEditorPlugin.getDefault().getImage(CommonEditorPlugin.COMMAND));
				menuItem.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (command != null) {
							CommandResult commandResult = CommandExecutionUtils.executeCommand(command, textEditor);
							CommandExecutionUtils.processCommandResult(command, commandResult, textEditor);
						}
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
			}
		}
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	private static String getContentTypeAtOffset(IDocument document, int offset) throws BadLocationException {
		QualifiedContentType contentType = DocumentContentTypeManager.getInstance().getContentType(document, offset);
		if (contentType != null) {
			return ContentTypeTranslation.getDefault().translate(contentType).toString();
		}
		return document.getContentType(offset);
	}
}
