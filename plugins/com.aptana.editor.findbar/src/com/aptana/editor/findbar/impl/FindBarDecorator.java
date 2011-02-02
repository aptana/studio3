/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.aptana.editor.findbar.FindBarPlugin;
import com.aptana.editor.findbar.api.IFindBarDecorator;
import com.aptana.editor.findbar.preferences.IPreferencesConstants;

/**
 * Main control of the find bar.
 *
 * @author Fabio Zadrozny
 */
public class FindBarDecorator implements IFindBarDecorator, SelectionListener
{

	private static final String CLOSE = "icons/close.png"; //$NON-NLS-1$
	private static final String PREVIOUS = "icons/previous.png"; //$NON-NLS-1$
	private static final String NEXT = "icons/next.png"; //$NON-NLS-1$
	private static final String SIGMA = "icons/sigma.png"; //$NON-NLS-1$
	private static final String FINDREPLACE = "icons/findreplace.png"; //$NON-NLS-1$
	private static final String CASE_SENSITIVE = "icons/casesensitive.png"; //$NON-NLS-1$
	private static final String CASE_SENSITIVE_DISABLED = "icons/casesensitive_disabled.png"; //$NON-NLS-1$
	private static final String REGEX = "icons/regex.png"; //$NON-NLS-1$
	private static final String REGEX_DISABLED = "icons/regex_disabled.png"; //$NON-NLS-1$
	private static final String WHOLE_WORD = "icons/whole_word.png"; //$NON-NLS-1$
	private static final String WHOLE_WORD_DISABLED = "icons/whole_word_disabled.png"; //$NON-NLS-1$

	private final ITextEditor textEditor;
	private ISourceViewer sourceViewer;
	final IEditorStatusLine statusLineManager;
	private final String PREFERENCE_NAME_FIND = "FIND_BAR_DECORATOR_FIND_ENTRIES"; //$NON-NLS-1$
	private final String PREFERENCE_NAME_REPLACE = "FIND_BAR_DECORATOR_REPLACE_ENTRIES"; //$NON-NLS-1$
	private IAction fOriginalFindBarAction;

	public FindBarDecorator(ITextEditor textEditor)
	{
		this.textEditor = textEditor;
		this.statusLineManager = (IEditorStatusLine) textEditor.getAdapter(IEditorStatusLine.class);
		findBarActions = new FindBarActions(textEditor, this);
	}

	public Composite createFindBarComposite(Composite parent)
	{
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);

