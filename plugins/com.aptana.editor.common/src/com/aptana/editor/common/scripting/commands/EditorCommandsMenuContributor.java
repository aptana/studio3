package com.aptana.editor.common.scripting.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
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
import com.aptana.editor.common.ITopContentTypesProvider;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;
import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.model.NotFilter;
import com.aptana.scripting.model.ScopeFilter;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.util.CollectionsUtil;

/**
 * This contributes the menus for editor scope to the Commands menu.
 * 
 * @author schitale
 */
public class EditorCommandsMenuContributor extends ContributionItem
{

	private static final String DISPOSE_ON_HIDE = "DOH"; //$NON-NLS-1$

	// FIXME Char for tab. Figure out how to use an image instead.
	private static final String TAB = "\u21E5"; //$NON-NLS-1$

	public EditorCommandsMenuContributor()
	{
	}

	public EditorCommandsMenuContributor(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		super.fill(menu, index);
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench().getService(
				IEvaluationService.class);
		if (evaluationService != null)
		{
			IEvaluationContext currentState = evaluationService.getCurrentState();
			Object activePart = currentState.getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof ITextEditor)
			{
				fill(menu, (ITextEditor) activePart);
			}
		}
	}

	public static void fill(final Menu menu, ITextEditor textEditor)
	{
		// Add the listener to clean up the menu on hide
		// so that the accelerators do not interfere with
		// key binding
		menu.addMenuListener(new MenuListener()
		{
			@Override
			public void menuShown(MenuEvent e)
			{
			}

			@Override
			public void menuHidden(MenuEvent e)
			{
				menu.removeMenuListener(this);
				// Have to use the delay to allow the dispatching
				// of any menu item selection events
				menu.getDisplay().timerExec(200, new Runnable()
				{
					@Override
					public void run()
					{
						if (menu.isDisposed())
						{
							return;
						}
						MenuItem[] menuItems = menu.getItems();
						for (MenuItem menuItem : menuItems)
						{
							if (!menuItem.isDisposed())
							{
								// Is this a menu item that we had added
								Object data = menuItem.getData(DISPOSE_ON_HIDE);
								if (data != null)
								{
									menuItem.dispose();
								}
							}
						}
					}
				});
			}
		});

		// Is this an Aptana Editor
		if (textEditor instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor abstractThemeableEditor = (AbstractThemeableEditor) textEditor;
			String contentTypeAtOffset = null;
			List<MenuElement> menusFromScopeList = new LinkedList<MenuElement>();
			MenuElement[] menusFromScope;
			MenuElement[] menusFromOtherScopes = null;
			try
			{
				IDocument document = abstractThemeableEditor.getDocumentProvider().getDocument(
						abstractThemeableEditor.getEditorInput());
				int caretOffset = TextEditorUtils.getCaretOffset(abstractThemeableEditor);
				// Get the scope at caret offset
				contentTypeAtOffset = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(
						document, caretOffset);
			}
			catch (BadLocationException e)
			{
				CommonEditorPlugin.logError(e);
			}

			// First pull all possible menus from the current caret position's scopes
			if (contentTypeAtOffset != null)
			{
				ScopeFilter filter = new ScopeFilter(contentTypeAtOffset);
				menusFromScope = BundleManager.getInstance().getMenus(filter);
				if (menusFromScope.length > 0)
				{
					menusFromScopeList.addAll(Arrays.asList(menusFromScope));
				}
			}

			// Next we get all possible scopes from the top level content type provider
			SourceViewerConfiguration sourceViewerConfiguration = abstractThemeableEditor
					.getSourceViewerConfigurationNonFinal();
			if (sourceViewerConfiguration instanceof ITopContentTypesProvider)
			{
				String[][] topContentTypes = ((ITopContentTypesProvider) sourceViewerConfiguration)
						.getTopContentTypes();
				List<String> topLevelContentTypesList = new LinkedList<String>();
				for (String[] topContentType : topContentTypes)
				{
					QualifiedContentType qualifiedContentType = new QualifiedContentType(topContentType);
					String contentType = getContentTypeTranslator().translate(qualifiedContentType).toString();
					topLevelContentTypesList.add(contentType);
				}
				if (topLevelContentTypesList.size() > 0)
				{
					String[] topLevelContentTypes = new String[topLevelContentTypesList.size()];
					topLevelContentTypesList.toArray(topLevelContentTypes);

					// Get menus
					ScopeFilter topLevelContentTypesFilter = new ScopeFilter(topLevelContentTypes);
					menusFromScope = BundleManager.getInstance().getMenus(topLevelContentTypesFilter);
					if (menusFromScope.length > 0)
					{
						// Collect
						menusFromScopeList.addAll(Arrays.asList(menusFromScope));
					}

					// Next we use a negative filter to get menus that belong to scopes
					// that do not match the top level scopes. We will use this
					// later to build the "Other" menu.
					NotFilter notFilter = new NotFilter(topLevelContentTypesFilter);
					menusFromOtherScopes =  BundleManager.getInstance().getMenus(notFilter);
				}
			}

			// Do we have some menus?
			if (menusFromScopeList.size() > 0)
			{
				// Remove duplicates and sort
				CollectionsUtil.removeDuplicates(menusFromScopeList);
				Collections.sort(menusFromScopeList, new Comparator<MenuElement>()
				{
					@Override
					public int compare(MenuElement menuElement1, MenuElement menuElement2)
					{
						return menuElement1.getDisplayName().compareTo(menuElement2.getDisplayName());
					}
				});
				menusFromScope = new MenuElement[menusFromScopeList.size()];
				menusFromScopeList.toArray(menusFromScope);

				// Now build the menu
				buildMenu(menu, menusFromScope, abstractThemeableEditor, contentTypeAtOffset);
			}

			// Are there any menus that belong to scopes other than top level scopes
			if (menusFromOtherScopes != null && menusFromOtherScopes.length > 0)
			{
				// Build the "Other" menu
				new MenuItem(menu, SWT.SEPARATOR);

				MenuItem menuItemForOtherScopes = new MenuItem(menu, SWT.CASCADE);
				menuItemForOtherScopes.setText(Messages.EditorCommandsMenuContributor_CommandsForOtherScopes);

				Menu menuForOtherScopes = new Menu(menu);
				menuItemForOtherScopes.setMenu(menuForOtherScopes);
				buildMenu(menuForOtherScopes, menusFromOtherScopes, abstractThemeableEditor, contentTypeAtOffset);
			}
		}
	}

	protected static IContentTypeTranslator getContentTypeTranslator()
	{
		return CommonEditorPlugin.getDefault().getContentTypeTranslator();
	}

	/**
	 * This recursively builds the menu contribution.
	 * 
	 * @param menu
	 * @param menusFromScope
	 * @param textEditor
	 * @param contentTypeAtOffset
	 */
	private static void buildMenu(Menu menu, MenuElement[] menusFromScope, final ITextEditor textEditor,
			String contentTypeAtOffset)
	{
		for (MenuElement menuForScope : menusFromScope)
		{
			String displayName = menuForScope.getDisplayName();
			if (menuForScope.isHierarchicalMenu())
			{
				MenuItem menuItemForMenuForScope = new MenuItem(menu, SWT.CASCADE);
				menuItemForMenuForScope.setText(displayName);
				// We mark the cascade menu items for disposal when the menu is hidden
				// so that accelerators on descendant menu items do not hinder the handling of
				// key bindings
				menuItemForMenuForScope.setData(DISPOSE_ON_HIDE, Boolean.TRUE);

				Menu menuForMenuForScope = new Menu(menu);
				menuItemForMenuForScope.setMenu(menuForMenuForScope);

				// Recursive
				buildMenu(menuForMenuForScope, menuForScope.getChildren(), textEditor, contentTypeAtOffset);
			}
			else if (menuForScope.isSeparator())
			{
				new MenuItem(menu, SWT.SEPARATOR);
			}
			else
			{
				final CommandElement command = menuForScope.getCommand();
				final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);

				if (command != null)
				{
					KeySequence[] keySequences = command.getKeySequences();
					if (keySequences != null && keySequences.length > 0)
					{
						KeySequence keySequence = keySequences[0];
						KeyStroke[] keyStrokes = keySequence.getKeyStrokes();
						// Eclipse can show only single key stroke key sequences
						if (keyStrokes.length == 1)
						{
							int accelerator = SWTKeySupport.convertKeyStrokeToAccelerator(keyStrokes[0]);
							menuItem.setAccelerator(accelerator);
							// We mark the first level menu item with accelerators
							// for disposal when the menu is hidden so that it does
							// the accelerator does not interfere with the handling
							// of key bindings
							if (menu.getParentMenu() == null)
							{
								menuItem.setData(DISPOSE_ON_HIDE, Boolean.TRUE);
							}
						}
					}
					if (command instanceof SnippetElement || keySequences == null || keySequences.length == 0)
					{
						String[] triggers = command.getTriggers();
						if (triggers != null && triggers.length > 0)
						{
							// Use first trigger
							displayName += " (" + triggers[0] + TAB + ")"; //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
				menuItem.setText(displayName);
				menuItem.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						if (command == null)
						{
							// There is no associated command. Show a message to the user.
							MessageDialog.openError(menuItem.getParent().getShell(),
									Messages.EditorCommandsMenuContributor_TITLE_CommandNotDefined, Messages.bind(
											Messages.EditorCommandsMenuContributor_MSG_CommandNotDefined, menuItem
													.getText()));
						}
						else
						{
							CommandResult commandResult = CommandExecutionUtils.executeCommand(command,
									InvocationType.MENU, textEditor);
							CommandExecutionUtils.processCommandResult(command, commandResult, textEditor);
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
				// Enable the menu item if:
				// 1. There is no associated command so that we can show a message to the user when they invoke the menu
				// item
				// 2. The command did not specify the scope
				// 3. The command specified the scope and it matches the current scope
				menuItem.setEnabled(command == null || command.getScope() == null
						|| command.getScopeSelector().matches(contentTypeAtOffset));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic()
	{
		return true;
	}
}
