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
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
	private static final String CLOSE_ENTER = "icons/close_enter.png"; //$NON-NLS-1$
	private static final String SEARCH_BACKWARD = "icons/search_backward.png"; //$NON-NLS-1$
	private static final String SIGMA = "icons/sigma.png"; //$NON-NLS-1$
	private static final String FINDREPLACE = "icons/findreplace.png"; //$NON-NLS-1$
	private static final String SEARCH_OPEN_FILES = "icons/searchopenfiles.png"; //$NON-NLS-1$
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
		content.setLayoutData(createdDefaultGridData(SWT.FILL, SWT.FILL, true, true));
		return content;
	}

	public void createFindBar(ISourceViewer sourceViewer)
	{
		this.sourceViewer = sourceViewer;
		findBarFinder = new FindBarFinder(textEditor, sourceViewer, this);
		findBar = new Composite(composite, SWT.BORDER);
		findBarGridData = createdDefaultGridData(SWT.FILL, SWT.CENTER, true, false);
		findBarGridData.exclude = true;
		findBar.setLayoutData(findBarGridData);

		close = createLabel(CLOSE, true, CLOSE_ENTER);
		close.setToolTipText(Messages.FindBarDecorator_TOOLTIP_HideFindBar);

		findButton = createButton(null, true);
		findButton.setText(Messages.FindBarDecorator_LABEL_FInd);

		combo = createCombo(PREFERENCE_NAME_FIND);

		comboReplace = createCombo(PREFERENCE_NAME_REPLACE);

		ToolBar optionsToolBar = new ToolBar(findBar, SWT.NONE);
		optionsToolBar.setLayoutData(createdDefaultGridData(SWT.LEFT, SWT.CENTER, false, false));

		caseSensitive = createToolItem(optionsToolBar);
		caseSensitive.setImage(FindBarPlugin.getImage(CASE_SENSITIVE));
		caseSensitive.setDisabledImage(FindBarPlugin.getImage(CASE_SENSITIVE_DISABLED));
		caseSensitive.setToolTipText(Messages.FindBarDecorator_LABEL_CaseSensitive);
		caseSensitive.addSelectionListener(this);

		wholeWord = createToolItem(optionsToolBar);
		wholeWord.setImage(FindBarPlugin.getImage(WHOLE_WORD));
		wholeWord.setDisabledImage(FindBarPlugin.getImage(WHOLE_WORD_DISABLED));
		wholeWord.setToolTipText(Messages.FindBarDecorator_LABEL_WholeWord);
		wholeWord.addSelectionListener(this);
		wholeWord.setEnabled(false);

		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget instanceof IFindReplaceTargetExtension3)
		{
			regularExpression = createToolItem(optionsToolBar);
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

		searchBackward = createToolItem(optionsToolBar);
		searchBackward.setImage(FindBarPlugin.getImage(SEARCH_BACKWARD));
		searchBackward.setToolTipText(Messages.FindBarDecorator_LABEL_SearchBackward);
		searchBackward.addSelectionListener(this);

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
		count.setText("       "); //$NON-NLS-1$
		count.setToolTipText(Messages.FindBarDecorator_TOOLTIP_MatchCount);
		count.setLayoutData(createdDefaultGridData(SWT.LEFT, SWT.CENTER, false, false));

		searchInOpenFiles = createButton(SEARCH_OPEN_FILES, true);
		searchInOpenFiles.setToolTipText(Messages.FindBarDecorator_TOOLTIP_SearchInOpenFiles);

		showFindReplaceDialog = createButton(FINDREPLACE, true);
		showFindReplaceDialog.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowFindReplaceDialog);

		disableWhenHidden = new Control[] { combo, comboReplace, optionsToolBar, close, countTotal, findButton,
				replaceFind, replace, replaceAll, count, showFindReplaceDialog, searchInOpenFiles };
		
		int NUMBER_OF_ITEMS = disableWhenHidden.length;
		GridLayout gridLayout = new GridLayout(NUMBER_OF_ITEMS, false);
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		gridLayout.verticalSpacing = 0;
		findBar.setLayout(gridLayout);

	}

	private ToolItem createToolItem(ToolBar optionsToolBar)
	{
		return new ToolItem(optionsToolBar, SWT.CHECK);
	}

	/**
	 * Create a default check (case, word, regexp).
	 */
	private Button createCheck()
	{
		Button button = new Button(findBar, SWT.CHECK);
		button.setLayoutData(createdDefaultGridData());
		button.addSelectionListener(this);
		setDefaultFocusListener(button);
		return button;
	}

	private GridData createdDefaultGridData(int horizontalAlignment, int verticalAlignment,
			boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace)
	{
		GridData gridData = new GridData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace,
				grabExcessVerticalSpace);
		gridData.heightHint = 22;
		return gridData;
	}

	private GridData createdDefaultGridData()
	{
		return createdDefaultGridData(SWT.LEFT, SWT.CENTER, false, false);
	}

	/**
	 * Create a default button (find, replace, replace/find, replace all).
	 */
	private Button createButton(String image, boolean enabled)
	{
		GridData layoutData = createdDefaultGridData();
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
	 * Create a label (with a different image when the mouse is over). When it's clicked, use the default handler to treat the action.
	 */
	private Label createLabel(String image, boolean enabled, String imageEntered)
	{
		GridData layoutData = createdDefaultGridData();
		final Label label = new Label(findBar, SWT.CENTER);
		label.setEnabled(enabled);
		final Image imageRegular = FindBarPlugin.getImage(image);
		label.setImage(imageRegular);

		final Image imageMouseOver = FindBarPlugin.getImage(imageEntered);
		label.addMouseTrackListener(new MouseTrackAdapter()
		{
			public void mouseExit(MouseEvent e)
			{
				label.setImage(imageRegular);
			}

			public void mouseEnter(MouseEvent e)
			{
				label.setImage(imageMouseOver);
			}
		});

		label.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(MouseEvent e)
			{
				handleWidgetSelected(label);
			}
		});
		label.setLayoutData(layoutData);

		return label;
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
		GridData comboGridData = createdDefaultGridData(SWT.FILL, SWT.CENTER, true, false);
		comboGridData.minimumWidth = size.x;
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

	/**
	 * Do searches when we're modifying the text in the combo -- i.e.: incremental search.
	 */
	private final class SearchOnTextChangedModifyListener implements ModifyListener, KeyListener
	{
		private String lastText = EMPTY;

		/**
		 * Used for external clients to ask to start/stop ignoring under certain circumstances through
		 * startIgnore/endIgnore.
		 */
		private int ignore;

		/**
		 * Used internally to know if we should actually do the search -- because we only want to do searches if we're
		 * within a key event (i.e. not scrolling with the mouse) and that key is not something that'll act in the combo
		 * to change the text to a different text (as up/down)
		 */
		private boolean searchOnModifyText;

		public void modifyText(ModifyEvent e)
		{
			if (ignore > 0 || !searchOnModifyText)
			{
				return;
			}
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			if (!preferenceStore.getBoolean(IPreferencesConstants.INCREMENTAL_SEARCH_ON_FIND_BAR))
			{
				return;
			}

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

		public void startIgnore()
		{
			this.ignore += 1;
		}

		public void endIgnore()
		{
			Assert.isTrue(ignore > 0);
			this.ignore -= 1;
		}

		public void keyPressed(KeyEvent e)
		{
			switch (e.keyCode)
			{
				case SWT.ARROW_UP:
				case SWT.ARROW_DOWN:
				case SWT.PAGE_UP:
				case SWT.PAGE_DOWN:
					searchOnModifyText = false;
					break;
				default:
					searchOnModifyText = true;
			}
		}

		public void keyReleased(KeyEvent e)
		{
			searchOnModifyText = false;
		}
	}

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
		handleWidgetSelected(source);
	}

	public void handleWidgetSelected(Object source)
	{
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		CopiedFromFindReplaceDialog findReplaceDialog = new CopiedFromFindReplaceDialog(findReplaceTarget,
				statusLineManager);

		if (source == close)
		{
			hideFindBar();
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
			findNextOrPrev();
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
				if (searchBackward.getSelection())
				{
					findBarFinder.find(false);
				}
				else
				{
					findBarFinder.find(true);
				}
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
		else if (source == searchInOpenFiles)
		{
			searchInOpenFiles();
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
	ToolItem searchBackward;
	ToolItem regularExpression;
	private Label close;
	private Button countTotal;
	private Button findButton;
	private Button replaceFind;
	private Button replace;
	private Button replaceAll;
	private Label count;
	private Button showFindReplaceDialog;
	Button searchInOpenFiles;
	private Control[] disableWhenHidden;

	private FindBarActions findBarActions;
	private FindBarFinder findBarFinder;

	private static final String EMPTY = ""; //$NON-NLS-1$

	private SearchOnTextChangedModifyListener modifyListener = new SearchOnTextChangedModifyListener();

	private void adjustEnablement()
	{
		String text = combo.getText();
		findButton.setEnabled(!EMPTY.equals(text));
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
			removeComboSearchOnTextChangeListener();
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

	public void addComboSearchOnTextChangeListener()
	{
		combo.addKeyListener(modifyListener);
		combo.addModifyListener(modifyListener);
	}

	public void removeComboSearchOnTextChangeListener()
	{
		combo.removeKeyListener(modifyListener);
		combo.removeModifyListener(modifyListener);
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
					addComboSearchOnTextChangeListener();
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

	/* default */void findNextOrPrev()
	{
		if (searchBackward.getSelection())
		{
			findPrevious();
		}
		else
		{
			findNext();

		}
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
				modifyListener.startIgnore();
			}
			combo.setItems(items.toArray(new String[0]));
			combo.select(0);
		}
		finally
		{
			if (removeAddListener)
			{
				modifyListener.endIgnore();
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

	void searchInOpenFiles()
	{
		String searchText = combo.getText();
		if (searchText.length() >= 0)
		{
			boolean isWholeWord = wholeWord.getSelection();
			boolean isRegEx = regularExpression.getSelection();
			boolean isCaseSensitive = caseSensitive.getSelection();
			if (isWholeWord && !isRegEx && isWord(searchText))
			{
				isRegEx = true;
				searchText = "\\b" + searchText + "\\b";
			}

			IStatusLineManager statusLineManager = (IStatusLineManager) textEditor.getAdapter(IStatusLineManager.class);
			FindInOpenDocuments.findInOpenDocuments(searchText, isCaseSensitive, isWholeWord, isRegEx,
					statusLineManager);
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
	boolean isWord(String str)
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