		Composite content = new Composite(composite, SWT.NONE);
		content.setLayout(new FillLayout());
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return content;
	}

	public void createFindBar(ISourceViewer sourceViewer)
	{
		this.sourceViewer = sourceViewer;
		findBarFinder = new FindBarFinder(textEditor, sourceViewer, this);
		findBar = new Composite(composite, SWT.BORDER);
		findBarGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		findBarGridData.exclude = true;
		findBar.setLayoutData(findBarGridData);

		int NUMBER_OF_ITEMS = 17;
		GridLayout gridLayout = new GridLayout(NUMBER_OF_ITEMS, false);
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		gridLayout.verticalSpacing = 0;
		findBar.setLayout(gridLayout);

		close = createButton(CLOSE, true);
		close.setToolTipText(Messages.FindBarDecorator_TOOLTIP_HideFindBar);

		findButton = createButton(null, true);
		findButton.setText(Messages.FindBarDecorator_LABEL_FInd);

		combo = createCombo(PREFERENCE_NAME_FIND);

		comboReplace = createCombo(PREFERENCE_NAME_REPLACE);

		previous = createButton(PREVIOUS, false);
		next = createButton(NEXT, false);

		ToolBar optionsToolBar = new ToolBar(findBar, SWT.NONE);
		optionsToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		caseSensitive = new ToolItem(optionsToolBar, SWT.CHECK);
		caseSensitive.setImage(FindBarPlugin.getImage(CASE_SENSITIVE));
		caseSensitive.setDisabledImage(FindBarPlugin.getImage(CASE_SENSITIVE_DISABLED));
		caseSensitive.setToolTipText(Messages.FindBarDecorator_LABEL_CaseSensitive);
		caseSensitive.addSelectionListener(this);

		wholeWord = new ToolItem(optionsToolBar, SWT.CHECK);
		wholeWord.setImage(FindBarPlugin.getImage(WHOLE_WORD));
		wholeWord.setDisabledImage(FindBarPlugin.getImage(WHOLE_WORD_DISABLED));
		wholeWord.setToolTipText(Messages.FindBarDecorator_LABEL_WholeWord);
		wholeWord.addSelectionListener(this);
		wholeWord.setEnabled(false);

		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget instanceof IFindReplaceTargetExtension3)
		{
			regularExpression = new ToolItem(optionsToolBar, SWT.CHECK);
			regularExpression.setImage(FindBarPlugin.getImage(REGEX));
			regularExpression.setDisabledImage(FindBarPlugin.getImage(REGEX_DISABLED));
			regularExpression.setToolTipText(Messages.FindBarDecorator_LABEL_RegularExpression);
			regularExpression.addSelectionListener(new SelectionListener()
			{

				public void widgetSelected(SelectionEvent e)
				{
					adjustEnablement(); // Because whole word is not valid when regexp is chosen.
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		}

		replaceFind = createButton(null, true);
		replaceFind.setText(Messages.FindBarDecorator_LABEL_ReplaceFind);

		replace = createButton(null, true);
		replace.setText(Messages.FindBarDecorator_LABEL_Replace);

		replaceAll = createButton(null, true);
		replaceAll.setText(Messages.FindBarDecorator_LABEL_ReplaceAll);

		countTotal = createCheck();
		countTotal.setImage(FindBarPlugin.getImage(SIGMA));
		countTotal.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowMatchCount);

		count = new Label(findBar, SWT.NONE);
		count.setText("            "); //$NON-NLS-1$
		count.setToolTipText(Messages.FindBarDecorator_TOOLTIP_MatchCount);
		count.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Label streach = new Label(findBar, SWT.NONE);
		streach.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		showFindReplaceDialog = createButton(FINDREPLACE, true);
		showFindReplaceDialog.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowFindReplaceDialog);

		disableWhenHidden = new Control[] { combo, comboReplace, optionsToolBar, close, next, previous, countTotal,
				findButton, replaceFind, replace, replaceAll, count, showFindReplaceDialog, };
	}

	/**
	 * Create a default check (case, word, regexp).
	 */
	private Button createCheck()
	{
		Button button = new Button(findBar, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		button.addSelectionListener(this);
		setDefaultFocusListener(button);
		return button;
	}

	/**
	 * Create a default button (find, replace, replace/find, replace all).
	 */
	private Button createButton(String image, boolean enabled)
	{
		GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		Button button = new Button(findBar, SWT.PUSH);
		button.setEnabled(enabled);
		if (image != null)
		{
			button.setImage(FindBarPlugin.getImage(image));
		}
		button.addSelectionListener(this);
		button.setLayoutData(layoutData);
		setDefaultFocusListener(button);

		return button;
	}

	/**
	 * Sets the focus control so that when gaining focus the actions are enabled and hen loosing the actions are
	 * disabled.
	 */
	private void setDefaultFocusListener(Button button)
	{
		button.addFocusListener(findBarActions.createFocusListener(button));
	}

	/**
	 * Creates a combo (find, replace).
	 */
	private Combo createCombo(String preferenceName)
	{
		final Combo combo = new Combo(findBar, SWT.DROP_DOWN);
		combo.setText("                            "); //$NON-NLS-1$
		Point size = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		GridData comboGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		comboGridData.widthHint = size.x;
		combo.setLayoutData(comboGridData);

		List<String> list = FindBarEntriesHelper.loadEntries(preferenceName);
		list.add(0, EMPTY);
		combo.setItems(list.toArray(new String[list.size()]));
		combo.select(0);

		combo.addFocusListener(findBarActions.createFocusListener(combo));
		return combo;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.findbar.api.IFindBarDecorator#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			showFindBar();
		}
		else
		{
			hideFindBar();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.findbar.api.IFindBarDecorator#isVisible()
	 */
	public boolean isVisible()
	{
		return !findBarGridData.exclude;
	}

	
	private final UpdateFindBarActionOnPropertyChange fFindBarActionOnPropertyChange = new UpdateFindBarActionOnPropertyChange();

	private final class UpdateFindBarActionOnPropertyChange implements IPropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			if (event.getProperty().equals(IPreferencesConstants.USE_CUSTOM_FIND_BAR))
			{
				updateFindBarAction();
			}
		}
	}

	
	/**
	 * Updates the find bar given the preferences (and registers a listener to update it whenever needed).
	 */
	public void installActions()
	{
		IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
		preferenceStore.addPropertyChangeListener(fFindBarActionOnPropertyChange);
		fOriginalFindBarAction = textEditor.getAction(ITextEditorActionConstants.FIND);
		updateFindBarAction();
	}

	
	/**
	 * Updates the find bar action (sets it as the Aptana find bar or restores the original one).
	 */
	private void updateFindBarAction()
	{
		IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
		boolean useCustomFindBar = preferenceStore.getBoolean(IPreferencesConstants.USE_CUSTOM_FIND_BAR);
		if (useCustomFindBar)
		{
			// Replaces the actual find with our find.
			textEditor.setAction(ITextEditorActionConstants.FIND, new ShowFindBarAction(textEditor));
		}
		else
		{
			// Restore the original find action.
			textEditor.setAction(ITextEditorActionConstants.FIND, fOriginalFindBarAction);
		}

	}

	public void dispose()
	{
		IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
		preferenceStore.removePropertyChangeListener(fFindBarActionOnPropertyChange);
		fOriginalFindBarAction = null;
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		CopiedFromFindReplaceDialog findReplaceDialog = new CopiedFromFindReplaceDialog(findReplaceTarget,
				statusLineManager);

		if (source == close)
		{
			hideFindBar();
		}
		else if (source == previous)
		{
			findPrevious();
		}
		else if (source == next)
		{
			findNext();
		}
		else if (source == caseSensitive || source == wholeWord)
		{
			setFindText(combo.getText());
			findBarFinder.find(true, true);
			showCountTotal();
		}
		else if (source == regularExpression)
		{
			setFindText(combo.getText());
			findBarFinder.find(true, true);
			showCountTotal();
		}
		else if (source == countTotal)
		{
			showCountTotal();
		}
		else if (source == findButton)
		{
			findNext();
		}
		else if (source == replaceFind || source == replace)
		{
			setFindText(combo.getText());
			setFindText(comboReplace.getText(), true, comboReplace, PREFERENCE_NAME_REPLACE);
			PatternSyntaxException exception = null;
			try
			{
				findReplaceDialog.replaceSelection(comboReplace.getText(), regularExpression.getSelection());
				showCountTotal();
			}
			catch (PatternSyntaxException e1)
			{
				// Don't log it now, there's still a chance that doing a find will get us to the proper state.
				exception = e1;
			}
			catch (IllegalStateException e1)
			{
				if (findBarFinder.find(true, true, true, false, true))
				{
					try
					{
						findReplaceDialog.replaceSelection(comboReplace.getText(), regularExpression.getSelection());
						showCountTotal();
					}
					catch (IllegalStateException e2)
					{
						// ignore
					}
					catch (PatternSyntaxException e3)
					{
						exception = e3;
					}
				}
				else
				{
					statusLineManager.setMessage(false, Messages.FindBarDecorator_MSG_ReplaceNeedsFind, null);
					return;
				}
			}
			if (source == replaceFind)
			{
				findBarFinder.find(true);
			}
			else
			{
				if (exception != null)
				{
					statusLineManager.setMessage(true, exception.getMessage(), null);
				}
				else
				{
					statusLineManager.setMessage(false, EMPTY, null);
				}
			}
		}
		else if (source == replaceAll)
		{
			setFindText(combo.getText());
			setFindText(comboReplace.getText(), true, comboReplace, PREFERENCE_NAME_REPLACE);
			try
			{
				int replaced = findReplaceDialog.replaceAll(combo.getText(), comboReplace.getText(), true,
						caseSensitive.getSelection(), getWholeWord(), regularExpression.getSelection());
				showCountTotal();
				statusLineManager.setMessage(false, String.format(Messages.FindBarDecorator_MSG_Replaced, replaced),
						null);
			}
			catch (PatternSyntaxException e1)
			{
				statusLineManager.setMessage(true, e1.getMessage(), null);
			}
		}
		else if (source == showFindReplaceDialog)
		{
			showFindReplaceDialog();
		}
	}

	boolean isActive()
	{
		return isVisible() && (combo.getDisplay().getFocusControl() == combo);
	}

	private Composite composite;

	private Composite findBar;
	private GridData findBarGridData;

	/* default */Combo combo;
	/* default */Combo comboReplace;
	ToolItem caseSensitive;
	ToolItem wholeWord;
	ToolItem regularExpression;
	private Button close;
	private Button next;
	private Button previous;
	private Button countTotal;
	private Button findButton;
	private Button replaceFind;
	private Button replace;
	private Button replaceAll;
	private Label count;
	private Button showFindReplaceDialog;
	private Control[] disableWhenHidden;

	private FindBarActions findBarActions;
	private FindBarFinder findBarFinder;

	private static final String EMPTY = ""; //$NON-NLS-1$

	private ModifyListener modifyListener = new ModifyListener()
	{
		private String lastText = EMPTY;

		public void modifyText(ModifyEvent e)
		{
			combo.setForeground(null);
			boolean wrap = true;
			String text = combo.getText();
			if (lastText.startsWith(text))
			{
				wrap = false;
			}
			lastText = text;
			adjustEnablement();
			if (EMPTY.equals(text))
			{
				ISelectionProvider selectionProvider = textEditor.getSelectionProvider();
				ISelection selection = selectionProvider.getSelection();
				if (selection instanceof TextSelection)
				{
					ITextSelection textSelection = (ITextSelection) selection;
					selectionProvider.setSelection(new TextSelection(textSelection.getOffset(), 0));
				}
			}
			else
			{
				findBarFinder.find(true, true, wrap);
			}
			showCountTotal();
		}
	};

	private void adjustEnablement()
	{
		String text = combo.getText();
		previous.setEnabled(!EMPTY.equals(text));
		next.setEnabled(!EMPTY.equals(text));
		count.setText(EMPTY);
		wholeWord.setEnabled(!EMPTY.equals(text) && !regularExpression.getSelection() && isWord(text));
	}

	/* default */void hideFindBar()
	{
		if (findBarGridData.exclude == false)
		{
			findBarGridData.exclude = true;
			composite.layout();
			findBarFinder.resetIncrementalOffset();
			combo.removeModifyListener(modifyListener);
			statusLineManager.setMessage(false, EMPTY, null);
		}
		textEditor.setFocus();
		if (disableWhenHidden != null)
		{
			for (Control w : disableWhenHidden)
			{
				w.setEnabled(false);
			}
		}
	}

	/* default */void showFindBar()
	{
		if (disableWhenHidden != null)
		{
			for (Control w : disableWhenHidden)
			{
				w.setEnabled(true);
			}
		}

		boolean wasExcluded = findBarGridData.exclude;
		if (findBarGridData.exclude)
		{
			findBarGridData.exclude = false;
			composite.layout();
		}
		if (!findBarActions.isActivated())
		{
			// Only change the text if it is not activated (otherwise it means it was
			// already activated and the user was in another control in the find bar and used Ctrl+F, in which case we
			// don't want to
			// change it -- other cases mean that Ctrl+F was used from the editor or somewhere else, which means
			// we have to update it).
			ISelection selection = sourceViewer.getSelectionProvider().getSelection();
			if (selection instanceof ITextSelection)
			{
				ITextSelection textSelection = (ITextSelection) selection;
				String text = textSelection.getText();
				if (text.indexOf("\n") == -1 && text.indexOf("\r") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
					setFindText(text, !wasExcluded);
				}
			}
		}
		if (wasExcluded)
		{
			combo.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					combo.addModifyListener(modifyListener);
				}
			});
		}
		adjustEnablement();
		boolean comboHasFocus = combo.isFocusControl();
		if (!comboHasFocus)
		{
			combo.setFocus();
			findBarFinder.resetIncrementalOffset();
		}
	}

	/* default */void findPrevious()
	{
		findBarFinder.find(false);
		setFindText(combo.getText());
	}

	/* default */void findNext()
	{
		findBarFinder.find(true);
		setFindText(combo.getText());
	}

	boolean getWholeWord()
	{
		return wholeWord.getEnabled() && wholeWord.getSelection() && !regularExpression.getSelection();
	}

	private void setFindText(String findText)
	{
		setFindText(findText, true);
	}

	private void setFindText(String findText, boolean removeAddListener)
	{
		setFindText(findText, removeAddListener, combo, PREFERENCE_NAME_FIND);
	}

	private void setFindText(String findText, boolean removeAddListener, final Combo combo, String preferenceName)
	{
		if (findText.length() == 0)
		{
			return; // nothing to do in this case
		}

		List<String> items = FindBarEntriesHelper.addEntry(findText, preferenceName);

		try
		{
			if (removeAddListener)
			{
				combo.removeModifyListener(modifyListener);
			}
			combo.setItems(items.toArray(new String[0]));
			combo.select(0);
		}
		finally
		{
			if (removeAddListener)
			{
				combo.addModifyListener(modifyListener);
			}
		}
	}

	private static final int TOO_MANY = Integer.getInteger(FindBarDecorator.class.getName() + ".TOO_MANY", 100); //$NON-NLS-1$

	private void showCountTotal()
	{
		if (!countTotal.getSelection())
		{
			count.setText(EMPTY);
			return;
		}
		String patternString = combo.getText();
		boolean patternStringIsAWord = isWord(patternString);
		int total = 0;
		if (!EMPTY.equals(patternString))
		{
			String text = sourceViewer.getDocument().get();
			int flags = 0;
			if (!caseSensitive.getSelection())
			{
				flags |= Pattern.CASE_INSENSITIVE;
			}
			if (!regularExpression.getSelection())
			{
				patternString = Pattern.quote(patternString);
			}
			if (patternStringIsAWord && getWholeWord())
			{
				patternString = "\\b" + patternString + "\\b"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			Pattern pattern = Pattern.compile(patternString, flags);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find(0))
			{
				total = 1;
				while (matcher.find())
				{
					++total;
					if ((TOO_MANY != -1) && total > TOO_MANY)
					{
						break;
					}
				}
			}
		}
		if ((TOO_MANY != -1) && total > TOO_MANY)
		{
			count.setText("> " + TOO_MANY); //$NON-NLS-1$
		}
		else
		{
			count.setText(String.valueOf(total));
		}
	}

	private void showFindReplaceDialog()
	{
		// It's important that the combo has the focus.
		// Doing the find (Ctrl+F) anywhere will put the focus on the combo, but if the combo
		// has the focus, it'll show the default find dialog.
		// @see: com.aptana.editor.findbar.impl.ShowFindBarAction
		combo.setFocus();
		IWorkbenchPartSite site = textEditor.getSite();
		ICommandService commandService = (ICommandService) site.getService(ICommandService.class);
		Command findReplacecommand = commandService.getCommand(ActionFactory.FIND.create(site.getWorkbenchWindow())
				.getActionDefinitionId());
		IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
		if (handlerService != null)
		{
			try
			{
				handlerService.executeCommand(new ParameterizedCommand(findReplacecommand, null), null);
			}
			catch (ExecutionException e1)
			{
			}
			catch (NotDefinedException e1)
			{
			}
			catch (NotEnabledException e1)
			{
			}
			catch (NotHandledException e1)
			{
			}
		}
	}

	/**
	 * Tests whether each character in the given string is a letter.
	 * 
	 * @param str
	 *            the string to check
	 * @return <code>true</code> if the given string is a word
	 */
	private boolean isWord(String str)
	{
		if (str == null || str.length() == 0)
			return false;

		for (int i = 0; i < str.length(); i++)
		{
			if (!Character.isJavaIdentifierPart(str.charAt(i)))
				return false;
		}
		return true;
	}
}
