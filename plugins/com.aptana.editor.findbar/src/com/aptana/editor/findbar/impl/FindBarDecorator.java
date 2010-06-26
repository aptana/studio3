package com.aptana.editor.findbar.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.aptana.editor.findbar.FindBarPlugin;
import com.aptana.editor.findbar.api.IFindBarDecorator;

public class FindBarDecorator implements IFindBarDecorator, SelectionListener
{

	private final ITextEditor textEditor;
	private ISourceViewer sourceViewer;
	private final IEditorStatusLine statusLineManager;
	private final String PREFERENCE_NAME_FIND = "FIND_BAR_DECORATOR_FIND_ENTRIES"; //$NON-NLS-1$
	private final String PREFERENCE_NAME_REPLACE = "FIND_BAR_DECORATOR_REPLACE_ENTRIES"; //$NON-NLS-1$

	public FindBarDecorator(ITextEditor textEditor)
	{
		this.textEditor = textEditor;
		this.statusLineManager = (IEditorStatusLine) textEditor.getAdapter(IEditorStatusLine.class);
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

		close = createButton(FindBarPlugin.CLOSE, true);
		close.setToolTipText(Messages.FindBarDecorator_TOOLTIP_HideFindBar);

		findButton = createButton(null, true);
		findButton.setText(Messages.FindBarDecorator_LABEL_FInd);

		combo = createCombo(PREFERENCE_NAME_FIND);

		comboReplace = createCombo(PREFERENCE_NAME_REPLACE);
		combos = new Combo[] { combo, comboReplace };

		previous = createButton(FindBarPlugin.PREVIOUS, false);
		next = createButton(FindBarPlugin.NEXT, false);

		caseSensitive = createCheck();
		caseSensitive.setText(Messages.FindBarDecorator_LABEL_CaseSensitive);

		wholeWord = createCheck();
		wholeWord.setText(Messages.FindBarDecorator_LABEL_WholeWord);
		wholeWord.setEnabled(false);

		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget instanceof IFindReplaceTargetExtension3)
		{
			regularExpression = createCheck();
			regularExpression.setText(Messages.FindBarDecorator_LABEL_RegularExpression);
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
		countTotal.setImage(FindBarPlugin.getDefault().getImage(FindBarPlugin.SIGMA));
		countTotal.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowMatchCount);

