package com.aptana.editor.findbar.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
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
import org.eclipse.jface.action.IStatusLineManager;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.aptana.editor.findbar.FindBarPlugin;
import com.aptana.editor.findbar.api.IFindBarDecorator;

public class FindBarDecorator implements IFindBarDecorator {

	private final ITextEditor textEditor;
	private ISourceViewer sourceViewer;
	private final IStatusLineManager statusLineManager;

	public FindBarDecorator(ITextEditor textEditor, IStatusLineManager statusLineManager) {
		this.textEditor = textEditor;
		this.statusLineManager = statusLineManager;
	}

	public Composite createFindBarComposite(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);

		Composite content = new Composite(composite, SWT.NONE);
		content.setLayout(new FillLayout());
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return content;
	}
	
	public void createFindBar(ISourceViewer sourceViewer) {
		this.sourceViewer = sourceViewer;
		findBar = new Composite(composite, SWT.BORDER);
		findBarGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		findBarGridData.exclude = true;
		findBar.setLayoutData(findBarGridData);
		
		GridLayout gridLayout = new GridLayout(12, false);
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginRight = 0;
		gridLayout.horizontalSpacing = 4;
		gridLayout.verticalSpacing = 0;
		findBar.setLayout(gridLayout);

		ToolBar closeToolBar = new ToolBar(findBar, SWT.NONE);
		ToolItem close = new ToolItem(closeToolBar, SWT.NONE);
		close.setToolTipText(Messages.FindBarDecorator_TOOLTIP_HideFindBar);
		close.setImage(FindBarPlugin.getDefault().getImage(FindBarPlugin.CLOSE));
		close.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				hideFindBar();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		closeToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Label findLabel = new Label(findBar, SWT.NONE);
		findLabel.setText(Messages.FindBarDecorator_LABEL_FInd);
		GridData findLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		findLabelGridData.horizontalIndent = 5;
		findLabel.setLayoutData(findLabelGridData);
		
		combo = new Combo(findBar, SWT.DROP_DOWN);
		combo.setText("                            "); //$NON-NLS-1$
		Point size = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		GridData comboGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		comboGridData.horizontalIndent = 2;
		comboGridData.widthHint = size.x;
		combo.setLayoutData(comboGridData);
		combo.setText(EMPTY);
		combo.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				findBarContext(false);
			}

			public void focusGained(FocusEvent e) {
				combo.setForeground(null);
				findBarContext(true);
			}
		});
		
		ToolBar previousNextToolBar = new ToolBar(findBar, SWT.NONE);
		GridData previousNextToolBarGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		previousNextToolBar.setLayoutData(previousNextToolBarGridData);

		previous = new ToolItem(previousNextToolBar, SWT.PUSH);
		previous.setEnabled(false);
		previous.setImage(FindBarPlugin.getDefault().getImage(FindBarPlugin.PREVIOUS));
		previous.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				findPrevious();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		next = new ToolItem(previousNextToolBar, SWT.PUSH);
		next.setEnabled(false);
		next.setImage(FindBarPlugin.getDefault().getImage(FindBarPlugin.NEXT));
		next.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				findNext();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		caseSensitive = new Button(findBar, SWT.CHECK);
		caseSensitive.setText(Messages.FindBarDecorator_LABEL_CaseSensitive);
		caseSensitive.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		caseSensitive.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				find(true, true);
				showCountTotal();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		wholeWord = new Button(findBar, SWT.CHECK);
		wholeWord.setText(Messages.FindBarDecorator_LABEL_WholeWord);
		wholeWord.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		wholeWord.setEnabled(false);
		wholeWord.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				find(true, true);
				showCountTotal();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget instanceof IFindReplaceTargetExtension3) {
			regularExpression = new Button(findBar, SWT.CHECK);
			regularExpression.setText(Messages.FindBarDecorator_LABEL_RegularExpression);
			regularExpression.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			regularExpression.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					find(true, true);
					showCountTotal();
					adjustRegularExpressionState();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		
		ToolBar countTotalToolBar = new ToolBar(findBar, SWT.NONE);
		countTotalToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		// Add separator
		new ToolItem(countTotalToolBar, SWT.SEPARATOR);
		
		countTotal = new ToolItem(countTotalToolBar, SWT.CHECK);
		countTotal.setImage(FindBarPlugin.getDefault().getImage(FindBarPlugin.SIGMA));
		countTotal.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowMatchCount);
		countTotal.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				showCountTotal();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		count = new Label(findBar, SWT.NONE);
		count.setText("            "); //$NON-NLS-1$
		count.setToolTipText(Messages.FindBarDecorator_TOOLTIP_MatchCount);
		count.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Label streach = new Label(findBar, SWT.PUSH);
		streach.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		ToolBar showFindReplaceDialogToolBar = new ToolBar(findBar, SWT.NONE);
		showFindReplaceDialogToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		ToolItem showFindReplaceDialog = new ToolItem(showFindReplaceDialogToolBar, SWT.PUSH);
		showFindReplaceDialog.setImage(FindBarPlugin.getDefault().getImage(FindBarPlugin.FINDREPLACE));
		showFindReplaceDialog.setToolTipText(Messages.FindBarDecorator_TOOLTIP_ShowFindReplaceDialog); 
		showFindReplaceDialog.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				showFindReplaceDialog();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.findbar.api.IFindBarDecorator#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			showFindBar();
		} else {
			hideFindBar();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.findbar.api.IFindBarDecorator#isVisible()
	 */
	public boolean isVisible() {
		return !findBarGridData.exclude;
	}
	
	public void installActions() {
		textEditor.setAction(ITextEditorActionConstants.FIND, new ShowFindBarAction(textEditor));
		
		// Activate handlers
		IHandlerService handlerService = (IHandlerService) textEditor.getSite().getService(IHandlerService.class);
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.hide", new HideFindBarHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.findPrevious", new FindPreviousHandler()); //$NON-NLS-1$
		handlerService.activateHandler("org.eclipse.ui.edit.findbar.findNext", new FindNextHandler()); //$NON-NLS-1$
	}
	
	boolean isActive() {
		return isVisible() && (combo.getDisplay().getFocusControl() == combo);
	}
	
	private Composite composite;
	
	private Composite findBar;
	private GridData findBarGridData;
	
	private Combo combo;
	private Button caseSensitive;
	private Button wholeWord;
	private Button regularExpression;
	private int incrementalOffset = -1;
	private ToolItem next;
	private ToolItem previous;
	private ToolItem countTotal;
	private Label count;
	
	private IContextActivation findBarContextActivation;
	
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private ModifyListener modifyListener = new ModifyListener() {
		private String lastText = EMPTY;

		public void modifyText(ModifyEvent e) {
			combo.setForeground(null);
			boolean wrap = true;
			String text = combo.getText();
			if (lastText.startsWith(text)) {
				wrap = false;
			}
			lastText = text;
			adjustEnablement();
			adjustRegularExpressionState();
			if (EMPTY.equals(text)) {
				ISelectionProvider selectionProvider = textEditor.getSelectionProvider();
				ISelection selection = selectionProvider.getSelection();
				if (selection instanceof TextSelection) {
					ITextSelection textSelection = (ITextSelection) selection;
					selectionProvider.setSelection(
							new TextSelection(textSelection.getOffset(), 0));
				}
			} else {
				find(true, true, wrap);
			}
			showCountTotal();
		}
	};

	private class HideFindBarHandler extends AbstractHandler {
		public Object execute(ExecutionEvent event) throws ExecutionException {
			hideFindBar();
			return null;
		}
	}
	
	private class FindPreviousHandler extends AbstractHandler {
		public Object execute(ExecutionEvent event) throws ExecutionException {
			findPrevious();
			return null;
		}
	}
	
	private class FindNextHandler extends AbstractHandler {
		public Object execute(ExecutionEvent event) throws ExecutionException {
			findNext();
			return null;
		}
	}
	
	private void adjustEnablement() {
		String text = combo.getText();
		previous.setEnabled(!EMPTY.equals(text));
		next.setEnabled(!EMPTY.equals(text));
		count.setText(EMPTY);
		wholeWord.setEnabled((!EMPTY.equals(text)) && (isWord(text)));
	}
	
	private void adjustRegularExpressionState() {
		if (regularExpression == null) {
			return;
		}
//		regularExpression.setForeground(null);
		String findText = combo.getText();
		if (EMPTY.equals(findText)) {
			return;
		}
		if (regularExpression.getSelection()) {
			try {
				Pattern.compile(findText);
//				regularExpression.setForeground(null);
			} catch (PatternSyntaxException pse) {
//				regularExpression.setForeground(regularExpression.getDisplay().getSystemColor(SWT.COLOR_RED));
			}
		}
	}

	private void hideFindBar() {
		if (findBarGridData.exclude == false) {
			findBarGridData.exclude = true;
			composite.layout();
			incrementalOffset = -1;
			combo.removeModifyListener(modifyListener);
		}
		textEditor.setFocus();
	}
	
	private void showFindBar() {
		boolean wasExcluded = findBarGridData.exclude;
		if (findBarGridData.exclude) {
			findBarGridData.exclude = false;
			composite.layout();
		}
		ISelection selection = sourceViewer.getSelectionProvider()
				.getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			String text = textSelection.getText();
			if (text.indexOf("\n") == -1 && text.indexOf("\r") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
				setFindText(text, !wasExcluded);
			}
		}
		if (wasExcluded) {
			combo.getDisplay().asyncExec(new Runnable() {
				public void run() {
					combo.addModifyListener(modifyListener);
				}
			});		}
		adjustEnablement();
		boolean comboHasFocus = combo.isFocusControl();
		if (!comboHasFocus) {
			combo.setFocus();
			incrementalOffset = -1;
		}
	}
	
	private void findPrevious() {
		find(false);
		setFindText(combo.getText());
	}
	
	private void findNext() {
		find(true);
		setFindText(combo.getText());		
	}

	private void find(boolean forward) {
		find(forward, false);
	}

	private void find(boolean forward, boolean incremental) {
		find(forward, incremental, true, false);
	}

	private void find(boolean forward, boolean incremental, boolean wrap) {
		find(forward, incremental, wrap, false);
	}

	private void find(boolean forward, boolean incremental, boolean wrap,
			boolean wrapping) {
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget != null) {
			try {
				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.beginSession();
				}
				String findText = combo.getText();
				StyledText textWidget = sourceViewer.getTextWidget();
				int offset = textWidget.getCaretOffset();
				Point selection = textWidget.getSelection();
				if (wrapping) {
					if (forward) {
						offset = 0;
					} else {
						offset = sourceViewer.getDocument().getLength() - 1;
					}
				} else {
					if (forward) {
						if (incremental) {
							if (incrementalOffset == -1) {
								incrementalOffset = offset;
							} else {
								offset = incrementalOffset;
							}
						} else {
							incrementalOffset = selection.x;
						}
					} else {
						incrementalOffset = selection.x;
						if (selection.x != offset) {
							offset = selection.x;
						}
					}
				}
				int newOffset = -1;
				if (findReplaceTarget instanceof IFindReplaceTargetExtension3) {
					newOffset = ((IFindReplaceTargetExtension3) findReplaceTarget)
							.findAndSelect(offset, findText, forward,
									caseSensitive.getSelection(), wholeWord
											.getEnabled()
											&& wholeWord.getSelection(),
									regularExpression.getSelection());
				} else {
					newOffset = findReplaceTarget.findAndSelect(offset,
							findText, forward, caseSensitive.getSelection(),
							wholeWord.getEnabled() && wholeWord.getSelection());
				}

				if (newOffset != -1) {
					combo.setForeground(null);
					if (!forward) {
						selection = textWidget.getSelection();
						incrementalOffset = selection.x;
					}
					if (wrapping) {
						statusLineManager.setMessage(Messages.FindBarDecorator_MSG_Wrapped);
					} else {
						statusLineManager.setMessage(EMPTY);
					}
				} else {
					if (wrap) {
						if (!wrapping) {
							find(forward, incremental, wrap, true);
							return;
						}
					}
					combo.setForeground(combo.getDisplay().getSystemColor(
							SWT.COLOR_RED));
					textWidget.getDisplay().beep();
					statusLineManager.setMessage(Messages.FindBarDecorator_MSG_StringNotFound);
				}
			} finally {
				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.endSession();
				}
			}
		}
	}
	
	private void setFindText(String findText) {
		setFindText(findText, true);
	}
	
	private void setFindText(String findText, boolean removeAddListener) {
		String[] items = combo.getItems();
		Set<String> itemSet = new LinkedHashSet<String>();
		itemSet.add(findText);
		itemSet.addAll(Arrays.asList(items));
		try {
			if (removeAddListener) {
				combo.removeModifyListener(modifyListener);
			}
			combo.setItems(itemSet.toArray(new String[0]));
			combo.select(0);
		} finally {
			if (removeAddListener) {
				combo.getDisplay().asyncExec(new Runnable() {
					public void run() {
						combo.addModifyListener(modifyListener);
					}
				});
			}
		}
	}
	
	private static final int TOO_MANY = Integer.getInteger(FindBarDecorator.class.getName()+".TOO_MANY", 100); //$NON-NLS-1$
	private void showCountTotal() {
		if (!countTotal.getSelection()) {
			count.setText(EMPTY);
			return;
		}
		String patternString = combo.getText();
		boolean patternStringIsAWord = isWord(patternString);
		int total = 0;
		if (!EMPTY.equals(patternString)) {
			String text = sourceViewer.getDocument().get();
			int flags = 0;
			if (!caseSensitive.getSelection()) {
				flags |= Pattern.CASE_INSENSITIVE;
			}
			if (!regularExpression.getSelection()) {
				patternString = Pattern.quote(patternString);
			}
			if (patternStringIsAWord && wholeWord.getSelection()) {
				patternString = "\\b" + patternString + "\\b"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			Pattern pattern = Pattern.compile(patternString, flags);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find(0)) {
				total = 1;
				while (matcher.find()) {
					++total;
					if ((TOO_MANY != -1) && total > TOO_MANY) {
						break;
					}
				}
			}
		}
		if ((TOO_MANY != -1) && total > TOO_MANY) {
			count.setText("> " + TOO_MANY); //$NON-NLS-1$
		} else {
			count.setText(String.valueOf(total));
		}
	}
	
	private void showFindReplaceDialog() {
		IWorkbenchPartSite site = textEditor.getSite();
		ICommandService commandService = (ICommandService) site.getService(ICommandService.class);
		Command findReplacecommand = commandService.getCommand(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
		IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
		if (handlerService != null) {
			try {
				handlerService.executeCommand(
						new ParameterizedCommand(findReplacecommand, null), null);
			} catch (ExecutionException e1) {
			} catch (NotDefinedException e1) {
			} catch (NotEnabledException e1) {
			} catch (NotHandledException e1) {
			}
		}
	}	
	
	private void findBarContext(boolean activate) {
		IWorkbenchPartSite site = textEditor.getSite();
		IContextService contextService = (IContextService) site.getService(IContextService.class);
		if (activate) {
			findBarContextActivation = contextService.activateContext("org.eclipse.ui.textEditorScope.findbar"); //$NON-NLS-1$
		} else {
			if (findBarContextActivation != null); {
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
	private boolean isWord(String str) {
		if (str == null || str.length() == 0)
			return false;

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isJavaIdentifierPart(str.charAt(i)))
				return false;
		}
		return true;
	}

}
