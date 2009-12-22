package com.aptana.editor.common.scripting.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
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
import com.aptana.editor.common.ITopContentTypesProvider;
import com.aptana.editor.common.QualifiedContentType;
import com.aptana.editor.common.TextEditorUtils;
import com.aptana.editor.common.tmp.ContentTypeTranslation;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.model.SnippetElement;

/**
 * This contributes the menus for editor scope to the Commands menu.
 * 
 * @author schitale
 *
 */
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
			if (activePart instanceof ITextEditor) {
				fill(menu, (ITextEditor) activePart);
			}
		}
	}
	
	public static void fill(Menu menu, ITextEditor textEditor) {
		// Is this an Aptana Editor
		if (textEditor instanceof AbstractThemeableEditor) {
			AbstractThemeableEditor abstractThemeableEditor = (AbstractThemeableEditor) textEditor;
			String[] splitContentTypesAtOffset = null;
			String contentTypeAtOffset = null;
			MenuElement[] menusFromScope;
			try {
				IDocument document = abstractThemeableEditor.getDocumentProvider().getDocument(abstractThemeableEditor.getEditorInput());
				int caretOffset = TextEditorUtils.getCaretOffset(abstractThemeableEditor);
				// Get the scope at caret offset
				contentTypeAtOffset = getContentTypeAtOffset(document, caretOffset);
				// Split scope into successively outer scope
				splitContentTypesAtOffset = ScopeSelector.splitScope(contentTypeAtOffset);
				for (int i = 0; i < splitContentTypesAtOffset.length; i++) {
					menusFromScope = BundleManager.getInstance().getMenusFromScope(splitContentTypesAtOffset[i]);
					if (menusFromScope.length > 0) {
						// Build the menu
						buildMenu(menu, menusFromScope, abstractThemeableEditor, contentTypeAtOffset);
						break;
					}
				}
			} catch (BadLocationException e) {
				CommonEditorPlugin.logError(e);
			}
			
			SourceViewerConfiguration sourceViewerConfiguration = abstractThemeableEditor.getSourceViewerConfigurationNonFinal();
			List<MenuElement> menusFromScopeList = new LinkedList<MenuElement>();
			if (sourceViewerConfiguration instanceof ITopContentTypesProvider) {
				String[][] topContentTypes = ((ITopContentTypesProvider) sourceViewerConfiguration).getTopContentTypes();
				for (String[] topContentType : topContentTypes) {
					QualifiedContentType qualifiedContentType = new QualifiedContentType(topContentType);
					String contentType = ContentTypeTranslation.getDefault().translate(qualifiedContentType).toString();
					// Get menus
					menusFromScope = BundleManager.getInstance().getMenusFromScope(contentType);
					if (menusFromScope.length > 0) {
						// Collect
						menusFromScopeList.addAll(Arrays.asList(menusFromScope));
					}
				}
				
				// Do we have some menus?
				if (menusFromScopeList.size() > 0) {
					// Sort them
					Collections.sort(menusFromScopeList, new Comparator<MenuElement>() {
						@Override
						public int compare(MenuElement menuElement1, MenuElement menuElement2) {
							return menuElement1.getDisplayName().compareTo(menuElement2.getDisplayName());
						}
					});
					menusFromScope = new MenuElement[menusFromScopeList.size()];
					menusFromScopeList.toArray(menusFromScope);
					// Add separator
					new MenuItem(menu, SWT.SEPARATOR);
					
					// Now build the menu
					buildMenu(menu, menusFromScope, abstractThemeableEditor, contentTypeAtOffset);
				}
			}

			new MenuItem(menu, SWT.SEPARATOR);

			MenuItem menuItemForOtherScopes = new MenuItem(menu, SWT.CASCADE);
			menuItemForOtherScopes.setText(Messages.EditorCommandsMenuContributor_CommandsForOtherScopes);

			Menu menuForOtherScopes = new Menu(menu);
			menuItemForOtherScopes.setMenu(menuForOtherScopes);
			// TODO Need API in Bundle Manager to implement this.
		}
	}
	
	/**
	 * This recursively builds the menu contribution.
	 * 
	 * @param menu
	 * @param menusFromScope
	 * @param textEditor
	 * @param contentTypeAtOffset
	 */
	private static void buildMenu(Menu menu, MenuElement[] menusFromScope, final ITextEditor textEditor, String contentTypeAtOffset) {
		for (MenuElement menuForScope : menusFromScope) {
			if (menuForScope.isHierarchicalMenu()) {
				MenuItem menuItemForMenuForScope = new MenuItem(menu, SWT.CASCADE);
				menuItemForMenuForScope.setText(menuForScope.getDisplayName());
				
				Menu menuForMenuForScope = new Menu(menu);
				menuItemForMenuForScope.setMenu(menuForMenuForScope);
				
				// Recursive
				buildMenu(menuForMenuForScope, menuForScope.getChildren(), textEditor, contentTypeAtOffset);
			} else if (menuForScope.isSeparator()) {
				new MenuItem(menu, SWT.SEPARATOR);
			} else {
				final CommandElement command = menuForScope.getCommand();
				final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText(menuForScope.getDisplayName());
				if (command instanceof SnippetElement) {
					menuItem.setImage(CommonEditorPlugin.getDefault().getImage(CommonEditorPlugin.SNIPPET));
				} else {
					menuItem.setImage(CommonEditorPlugin.getDefault().getImage(CommonEditorPlugin.COMMAND));
				}
				menuItem.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (command == null) {
							// There is no associated command. Show a message to the user.
							MessageDialog.openError(menuItem.getParent().getShell(),
									Messages.EditorCommandsMenuContributor_TITLE_CommandNotDefined,
									Messages.bind(Messages.EditorCommandsMenuContributor_MSG_CommandNotDefined, menuItem.getText()));
						} else {
							CommandResult commandResult = CommandExecutionUtils.executeCommand(command, textEditor);
							CommandExecutionUtils.processCommandResult(command, commandResult, textEditor);
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				// Enable the menu item if:
				// 1. There is no associated command so that we can show a message to the user when they invoke the menu item
				// 2. The command did not specify the scope
				// 3. The command specified the scope and it matches the current scope
				menuItem.setEnabled(command == null || command.getScope() == null || command.getScopeSelector().matches(contentTypeAtOffset));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#isDynamic()
	 */
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