		count = new Label(findBar, SWT.NONE);
		count.setText("            "); //$NON-NLS-1$
		count.setToolTipText(Messages.FindBarDecorator_TOOLTIP_MatchCount);
		count.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Label streach = new Label(findBar, SWT.NONE);
		streach.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		showFindReplaceDialog = createButton(FindBarPlugin.FINDREPLACE, true);
		showFindReplaceDialog.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowFindReplaceDialog);

		disableWhenHidden = new Control[] { combo, comboReplace, caseSensitive, wholeWord, regularExpression, close,
				next, previous, countTotal, findButton, replaceFind, replace, replaceAll, count, showFindReplaceDialog, };
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
			button.setImage(FindBarPlugin.getDefault().getImage(image));
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
		final FocusListener defaultFocusListener = new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				findBarContext(false);
			}

			public void focusGained(FocusEvent e)
			{
				findBarContext(true);
			}
		};
		button.addFocusListener(defaultFocusListener);
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
		combo.setItems((String[]) list.toArray(new String[list.size()]));
		combo.select(0);

		combo.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				findBarContext(false);
			}

			public void focusGained(FocusEvent e)
			{
				combo.setForeground(null);
				findBarContext(true);
			}
		});
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

	public void installActions()
	{
		textEditor.setAction(ITextEditorActionConstants.FIND, new ShowFindBarAction(textEditor));

		// Activate handlers
		IHandlerService handlerService = (IHandlerService) textEditor.getSite().getService(IHandlerService.class);
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.hide", new HideFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.findPrevious", new FindPreviousHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.findNext", new FindNextHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.cut", new CutFromFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.copy", new CopyFromFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.paste", new PasteInFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.selectall", new SelectAllFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.home", new HomeFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.end", new EndFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.selectHome", new SelectHomeFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.selectEnd", new SelectEndFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.focusFind", new FocusFindFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.focusReplace", new FocusReplaceFindBarHandler()); //$NON-NLS-1$
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	@Override
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
			find(true, true);
			showCountTotal();
		}
		else if (source == regularExpression)
		{
			setFindText(combo.getText());
			find(true, true);
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
				if (find(true, true, true, false, true))
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
				find(true);
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

	private Combo combo;
	private Combo comboReplace;
	private Combo[] combos;
	private Button caseSensitive;
	private Button wholeWord;
	private Button regularExpression;
	private int incrementalOffset = -1;
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
	private boolean fActivated;
	private Control[] disableWhenHidden;

	private IContextActivation findBarContextActivation;

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
				find(true, true, wrap);
			}
			showCountTotal();
		}
	};

	private class HideFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			hideFindBar();
			return null;
		}
	}

	private class FindPreviousHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			findPrevious();
			return null;
		}
	}

	private class FindNextHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			findNext();
			return null;
		}
	}

	private class CutFromFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					c.cut();
					break;
				}
			}
			return null;
		}
	}

	private class CopyFromFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					c.copy();
					break;
				}
			}
			return null;
		}
	}

	private class PasteInFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					c.paste();
					break;
				}
			}
			return null;
		}
	}

	private class SelectAllFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					c.setSelection(new Point(0, combo.getText().length()));
					break;
				}
			}
			return null;
		}
	}

	private class HomeFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					c.setSelection(new Point(0, 0));
					break;
				}
			}
			return null;
		}
	}

	private class EndFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					c.setSelection(new Point(combo.getText().length(), combo.getText().length()));
					break;
				}
			}
			return null;
		}
	}

	private class SelectHomeFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					Point selection = c.getSelection();
					c.setSelection(new Point(0, selection.x));
					break;
				}
			}
			return null;
		}
	}

	private class SelectEndFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			for (Combo c : combos)
			{
				if (c.isFocusControl())
				{
					Point selection = c.getSelection();
					c.setSelection(new Point(selection.x, combo.getText().length()));
					break;
				}
			}
			return null;
		}
	}

	private class FocusFindFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			combo.setFocus();
			return null;
		}
	}

	private class FocusReplaceFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			comboReplace.setFocus();
			return null;
		}
	}

	private void adjustEnablement()
	{
		String text = combo.getText();
		previous.setEnabled(!EMPTY.equals(text));
		next.setEnabled(!EMPTY.equals(text));
		count.setText(EMPTY);
		wholeWord.setEnabled(!EMPTY.equals(text) && !regularExpression.getSelection() && isWord(text));
	}

	private void hideFindBar()
	{
		if (findBarGridData.exclude == false)
		{
			findBarGridData.exclude = true;
			composite.layout();
			incrementalOffset = -1;
			combo.removeModifyListener(modifyListener);
			statusLineManager.setMessage(false, EMPTY, null);
		}
		textEditor.setFocus();
		if(disableWhenHidden != null){
			for(Control w:disableWhenHidden){
				w.setEnabled(false);
			}
		}
	}

	private void showFindBar()
	{
		if(disableWhenHidden != null){
			for(Control w:disableWhenHidden){
				w.setEnabled(true);
			}
		}

		boolean wasExcluded = findBarGridData.exclude;
		if (findBarGridData.exclude)
		{
			findBarGridData.exclude = false;
			composite.layout();
		}
		if (!fActivated)
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
			incrementalOffset = -1;
		}
	}

	private void findPrevious()
	{
		find(false);
		setFindText(combo.getText());
	}

	private void findNext()
	{
		find(true);
		setFindText(combo.getText());
	}

	private void find(boolean forward)
	{
		find(forward, false);
	}

	private void find(boolean forward, boolean incremental)
	{
		find(forward, incremental, true);
	}

	private void find(boolean forward, boolean incremental, boolean wrap)
	{
		find(forward, incremental, wrap, false, false);
	}

	private boolean find(boolean forward, boolean incremental, boolean wrap, boolean wrapping,
			boolean initialSearchBeforeReplace)
	{
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget != null)
		{
			try
			{
				if (findReplaceTarget instanceof IFindReplaceTargetExtension)
				{
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.beginSession();
				}
				String findText = combo.getText();
				StyledText textWidget = sourceViewer.getTextWidget();
				int offset = textWidget.getCaretOffset();
				Point selection = textWidget.getSelection();
				if (wrapping)
				{
					if (forward)
					{
						offset = 0;
					}
					else
					{
						offset = sourceViewer.getDocument().getLength() - 1;
					}
				}
				else
				{
					if (forward)
					{
						if (incremental)
						{
							if (incrementalOffset == -1)
							{
								incrementalOffset = offset;
							}
							else
							{
								offset = incrementalOffset;
							}
						}
						else
						{
							incrementalOffset = selection.x;
						}
					}
					else
					{
						incrementalOffset = selection.x;
						if (selection.x != offset)
						{
							offset = selection.x;
						}
					}
				}
				int newOffset = -1;
				if (initialSearchBeforeReplace)
				{
					String selectionText = textWidget.getSelectionText();
					if (selectionText.equals(combo.getText()))
					{
						offset -= (selection.y - selection.x);
					}
					else
					{
						return false;
					}
				}
				if (findReplaceTarget instanceof IFindReplaceTargetExtension3)
				{
					try
					{
						newOffset = ((IFindReplaceTargetExtension3) findReplaceTarget)
								.findAndSelect(offset, findText, forward, caseSensitive.getSelection(), getWholeWord(),
										regularExpression.getSelection());
					}
					catch (PatternSyntaxException e)
					{
						statusLineManager.setMessage(true, e.getMessage(), null);
						return false;
					}
				}
				else
				{
					newOffset = findReplaceTarget.findAndSelect(offset, findText, forward,
							caseSensitive.getSelection(), getWholeWord());
				}

				if (newOffset != -1)
				{
					combo.setForeground(null);
					if (!forward)
					{
						selection = textWidget.getSelection();
						incrementalOffset = selection.x;
					}
					if (wrapping)
					{
						statusLineManager.setMessage(false, Messages.FindBarDecorator_MSG_Wrapped, null);
					}
					else
					{
						statusLineManager.setMessage(false, EMPTY, null);
					}
				}
				else
				{
					if (wrap)
					{
						if (!wrapping)
						{
							return find(forward, incremental, wrap, true, initialSearchBeforeReplace);
						}
					}
					combo.setForeground(combo.getDisplay().getSystemColor(SWT.COLOR_RED));
					textWidget.getDisplay().beep();
					statusLineManager.setMessage(false, Messages.FindBarDecorator_MSG_StringNotFound, null);
				}
			}
			finally
			{
				if (findReplaceTarget instanceof IFindReplaceTargetExtension)
				{
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.endSession();
				}
			}
		}
		return true;
	}

	private boolean getWholeWord()
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
				combo.getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						combo.addModifyListener(modifyListener);
					}
				});
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

	private void findBarContext(boolean activate)
	{
		fActivated = activate;
		IWorkbenchPartSite site = textEditor.getSite();
		IContextService contextService = (IContextService) site.getService(IContextService.class);
		if (activate)
		{
			findBarContextActivation = contextService.activateContext("org.eclipse.ui.textEditorScope.findbar"); //$NON-NLS-1$
		}
		else
		{
			if (findBarContextActivation != null)
			{
				contextService.deactivateContext(findBarContextActivation);
				findBarContextActivation = null;
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
