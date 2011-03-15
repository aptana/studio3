/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.util.ArrayList;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
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
import com.aptana.editor.findbar.impl.FindBarEntriesHelper.EntriesControlHandle;
import com.aptana.editor.findbar.preferences.IPreferencesConstants;

/**
 * Main control of the find bar.
 * 
 * @author Fabio Zadrozny
 */
public class FindBarDecorator implements IFindBarDecorator, SelectionListener
{

	/**
	 * Yes, the configuration for the find bar is shared across all find bars (so, when some configuration changes in
	 * one, all are updated)
	 */
	private static final FindBarConfiguration findBarConfiguration = new FindBarConfiguration();

	/**
	 * Yes, the entries in the combos are also always synchronized.
	 */
	private static final FindBarEntriesHelper findBarEntriesHelper = new FindBarEntriesHelper();

	private static final String CLOSE = "icons/close.png"; //$NON-NLS-1$
	private static final String CLOSE_ENTER = "icons/close_enter.png"; //$NON-NLS-1$
	private static final String SEARCH_BACKWARD = "icons/search_backward.png"; //$NON-NLS-1$
	private static final String OPTIONS = "icons/gear.png"; //$NON-NLS-1$
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

	private List<FindBarOption> fFindBarOptions = new ArrayList<FindBarOption>();

	private Composite composite;

	private Composite findBar;
	private GridData findBarGridData;

	/* default */Combo combo;
	/* default */Combo comboReplace;
	ToolItem caseSensitive;
	ToolItem wholeWord;
	ToolItem searchBackward;
	ToolItem options;
	ToolItem regularExpression;
	private Label close;
	private Button countTotal;
	private Button findButton;
	private Button replaceFind;
	private Button replace;
	private Button replaceAll;
	private Button showFindReplaceDialog;
	Button searchInOpenFiles;
	private Control[] disableWhenHidden;

	private FindBarActions findBarActions;

	FindBarActions getFindBarActions()
	{
		return findBarActions;
	}

	private FindBarFinder findBarFinder;

	private static final String EMPTY = ""; //$NON-NLS-1$

	private SearchOnTextChangedModifyListener modifyListener = new SearchOnTextChangedModifyListener();

	private final List<EntriesControlHandle> entriesControlHandles = new ArrayList<FindBarEntriesHelper.EntriesControlHandle>();

	public FindBarDecorator(final ITextEditor textEditor)
	{
		this.textEditor = textEditor;
		this.statusLineManager = (IEditorStatusLine) textEditor.getAdapter(IEditorStatusLine.class);
		findBarActions = new FindBarActions(textEditor, this);

		fFindBarOptions.add(new FindBarOption(
				"caseSensitive",//$NON-NLS-1$ 
				CASE_SENSITIVE, CASE_SENSITIVE_DISABLED, Messages.FindBarDecorator_LABEL_CaseSensitive, this,
				IPreferencesConstants.CASE_SENSITIVE_IN_FIND_BAR)
		{
			public void execute(FindBarDecorator dec)
			{
				dec.findNextOrprevAfterChangeOption();
			}
		});
		fFindBarOptions.add(new FindBarOption(
				"wholeWord", //$NON-NLS-1$
				WHOLE_WORD, WHOLE_WORD_DISABLED, Messages.FindBarDecorator_LABEL_WholeWord, this, false,
				IPreferencesConstants.WHOLE_WORD_IN_FIND_BAR)
		{
			public void execute(FindBarDecorator dec)
			{
				dec.findNextOrprevAfterChangeOption();
			}
		});
		fFindBarOptions.add(new FindBarOption(
				"regularExpression", //$NON-NLS-1$
				REGEX, REGEX_DISABLED, Messages.FindBarDecorator_LABEL_RegularExpression, this,
				IPreferencesConstants.REGULAR_EXPRESSION_IN_FIND_BAR)
		{

			@Override
			protected boolean canCreateItem()
			{
				// Cannot create it if it's not supported.
				IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor
						.getAdapter(IFindReplaceTarget.class);
				return findReplaceTarget instanceof IFindReplaceTargetExtension3;
			}

			@Override
			public ToolItem createToolItem(ToolBar optionsToolBar)
			{
				ToolItem item = super.createToolItem(optionsToolBar);
				if (item == null)
				{
					return null;
				}
				item.addSelectionListener(new SelectionListener()
				{

					public void widgetSelected(SelectionEvent e)
					{
						adjustEnablement(); // Because whole word is not valid when regexp is chosen.
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
				return item;
			}

			public void execute(FindBarDecorator dec)
			{
				dec.findNextOrprevAfterChangeOption();
			}
		});
		fFindBarOptions.add(new FindBarOption(
				"searchBackward", //$NON-NLS-1$
				SEARCH_BACKWARD, null, Messages.FindBarDecorator_LABEL_SearchBackward, this,
				IPreferencesConstants.SEARCH_BACKWARD_IN_FIND_BAR)
		{
			public void execute(FindBarDecorator dec)
			{
				// no-op (don't do anything in this case)
			}
		});
		FindBarOption opt = new FindBarOption("options", //$NON-NLS-1$
				OPTIONS, null, Messages.FindBarDecorator_LABEL_ShowOptions, this, null)
		{
			public void execute(FindBarDecorator dec)
			{
				showOptions(true);
			}
		};
		opt.createMenuItem = false;
		fFindBarOptions.add(opt);

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

		for (FindBarOption option : fFindBarOptions)
		{
			option.createToolItem(optionsToolBar);
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
		countTotal.setText("           "); //$NON-NLS-1$
		GridData countLayoutData = (GridData) countTotal.getLayoutData();
		countLayoutData.minimumWidth = countTotal.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		countTotal.setLayoutData(countLayoutData);

		searchInOpenFiles = createButton(SEARCH_OPEN_FILES, true);
		searchInOpenFiles.setToolTipText(Messages.FindBarDecorator_TOOLTIP_SearchInOpenFiles);

		showFindReplaceDialog = createButton(FINDREPLACE, true);
		showFindReplaceDialog.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowFindReplaceDialog);

		disableWhenHidden = new Control[] { combo, comboReplace, optionsToolBar, close, countTotal, findButton,
				replaceFind, replace, replaceAll, showFindReplaceDialog, searchInOpenFiles };

		int NUMBER_OF_ITEMS = disableWhenHidden.length;
		GridLayout gridLayout = new GridLayout(NUMBER_OF_ITEMS, false);
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 4;
		gridLayout.verticalSpacing = 0;
		findBar.setLayout(gridLayout);

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
	 * Create a label (with a different image when the mouse is over). When it's clicked, use the default handler to
	 * treat the action.
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
		GridData comboGridData = createdDefaultGridData(SWT.FILL, SWT.CENTER, true, false);
		Point size = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		comboGridData.minimumWidth = size.x;
		combo.setLayoutData(comboGridData);

		entriesControlHandles.add(findBarEntriesHelper.register(combo, modifyListener, preferenceName));

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
	private Color fStringNotFoundColor;

	/**
	 * Do searches when we're modifying the text in the combo -- i.e.: incremental search.
	 */
	private final class SearchOnTextChangedModifyListener implements ModifyListener, KeyListener, IStartEndIgnore
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
			adjustEnablement();
			if (ignore > 0 || !searchOnModifyText)
			{
				return;
			}
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			if (!preferenceStore.getBoolean(IPreferencesConstants.INCREMENTAL_SEARCH_ON_FIND_BAR))
			{
				return;
			}

			combo.setBackground(null);
			boolean wrap = true;
			String text = combo.getText();
			if (lastText.startsWith(text))
			{
				wrap = false;
			}
			lastText = text;
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

	public Color getfStringNotFoundColor()
	{
		if (fStringNotFoundColor == null)
		{
			fStringNotFoundColor = new Color(Display.getCurrent(), 0xff, 0xcc, 0x66);
		}
		return fStringNotFoundColor;
	}

	public void dispose()
	{
		IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
		preferenceStore.removePropertyChangeListener(fFindBarActionOnPropertyChange);
		fOriginalFindBarAction = null;
		if (fStringNotFoundColor != null)
		{
			fStringNotFoundColor.dispose();
			fStringNotFoundColor = null;
		}
		for (FindBarOption opt : this.fFindBarOptions)
		{
			opt.dispose();
		}
		findBarEntriesHelper.unregister(entriesControlHandles);
		entriesControlHandles.clear();
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
		if (source == close)
		{
			hideFindBar();
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
			replace(source == replaceFind);
		}
		else if (source == replaceAll)
		{
			replaceAll();
		}
		else if (source == searchInOpenFiles)
		{
			searchInOpenFiles();
		}
		else if (source == showFindReplaceDialog)
		{
			showFindReplaceDialog();
		}
		else
		{
			FindBarPlugin.log(new RuntimeException("Unhandled selection for widget: " + source)); //$NON-NLS-1$
		}
	}

	private void findNextOrprevAfterChangeOption()
	{
		setFindText(combo.getText());
		findBarFinder.find(!getConfiguration().getSearchBackward(), true);
		showCountTotal();
	}

	private void replace(boolean newFind)
	{
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		CopiedFromFindReplaceDialog findReplaceDialog = new CopiedFromFindReplaceDialog(findReplaceTarget,
				statusLineManager);

		setFindText(combo.getText());
		setFindTextOnReplace(comboReplace.getText());
		PatternSyntaxException exception = null;
		try
		{
			findReplaceDialog.replaceSelection(comboReplace.getText(), getConfiguration().getRegularExpression());
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
					findReplaceDialog.replaceSelection(comboReplace.getText(), getConfiguration()
							.getRegularExpression());
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
		if (newFind)
		{
			if (getConfiguration().getSearchBackward())
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

	private void replaceAll()
	{
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		CopiedFromFindReplaceDialog findReplaceDialog = new CopiedFromFindReplaceDialog(findReplaceTarget,
				statusLineManager);

		setFindText(combo.getText());
		setFindTextOnReplace(comboReplace.getText());
		try
		{
			int replaced = findReplaceDialog.replaceAll(combo.getText(), comboReplace.getText(), true,
					getConfiguration().getCaseSensitive(), getWholeWord(), getConfiguration().getRegularExpression());
			showCountTotal();
			statusLineManager.setMessage(false, String.format(Messages.FindBarDecorator_MSG_Replaced, replaced), null);
		}
		catch (PatternSyntaxException e1)
		{
			statusLineManager.setMessage(true, e1.getMessage(), null);
		}
	}

	boolean isActive()
	{
		return isVisible() && (combo.getDisplay().getFocusControl() == combo);
	}

	private void adjustEnablement()
	{
		String text = combo.getText();
		findButton.setEnabled(!EMPTY.equals(text));
		countTotal.setText(EMPTY);
		wholeWord.setEnabled(!EMPTY.equals(text) && !getConfiguration().getRegularExpression() && isWord(text));
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
		if (wasExcluded)
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
					setFindText(text);
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
		if (getConfiguration().getSearchBackward())
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
		return wholeWord.getEnabled() && getConfiguration().getWholeWord()
				&& !getConfiguration().getRegularExpression();
	}

	private void setFindText(String findText)
	{
		setFindText(findText, combo, PREFERENCE_NAME_FIND);
	}

	private void setFindTextOnReplace(String findText)
	{
		setFindText(findText, comboReplace, PREFERENCE_NAME_REPLACE);
	}

	private void setFindText(String findText, final Combo combo, String preferenceName)
	{
		findBarEntriesHelper.addEntry(findText, preferenceName);
	}

	private static final int TOO_MANY = Integer.getInteger(FindBarDecorator.class.getName() + ".TOO_MANY", 100); //$NON-NLS-1$

	private void showCountTotal()
	{
		if (!countTotal.getSelection())
		{
			countTotal.setText(EMPTY);
			return;
		}
		String patternString = combo.getText();
		boolean patternStringIsAWord = isWord(patternString);
		int total = 0;
		if (!EMPTY.equals(patternString))
		{
			String text = sourceViewer.getDocument().get();
			int flags = 0;
			if (!getConfiguration().getCaseSensitive())
			{
				flags |= Pattern.CASE_INSENSITIVE;
			}
			if (!getConfiguration().getRegularExpression())
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
			countTotal.setText("> " + TOO_MANY); //$NON-NLS-1$
		}
		else
		{
			countTotal.setText(String.valueOf(total));
		}
	}

	void showOptions(boolean useMousePos)
	{
		Shell shell = new Shell(Display.getCurrent());
		Menu menu = new Menu(shell, SWT.POP_UP);

		for (FindBarOption option : fFindBarOptions)
		{
			option.createMenuItem(menu);
		}

		Point location;
		if (useMousePos)
		{
			Display current = Display.getCurrent();
			location = current.getCursorLocation();
		}
		else
		{
			Rectangle bounds = options.getBounds();
			location = options.getParent().toDisplay(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
		}
		menu.setLocation(location);
		menu.setVisible(true);
	}

	void searchInOpenFiles()
	{
		String searchText = combo.getText();
		if (searchText.length() >= 0)
		{
			boolean isWholeWord = getConfiguration().getWholeWord();
			boolean isRegEx = getConfiguration().getRegularExpression();
			boolean isCaseSensitive = getConfiguration().getCaseSensitive();
			if (isWholeWord && !isRegEx && isWord(searchText))
			{
				isRegEx = true;
				searchText = "\\b" + searchText + "\\b"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			IStatusLineManager statusLineManager = (IStatusLineManager) textEditor.getAdapter(IStatusLineManager.class);
			FindInOpenDocuments.findInOpenDocuments(searchText, isCaseSensitive, isWholeWord, isRegEx,
					statusLineManager);
		}

	}

	void showFindReplaceDialog()
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

	public FindBarConfiguration getConfiguration()
	{
		return findBarConfiguration;
	}

}
