/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.replace.SimpleTextPatternReplacer;
import com.aptana.editor.findbar.FindBarPlugin;
import com.aptana.editor.findbar.api.IFindBarDecorator;
import com.aptana.editor.findbar.impl.FindBarEntriesHelper.EntriesControlHandle;
import com.aptana.editor.findbar.preferences.IPreferencesConstants;
import com.aptana.ui.util.UIUtils;

/**
 * Main control of the find bar.
 * 
 * @author Fabio Zadrozny
 */
public class FindBarDecorator implements IFindBarDecorator, SelectionListener
{
	private static final String REGEX_WORD_BOUNDARY = "\\b"; //$NON-NLS-1$
	private static final String REGEX_LITERAL_END = "\\E"; //$NON-NLS-1$
	private static final String REGEX_LITERAL_START = "\\Q"; //$NON-NLS-1$

	/**
	 * TextPatternReplacer to sanitize newlines
	 */
	private static final SimpleTextPatternReplacer NEWLINE_SANITIZER;
	static
	{
		NEWLINE_SANITIZER = new SimpleTextPatternReplacer();
		NEWLINE_SANITIZER.addPattern("\\n", "\\\\n"); //$NON-NLS-1$ //$NON-NLS-2$
		NEWLINE_SANITIZER.addPattern("\\r", "\\\\r"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static final FindBarVisibilityControl findBarVisibilityControl = new FindBarVisibilityControl();

	/* default */static final EclipseFindSettings eclipseFindSettings = new EclipseFindSettings();

	/**
	 * Yes, the configuration for the find bar is shared across all find bars (so, when some configuration changes in
	 * one, all are updated)
	 */
	/* default */static final FindBarConfiguration findBarConfiguration = new FindBarConfiguration(eclipseFindSettings);

	/**
	 * Yes, the entries in the combos are also always synchronized.
	 */
	private static final FindBarEntriesHelper findBarEntriesHelper = new FindBarEntriesHelper(eclipseFindSettings);

	private static final String CLOSE = "icons/close.png"; //$NON-NLS-1$
	private static final String CLOSE_ENTER = "icons/close_enter.png"; //$NON-NLS-1$
	private static final String SEARCH_BACKWARD = "icons/search_backward.png"; //$NON-NLS-1$
	private static final String OPTIONS = "icons/gear.png"; //$NON-NLS-1$
	private static final String SIGMA = "icons/sigma.png"; //$NON-NLS-1$
	private static final String CASE_SENSITIVE = "icons/casesensitive.png"; //$NON-NLS-1$
	private static final String CASE_SENSITIVE_DISABLED = "icons/casesensitive_disabled.png"; //$NON-NLS-1$
	private static final String REGEX = "icons/regex.png"; //$NON-NLS-1$
	private static final String REGEX_DISABLED = "icons/regex_disabled.png"; //$NON-NLS-1$
	private static final String WHOLE_WORD = "icons/whole_word.png"; //$NON-NLS-1$
	private static final String ICON_SEARCH_SELECTION = "icons/elcl16/segment_edit.png"; //$NON-NLS-1$
	private static final String WHOLE_WORD_DISABLED = "icons/whole_word_disabled.png"; //$NON-NLS-1$

	private final ITextEditor textEditor;
	private ISourceViewer sourceViewer;
	final IEditorStatusLine statusLineManager;
	private IAction fOriginalFindBarAction;

	private List<FindBarOption> fFindBarOptions = new ArrayList<FindBarOption>();

	private Composite composite;

	private Composite findBar;
	private GridData findBarGridData;

	/* default */Text textFind;
	ToolItem findHistory;
	int lastFindHistory;

	/* default */Text textReplace;
	ToolItem replaceHistory;
	int lastReplaceHistory;

	ToolItem caseSensitive;
	ToolItem wholeWord;
	ToolItem searchBackward;
	ToolItem options;
	ToolItem regularExpression;
	ToolItem countMatches;
	ToolItem searchSelection;
	ToolItem scopeToolItem;
	private Label close;
	Button findButton;
	private Button replaceFind;
	private Button replace;
	private Button replaceAll;
	private Control[] disableWhenHidden;
	int lastCountPosition, lastCountTotal, lastCountOffset;

	private Sash sash;
	private GridData sashGridData;
	private GridData separatorGridData;
	private Composite editorContent;

	private List<Control> findBarTabOrder = new ArrayList<Control>(4);
	private FindBarActions findBarActions;

	FindBarActions getFindBarActions()
	{
		return findBarActions;
	}

	int minimumFindBarHeight = 0;

	private FindBarFinder findBarFinder;

	private SearchOnTextChangedModifyListener modifyListener = new SearchOnTextChangedModifyListener();

	private final List<EntriesControlHandle> entriesControlHandles = new ArrayList<FindBarEntriesHelper.EntriesControlHandle>();

	public static final Color DISABLED_COLOR = UIUtils.getDisplay().getSystemColor(SWT.COLOR_GRAY);

	enum FindScope
	{
		//@formatter:off
		CURRENT_FILE(Messages.FindBarDecorator_LABEL_Scope_Current_File),
		OPEN_FILES(Messages.FindBarDecorator_LABEL_Scope_Open_Files),
		ENCLOSING_PROJECT(Messages.FindBarDecorator_LABEL_Scope_Enclosing_Project),
		WORKSPACE(Messages.FindBarDecorator_LABEL_Scope_Workspace);
		//@formatter:on

		private String name;

		FindScope(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	FindScope findScope;
	Map<FindScope, String> scopeMap = new LinkedHashMap<FindScope, String>(4);

	// Focus listener used to trigger when the find bar focus is lost
	FocusListener textViewerFocusListener = new FocusListener()
	{

		public void focusLost(FocusEvent e)
		{
		}

		public void focusGained(FocusEvent e)
		{
			findBarFocusLost();
		}
	};

	KeyListener textKeyListner = new KeyListener()
	{

		public void keyReleased(KeyEvent e)
		{
		}

		public void keyPressed(KeyEvent e)
		{
			boolean isCommandCtrl = e.stateMask == SWT.MOD1;
			boolean isCKey = e.character == 'A' || e.character == 'a';

			if (isCommandCtrl && isCKey)
			{
				((Text) e.widget).selectAll();
			}
			else if (e.character == SWT.TAB)
			{
				if (e.stateMask == 0 || e.stateMask == SWT.SHIFT)
				{
					e.doit = false;
					((Text) e.widget).traverse(e.stateMask == 0 ? SWT.TRAVERSE_TAB_NEXT : SWT.TRAVERSE_TAB_PREVIOUS);
				}
			}
			else if ((e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) && e.stateMask == SWT.MOD1)
			{
				e.doit = false;
				String preferenceName = (e.widget == textFind) ? FindBarEntriesHelper.PREFERENCE_NAME_FIND
						: FindBarEntriesHelper.PREFERENCE_NAME_REPLACE;
				insertHistory(preferenceName, e.keyCode == SWT.ARROW_DOWN);
			}
		}
	};

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
		fFindBarOptions.add(new FindBarOption("searchSelection", //$NON-NLS-1$
				ICON_SEARCH_SELECTION, null, Messages.FindBarDecorator_LABEL_SearchSelection, this, true, null)
		{
			public void execute(FindBarDecorator dec)
			{
				updateSearchSelection();
			}

			boolean isCheckable()
			{
				return true;
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
				IFindReplaceTarget findReplaceTarget = getFindReplaceTarget();
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
		countMatchesOption = new FindBarOption(
				"countMatches", //$NON-NLS-1$
				SIGMA, null, Messages.FindBarDecorator_TOOLTIP_MatchCount, this,
				IPreferencesConstants.MATCH_COUNT_IN_FIND_BAR)
		{
			public void execute(FindBarDecorator dec)
			{
				showCountTotal();
			}
		};
		fFindBarOptions.add(new FindBarOption(
				"searchBackward", //$NON-NLS-1$
				SEARCH_BACKWARD, null, Messages.FindBarDecorator_LABEL_SearchBackward, this,
				IPreferencesConstants.SEARCH_BACKWARD_IN_FIND_BAR)
		{
			public void execute(FindBarDecorator dec)
			{
				// no-op (don't do anything in this case)
				dec.showCountTotal();
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

		scopeMap.put(FindScope.CURRENT_FILE, FindBarPlugin.ICON_SEARCH_CURRENT_FILE);
		scopeMap.put(FindScope.OPEN_FILES, FindBarPlugin.ICON_SEARCH_OPEN_FILES);
		scopeMap.put(FindScope.ENCLOSING_PROJECT, FindBarPlugin.ICON_SEARCH_PROJECT);
		scopeMap.put(FindScope.WORKSPACE, FindBarPlugin.ICON_SEARCH_WORKSPACE);

		resetHistoryCounters();
	}

	public Composite createFindBarComposite(Composite parent)
	{
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		editorContent = new Composite(composite, SWT.NONE);
		editorContent.setLayout(new FillLayout());
		editorContent.setLayoutData(createdDefaultGridData(SWT.FILL, SWT.FILL, true, true));

		return editorContent;
	}

	public void createFindBar(ISourceViewer sourceViewer)
	{
		this.sourceViewer = sourceViewer;
		findBarFinder = new FindBarFinder(textEditor, sourceViewer, this);

		sashSeparator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		sashSeparator.setLayoutData(separatorGridData = GridDataFactory.fillDefaults().grab(true, false).exclude(true)
				.create());

		sash = new Sash(composite, SWT.HORIZONTAL | SWT.SMOOTH);
		sash.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				Rectangle rect = sash.getParent().getClientArea();
				event.y = Math.min(Math.max(event.y, minimumFindBarHeight), rect.height - minimumFindBarHeight);
				if (event.detail != SWT.DRAG)
				{
					sash.setBounds(event.x, event.y, event.width, event.height);
					layout();
				}
			}
		});

		composite.addControlListener(new ControlAdapter()
		{
			public void controlResized(ControlEvent event)
			{
				// When the parent composite is resized, layout the find bar
				findBar.getParent().layout(new Control[] { findBar });
			}
		});

		sashGridData = createdDefaultGridData(SWT.FILL, SWT.BEGINNING, true, false);
		sashGridData.heightHint = 3;
		sashGridData.exclude = true;
		sash.setLayoutData(sashGridData);

		findBar = new Composite(composite, SWT.NONE);
		findBarGridData = createdDefaultGridData(SWT.FILL, SWT.BEGINNING, true, false);
		findBarGridData.exclude = true;
		findBar.setLayoutData(findBarGridData);

		textFind = createText(FindBarEntriesHelper.PREFERENCE_NAME_FIND);
		textFind.setText(Messages.FindBarDecorator_Find_initial_text);
		textFind.setEnabled(false);
		textFind.setForeground(DISABLED_COLOR);
		textFind.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				findButton.setEnabled(!isTextFindEmpty());
				if (countMatches.getSelection())
				{
					FindBarDecorator.findBarConfiguration.toggle(countMatchesOption.preferencesKey);
					countMatches.setText(StringUtil.EMPTY);
					countMatches.getParent().getParent().layout(new Control[] { countMatches.getParent() });
				}
			}
		});
		textFind.addTraverseListener(new TraverseListener()
		{

			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				{
					if (replaceAll.isEnabled())
					{
						replaceAll.setFocus();
					}
					else
					{
						textReplace.setFocus();
					}
					e.doit = false;
				}
			}
		});
		findBarTabOrder.add(textFind);

		ToolBar findToolbar = new ToolBar(findBar, SWT.FLAT);
		findToolbar.setLayoutData(createdDefaultGridData(SWT.LEFT, SWT.BEGINNING, false, false));

		findHistory = createHistoryToolItem(findToolbar, FindBarEntriesHelper.PREFERENCE_NAME_FIND);

		textReplace = createText(FindBarEntriesHelper.PREFERENCE_NAME_REPLACE);
		textReplace.setText(Messages.FindBarDecorator_Replace_initial_text);
		textReplace.setForeground(DISABLED_COLOR);
		findBarTabOrder.add(textReplace);

		ToolBar replaceToolbar = new ToolBar(findBar, SWT.FLAT);
		replaceToolbar.setLayoutData(createdDefaultGridData(SWT.LEFT, SWT.BEGINNING, false, false));
		replaceHistory = createHistoryToolItem(replaceToolbar, FindBarEntriesHelper.PREFERENCE_NAME_REPLACE);

		Composite searchComposite = new Composite(findBar, SWT.NONE);
		searchComposite.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
		searchComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
				.grab(false, true).create());
		findBarTabOrder.add(searchComposite);

		Composite findButtonComposite = new Composite(searchComposite, SWT.NONE);
		findButtonComposite.setLayout(GridLayoutFactory.fillDefaults()
				.spacing(Platform.OS_MACOSX.equals(Platform.getOS()) ? 0 : 5, 0).numColumns(4).create());
		findButtonComposite.setLayoutData(GridDataFactory.fillDefaults()
				.indent(0, Platform.OS_MACOSX.equals(Platform.getOS()) ? -4 : 0).create());

		findButton = createButton(findButtonComposite, null, true);
		findButton.setText(Messages.FindBarDecorator_LABEL_Find);

		replaceFind = createButton(findButtonComposite, null, true);
		replaceFind.setText(Messages.FindBarDecorator_LABEL_ReplaceFind);

		replace = createButton(findButtonComposite, null, true);
		replace.setText(Messages.FindBarDecorator_LABEL_Replace);

		replaceAll = createButton(findButtonComposite, null, true);
		replaceAll.setText(Messages.FindBarDecorator_LABEL_ReplaceAll);

		ToolBar optionsToolBar = new ToolBar(searchComposite, SWT.RIGHT | SWT.FLAT);
		optionsToolBar.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
				.indent(Platform.OS_MACOSX.equals(Platform.getOS()) ? 5 : 0, 0).create());

		for (FindBarOption option : fFindBarOptions)
		{
			option.createToolItem(optionsToolBar);
		}

		new ToolItem(optionsToolBar, SWT.SEPARATOR);

		scopeToolItem = createScopeToolItem(optionsToolBar);

		countMatchesOption.createToolItem(optionsToolBar);

		searchComposite.setTabList(new Control[] { findButtonComposite });

		findBarTabOrder.add(textFind);
		findBarTabOrder.add(textReplace);

		close = createLabel(findBar, CLOSE, true, CLOSE_ENTER);
		close.setToolTipText(Messages.FindBarDecorator_TOOLTIP_HideFindBar);
		close.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING).create());

		disableWhenHidden = new Control[] { textFind, textReplace, optionsToolBar, close, findButton, replaceFind,
				replace, replaceAll };

		GridLayout findBarLayout = GridLayoutFactory.swtDefaults().margins(2, 0).spacing(4, 0).numColumns(6).create();
		findBarLayout.marginBottom = 2;
		findBar.setLayout(findBarLayout);
		findBar.setTabList(findBarTabOrder.toArray(new Control[findBarTabOrder.size()]));

		minimumFindBarHeight = findBar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + findBarLayout.marginBottom;
		findBarVisibilityControl.register(this);
	}

	private void findBarFocusLost()
	{
		// When the find bar focus is lost, update the search selection
		searchSelection.setSelection(false);
		updateSearchSelection();
	}

	/**
	 * @return
	 */
	private ToolItem createScopeToolItem(ToolBar parent)
	{
		findScope = FindScope.CURRENT_FILE;
		ToolItem toolItem = new ToolItem(parent, SWT.DROP_DOWN);
		toolItem.setText(Messages.FindBarDecorator_LABEL_Scope);
		toolItem.setToolTipText(MessageFormat.format(Messages.FindBarDecorator_TOOLTIP_Scope_menu_item,
				findScope.toString()));
		toolItem.setImage(FindBarPlugin.getImage(FindBarPlugin.ICON_SEARCH_CURRENT_FILE));
		toolItem.addSelectionListener(new SelectionAdapter()
		{
			Menu menu = null;

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ToolItem toolItem = (ToolItem) e.widget;
				ToolBar toolbar = toolItem.getParent();
				Rectangle bounds = toolItem.getBounds();
				Point point = toolbar.toDisplay(new Point(bounds.x, bounds.y + bounds.height));

				if (menu == null)
				{
					menu = new Menu(UIUtils.getActiveShell(),
							(toolbar.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT)) | SWT.POP_UP);

					Map<String, List<TriggerSequence>> commandToBindings = findBarActions.getCommandToBindings();

					for (final FindScope scope : scopeMap.keySet())
					{
						String commandId = StringUtil.EMPTY;
						String trigger = StringUtil.EMPTY;
						switch (scope)
						{
							case CURRENT_FILE:
								commandId = FindBarActions.SEARCH_IN_CURRENT_FILE_COMMAND_ID;
								break;
							case OPEN_FILES:
								commandId = FindBarActions.SEARCH_IN_OPEN_FILES_COMMAND_ID;
								break;
							case ENCLOSING_PROJECT:
								commandId = FindBarActions.SEARCH_IN_ENCLOSING_PROJECT_COMMAND_ID;
								break;
							case WORKSPACE:
								commandId = FindBarActions.SEARCH_IN_WORKSPACE_COMMAND_ID;
								break;
						}

						List<TriggerSequence> triggers = commandToBindings.get(commandId);
						if (!CollectionsUtil.isEmpty(triggers))
						{
							trigger = triggers.get(0).toString();
						}
						MenuItem menuItem = new MenuItem(menu, SWT.NONE);
						menuItem.setText(StringUtil.isEmpty(trigger) ? scope.toString() : MessageFormat.format(
								Messages.FindBarDecorator_LABEL_Scope_Shortcut, scope.toString(), trigger));
						menuItem.setImage(FindBarPlugin.getImage(scopeMap.get(scope)));
						menuItem.addSelectionListener(new SelectionAdapter()
						{
							public void widgetSelected(SelectionEvent e)
							{
								updateSearchScope(scope);
							};
						});
					}
				}
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);
			}
		});

		return toolItem;
	}

	void updateSearchScope(FindScope scope)
	{
		findScope = scope;
		scopeToolItem.setImage(FindBarPlugin.getImage(scopeMap.get(findScope)));
		scopeToolItem.setToolTipText(MessageFormat.format(Messages.FindBarDecorator_TOOLTIP_Scope_menu_item,
				findScope.toString()));

		boolean isCurrentFile = findScope == FindScope.CURRENT_FILE;
		Control[] controls = new Control[] { textReplace, replace, replaceAll, replaceFind };

		for (Control control : controls)
		{
			control.setEnabled(isCurrentFile);
		}
		textReplace.setBackground(isCurrentFile ? null : UIUtils.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		replaceHistory.setEnabled(isCurrentFile);
		searchSelection.setEnabled(isCurrentFile);
		countMatches.setEnabled(isCurrentFile);
	}

	private GridData createdDefaultGridData(int horizontalAlignment, int verticalAlignment,
			boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace)
	{
		return new GridData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace);
	}

	private GridData createdDefaultGridData()
	{
		return createdDefaultGridData(SWT.LEFT, SWT.CENTER, false, false);
	}

	/**
	 * Create a default button (find, replace, replace/find, replace all).
	 */
	private Button createButton(Composite parent, String image, boolean enabled)
	{
		GridData layoutData = createdDefaultGridData();
		Button button = new Button(parent, SWT.PUSH);
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

	private ToolItem createHistoryToolItem(ToolBar toolbar, final String preferenceName)
	{
		ToolItem historyToolItem = new ToolItem(toolbar, SWT.DROP_DOWN);
		historyToolItem.setImage(FindBarPlugin.getImage(FindBarPlugin.ICON_SEARCH_HISTORY));
		historyToolItem.setToolTipText(Messages.FindBarDecorator_TOOLTIP_History);

		historyToolItem.addSelectionListener(new SelectionAdapter()
		{
			Menu menu = null;

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ToolItem toolItem = (ToolItem) e.widget;
				menu = createHistoryMenu(toolItem, preferenceName, menu);
			}

		});

		return historyToolItem;
	}

	private Menu createHistoryMenu(ToolItem toolItem, final String preferenceName, Menu menu)
	{
		ToolBar toolbar = toolItem.getParent();
		Rectangle bounds = toolItem.getBounds();
		Point point = toolbar.toDisplay(new Point(bounds.x, bounds.y + bounds.height));

		List<String> loadEntries = findBarEntriesHelper.loadEntries(preferenceName);
		if (menu != null && !menu.isDisposed())
		{
			menu.dispose();
		}

		menu = new Menu(UIUtils.getActiveShell(), (toolbar.getStyle() & (SWT.RIGHT_TO_LEFT | SWT.LEFT_TO_RIGHT))
				| SWT.POP_UP);

		if (!CollectionsUtil.isEmpty(loadEntries))
		{
			int i = 0;
			for (final String item : loadEntries)
			{
				final MenuItem menuItem = new MenuItem(menu, SWT.NONE);
				menuItem.setData(Integer.valueOf(i));
				menuItem.setText(StringUtil.truncate(NEWLINE_SANITIZER.searchAndReplace(item), 30));
				menuItem.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						if (FindBarEntriesHelper.PREFERENCE_NAME_REPLACE.equals(preferenceName))
						{
							lastReplaceHistory = (Integer) menuItem.getData();
						}
						else
						{
							lastFindHistory = (Integer) menuItem.getData();
						}
						updateTextAfterHistorySelection(item, preferenceName);
					};
				});
				i++;
			}
		}
		else
		{
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(Messages.FindBarDecorator_LABEL_No_History);
		}

		menu.setLocation(point.x, point.y);
		menu.setVisible(true);
		return menu;
	}

	private void insertHistory(String preferenceName, boolean isNext)
	{
		List<String> loadEntries = findBarEntriesHelper.loadEntries(preferenceName);
		boolean isReplace = FindBarEntriesHelper.PREFERENCE_NAME_REPLACE.equals(preferenceName);
		int currentIndex = isReplace ? lastReplaceHistory : lastFindHistory;
		currentIndex = currentIndex + (isNext ? -1 : 1);
		if (currentIndex >= loadEntries.size())
		{
			currentIndex = 0;
		}
		else if (currentIndex < 0)
		{
			currentIndex = loadEntries.size() - 1;
		}

		String text = loadEntries.get(currentIndex);
		updateTextAfterHistorySelection(text, preferenceName);

		if (isReplace)
		{
			lastReplaceHistory = currentIndex;
		}
		else
		{
			lastFindHistory = currentIndex;
		}
	}

	private void updateTextAfterHistorySelection(String text, String preferenceName)
	{
		Text textBox = FindBarEntriesHelper.PREFERENCE_NAME_REPLACE.equals(preferenceName) ? textReplace : textFind;
		textBox.setForeground(null);
		textBox.setText(text);
		textBox.setSelection(0, text.length());
	}

	/**
	 * Create a label (with a different image when the mouse is over). When it's clicked, use the default handler to
	 * treat the action.
	 */
	private Label createLabel(Composite parent, String image, boolean enabled, String imageEntered)
	{
		GridData layoutData = createdDefaultGridData();
		final Label label = new Label(parent, SWT.CENTER);
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
	private Text createText(String preferenceName)
	{
		final Text text = new Text(findBar, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData gd = createdDefaultGridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = text.getLineHeight() * 3 + 3;
		gd.widthHint = 250;
		text.setLayoutData(gd);

		entriesControlHandles.add(findBarEntriesHelper.register(text, modifyListener, preferenceName));

		text.addFocusListener(findBarActions.createFocusListener(text));
		text.addKeyListener(textKeyListner);
		return text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.findbar.api.IFindBarDecorator#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		String selectedFindText = null;
		if (visible)
		{
			// Note: we have to get the text selection at this point (not after calling
			// findBarVisibilityControl.setVisible,
			// as the focus may have change
			Control focusControl = Display.getCurrent().getFocusControl();
			boolean wasExcluded = findBarGridData.exclude;
			if (wasExcluded || focusControl instanceof StyledText)
			{
				// Only change the text if it is not activated (otherwise it means it was
				// already activated and the user was in another control in the find bar and used Ctrl+F, in which case
				// we don't want to change it -- other cases mean that Ctrl+F was used from the editor or somewhere
				// else, which means we have to update it).
				ISelection selection = sourceViewer.getSelectionProvider().getSelection();
				if (selection instanceof ITextSelection)
				{
					ITextSelection textSelection = (ITextSelection) selection;
					String text = textSelection.getText();
					if (!StringUtil.isEmpty(text))
					{
						selectedFindText = text;
					}
				}
			}
		}

		findBarVisibilityControl.setVisible(visible, this, selectedFindText);
		if (visible)
		{
			textFind.setFocus();
			String text = textFind.getText();
			if (!StringUtil.isEmpty(text))
			{
				textFind.setSelection(0, text.length());
			}
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

	private IFindReplaceTarget fFindReplaceTarget;

	private CopiedFromFindReplaceDialog fFindReplaceDialog;

	/**
	 * Do searches when we're modifying the text in the combo -- i.e.: incremental search.
	 */
	private final class SearchOnTextChangedModifyListener implements ModifyListener, KeyListener, IStartEndIgnore
	{
		private String lastText = StringUtil.EMPTY;

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

			textFind.setBackground(null);
			boolean wrap = true;
			String text = textFind.getText();
			if (lastText.startsWith(text))
			{
				wrap = false;
			}
			lastText = text;
			if (StringUtil.EMPTY.equals(text))
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
		findBarVisibilityControl.unregister(this);
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

		if (sourceViewer.getTextWidget() != null && !sourceViewer.getTextWidget().isDisposed())
		{
			sourceViewer.getTextWidget().removeFocusListener(textViewerFocusListener);
		}
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
			setVisible(false);
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
		else
		{
			FindBarPlugin.log(new RuntimeException("Unhandled selection for widget: " + source)); //$NON-NLS-1$
		}
	}

	private void findNextOrprevAfterChangeOption()
	{
		if (isTextFindValid())
		{
			setFindText(textFind.getText());
			findBarFinder.find(!getConfiguration().getSearchBackward(), true);
			showCountTotal();
		}
	}

	private boolean isTextFindValid()
	{
		return !DISABLED_COLOR.equals(textFind.getForeground()) && !isTextFindEmpty();
	}

	private boolean isTextFindEmpty()
	{
		String findText = textFind.getText();
		return findText == null || findText.length() == 0;
	}

	private IFindReplaceTarget getFindReplaceTarget()
	{
		if (fFindReplaceTarget == null)
		{
			fFindReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		}
		return fFindReplaceTarget;
	}

	private CopiedFromFindReplaceDialog getFindReplaceDialog()
	{
		if (fFindReplaceDialog == null)
		{
			fFindReplaceDialog = new CopiedFromFindReplaceDialog(getFindReplaceTarget(), statusLineManager);
		}
		return fFindReplaceDialog;
	}

	private void replace(boolean newFind)
	{
		ISelectionProvider selectionProvider = this.textEditor.getSelectionProvider();
		ISelection selection = selectionProvider.getSelection();
		if (!(selection instanceof ITextSelection))
		{
			FindBarPlugin.log(new AssertionError("Expected text editor selection to be an ITextSelection. Was: " //$NON-NLS-1$
					+ selection));
			return;
		}
		ITextSelection textSelection = (ITextSelection) selection;
		String comboText = textFind.getText();
		if (comboText.length() == 0)
		{
			return;
		}
		setFindText(comboText);
		setFindTextOnReplace(getReplaceText());

		selectionProvider.setSelection(new TextSelection(this.textEditor.getDocumentProvider().getDocument(
				this.textEditor.getEditorInput()), textSelection.getOffset(), 0));

		// Do initial search before replace (always forward search as we just selected the initial offset).
		if (!findBarFinder.find(true, false, false))
		{
			return; // The messages (why the find didn't work) should be set already.
		}
		try
		{
			getFindReplaceDialog().replaceSelection(getReplaceText(), getConfiguration().getRegularExpression());
			showCountTotal();
		}
		catch (Exception e1)
		{
			statusLineManager.setMessage(true,
					MessageFormat.format(Messages.FindBarDecorator_ReplaceError, e1.getMessage()), null);
			FindBarPlugin.log(e1);
			return;
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
			statusLineManager.setMessage(false, StringUtil.EMPTY, null);
		}
	}

	private void replaceAll()
	{
		setFindText(textFind.getText());
		setFindTextOnReplace(getReplaceText());
		try
		{
			int replaced = getFindReplaceDialog().replaceAll(textFind.getText(), getReplaceText(), true,
					getConfiguration().getCaseSensitive(), getWholeWord(), getConfiguration().getRegularExpression());
			showCountTotal();
			statusLineManager.setMessage(false, String.format(Messages.FindBarDecorator_MSG_Replaced, replaced), null);
		}
		catch (PatternSyntaxException e1)
		{
			statusLineManager.setMessage(true, e1.getMessage(), null);
		}
	}

	boolean isFindTextActive()
	{
		return isVisible() && (textFind.getDisplay().getFocusControl() == textFind);
	}

	boolean isReplaceTextActive()
	{
		return isVisible() && (textReplace.getDisplay().getFocusControl() == textReplace);
	}

	private void adjustEnablement()
	{
		String text = textFind.getText();
		boolean isTextFindValue = isTextFindValid();
		findButton.setEnabled(isTextFindValue);
		replace.setEnabled(isTextFindValue);
		replaceFind.setEnabled(isTextFindValue);
		replaceAll.setEnabled(isTextFindValue);
		wholeWord.setEnabled(!StringUtil.EMPTY.equals(text) && !getConfiguration().getRegularExpression()
				&& isWord(text));
	}

	/**
	 * Note: this method should NEVER be called directly. Always use setVisible (the visibility control is the only
	 * place that should reference this method).
	 * 
	 * @param updateFocus
	 *            indicates if the focus should be given to the editor
	 */
	/* default */void hideFindBar(boolean updateFocus)
	{
		if (findBarGridData.exclude == false)
		{
			sashGridData.exclude = true;
			separatorGridData.exclude = true;
			findBarGridData.exclude = true;
			composite.layout();
			findBarFinder.resetIncrementalOffset();
			findBarFinder.resetScope();
			removeComboSearchOnTextChangeListener();
			statusLineManager.setMessage(false, StringUtil.EMPTY, null);
		}
		if (updateFocus)
		{
			textEditor.setFocus();
		}
		if (disableWhenHidden != null)
		{
			for (Control w : disableWhenHidden)
			{
				w.setEnabled(false);
			}
		}

		sourceViewer.getTextWidget().removeFocusListener(textViewerFocusListener);
	}

	public void addComboSearchOnTextChangeListener()
	{
		textFind.addKeyListener(modifyListener);
		textFind.addModifyListener(modifyListener);
	}

	public void removeComboSearchOnTextChangeListener()
	{
		textFind.removeKeyListener(modifyListener);
		textFind.removeModifyListener(modifyListener);
	}

	/**
	 * Note: this method should NEVER be called directly. Always use setVisible (the visibility control is the only
	 * place that should reference this method).
	 * 
	 * @param updateFocus
	 *            indicates if the focus should be given to the combo
	 */
	/* default */void showFindBar(boolean updateFocus)
	{
		if (disableWhenHidden != null)
		{
			for (Control w : disableWhenHidden)
			{
				if (w != null && !w.isDisposed())
				{
					w.setEnabled(true);
				}
			}
		}

		boolean wasExcluded = findBarGridData.exclude;
		if (findBarGridData.exclude)
		{
			separatorGridData.exclude = false;
			sashGridData.exclude = false;
			findBarGridData.exclude = false;
			composite.layout();
		}
		if (wasExcluded)
		{
			textFind.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					addComboSearchOnTextChangeListener();
				}
			});
		}
		adjustEnablement();
		if (!textFind.isFocusControl())
		{
			textFind.setFocus();
			findBarFinder.resetIncrementalOffset();
		}
		sourceViewer.getTextWidget().addFocusListener(textViewerFocusListener);
	}

	/* default */void findPrevious()
	{
		if (findScope == FindScope.CURRENT_FILE && isTextFindValid())
		{
			setFindText(textFind.getText());
			incrementCountPosition(false);
			findBarFinder.find(false);
			updateLastCountPosition();
		}
	}

	/* default */void findNext()
	{
		if (findScope == FindScope.CURRENT_FILE && isTextFindValid())
		{
			setFindText(textFind.getText());
			incrementCountPosition(true);
			findBarFinder.find(true);
			updateLastCountPosition();
		}
	}

	/* default */void findNextOrPrev()
	{
		if (findScope == FindScope.CURRENT_FILE && isTextFindValid())
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
		else
		{
			searchInScope();
		}
	}

	boolean getWholeWord()
	{
		return wholeWord.getEnabled() && getConfiguration().getWholeWord()
				&& !getConfiguration().getRegularExpression();
	}

	void setFindText(String findText)
	{
		setFindText(findText, textFind, FindBarEntriesHelper.PREFERENCE_NAME_FIND);
	}

	private void setFindTextOnReplace(String findText)
	{
		setFindText(findText, textReplace, FindBarEntriesHelper.PREFERENCE_NAME_REPLACE);
	}

	private void setFindText(String findText, final Text text, String preferenceName)
	{
		findBarEntriesHelper.addEntry(findText, preferenceName);
		resetHistoryCounters();
	}

	private void resetHistoryCounters()
	{
		lastFindHistory = -1;
		lastReplaceHistory = -1;
	}

	private static final int TOO_MANY = Integer.getInteger(FindBarDecorator.class.getName() + ".TOO_MANY", 100); //$NON-NLS-1$

	private Label sashSeparator;

	private FindBarOption countMatchesOption;

	void showCountTotal()
	{
		if (!countMatches.getSelection())
		{
			countMatches.setText(StringUtil.EMPTY);
			return;
		}

		lastCountOffset = sourceViewer.getTextWidget().getCaretOffset();

		int currentCount = 0;
		int total = 0;
		if (isTextFindValid())
		{
			String text = sourceViewer.getDocument().get();
			Pattern pattern = createFindPattern();
			IRegion scope = findBarFinder.getScope();

			// Truncate the search string if there is a scope set
			if (scope != null)
			{
				text = text.substring(scope.getOffset(), scope.getOffset() + scope.getLength());
			}
			else
			{
				scope = new Region(0, text.length());
			}

			Matcher matcher = pattern.matcher(text);

			if (matcher.find(0))
			{
				total = 1;

				FindBarConfiguration configuration = getConfiguration();
				if ((!configuration.getSearchBackward() && lastCountOffset > (matcher.start() + scope.getOffset()))
						|| (configuration.getSearchBackward() && lastCountOffset >= (matcher.start() + scope
								.getOffset())))
				{
					currentCount = total;
				}
				while (matcher.find())
				{
					++total;

					if ((!configuration.getSearchBackward() && lastCountOffset > (matcher.start() + scope.getOffset()))
							|| (configuration.getSearchBackward() && lastCountOffset >= (matcher.start() + scope
									.getOffset())))
					{
						currentCount = total;
					}

					if ((TOO_MANY != -1) && total > TOO_MANY)
					{
						break;
					}
				}
			}

			if (currentCount > total)
			{
				currentCount = currentCount - total;
			}
		}
		if ((TOO_MANY != -1) && total > TOO_MANY)
		{
			countMatches.setText("> " + TOO_MANY); //$NON-NLS-1$
			lastCountPosition = -1;
		}
		else
		{
			countMatches.setText(MessageFormat.format(Messages.FindBarDecorator_LABEL_Count_Match,
					String.valueOf(currentCount), String.valueOf(total)));
			lastCountPosition = currentCount;
		}
		lastCountTotal = total;

		findBar.layout(true, true);
	}

	Pattern createFindPattern()
	{
		String originalPattern = textFind.getText();
		String convertedPattern = convertTextString(originalPattern);

		// If the pattern was converted, then it should be run as a regular expression
		boolean isRegEx = getConfiguration().getRegularExpression()
				|| ObjectUtil.areNotEqual(originalPattern, convertedPattern);
		boolean patternStringIsAWord = isWord(convertedPattern);

		int flags = 0;
		if (!getConfiguration().getCaseSensitive())
		{
			flags |= Pattern.CASE_INSENSITIVE;
		}
		if (!isRegEx)
		{
			convertedPattern = Pattern.quote(convertedPattern);
		}
		if (patternStringIsAWord && getWholeWord())
		{
			convertedPattern = REGEX_WORD_BOUNDARY + convertedPattern + REGEX_WORD_BOUNDARY;
		}
		return Pattern.compile(convertedPattern, flags);
	}

	void incrementCountPosition(boolean isForward)
	{
		if (countMatches.getSelection())
		{
			if (lastCountOffset == sourceViewer.getTextWidget().getCaretOffset())
			{
				if ((TOO_MANY != -1) && lastCountTotal > TOO_MANY)
				{
					return;
				}

				if (isForward)
				{
					lastCountPosition++;
					if (lastCountPosition > lastCountTotal)
					{
						lastCountPosition = 1;
					}
				}
				else
				{
					lastCountPosition--;
					if (lastCountPosition < 1)
					{
						lastCountPosition = lastCountTotal;
					}
				}
				countMatches.setText(MessageFormat.format(Messages.FindBarDecorator_LABEL_Count_Match,
						String.valueOf(lastCountPosition), String.valueOf(lastCountTotal)));
			}
			else
			{
				showCountTotal();
			}
			countMatches.getParent().getParent().layout(new Control[] { countMatches.getParent() });
		}
	}

	private void updateLastCountPosition()
	{
		if (countMatches.getSelection())
		{
			lastCountOffset = sourceViewer.getTextWidget().getCaretOffset();
		}
	}

	void updateSearchSelection()
	{
		findBarFinder.resetScope();
		if (searchSelection.getSelection())
		{
			if (!textReplace.isFocusControl())
			{
				textFind.setFocus();
			}
			findBarFinder.enableScope(null);
		}
		if (countMatches.getSelection())
		{
			showCountTotal();
		}
	}

	void showOptions(boolean useMousePos)
	{
		Menu menu = new Menu(UIUtils.getActiveShell(), SWT.POP_UP);

		for (FindBarOption option : fFindBarOptions)
		{
			option.createMenuItem(menu);
		}

		Point location;
		if (useMousePos)
		{
			Display current = UIUtils.getDisplay();
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

	void searchInScope()
	{
		String searchText = textFind.getText();
		if (searchText.length() >= 0)
		{
			String convertedText = convertTextString(searchText);
			boolean isWholeWord = getConfiguration().getWholeWord();
			boolean isRegEx = getConfiguration().getRegularExpression()
					|| ObjectUtil.areNotEqual(searchText, convertedText);
			boolean isCaseSensitive = getConfiguration().getCaseSensitive();
			if (isWholeWord && !isRegEx && isWord(convertedText))
			{
				isRegEx = true;
				convertedText = REGEX_WORD_BOUNDARY + convertedText + REGEX_WORD_BOUNDARY;
			}

			IStatusLineManager statusLineManager = (IStatusLineManager) textEditor.getAdapter(IStatusLineManager.class);
			switch (findScope)
			{
				case OPEN_FILES:
					FindHelper.findInOpenDocuments(convertedText, isCaseSensitive, isWholeWord, isRegEx,
							statusLineManager);
					break;
				case ENCLOSING_PROJECT:
					FindHelper.findInEnclosingProject(convertedText, isCaseSensitive, isWholeWord, isRegEx,
							statusLineManager);
					break;
				case WORKSPACE:
					FindHelper.findInWorkspace(convertedText, isCaseSensitive, isWholeWord, isRegEx, statusLineManager);
					break;
			}
		}

	}

	String convertTextString(String textString)
	{
		// Only convert the text if it's not a regular expression. If it is then use the string as-is
		if (!getConfiguration().getRegularExpression())
		{
			StringBuilder sb = new StringBuilder();
			int startIndex = 0;
			Matcher matcher = StringUtil.LINE_SPLITTER.matcher(textString);

			while (matcher.find())
			{
				// Indicate the strings between the newline characters should be treated as literals
				sb.append(REGEX_LITERAL_START);
				sb.append(textString.substring(startIndex, matcher.start()));
				sb.append(REGEX_LITERAL_END);
				sb.append(StringUtil.REGEX_NEWLINE_GROUP);
				startIndex = matcher.end();
			}

			if (startIndex == 0)
			{
				sb.append(textString);
			}
			else if (startIndex < textString.length())
			{
				sb.append(REGEX_LITERAL_START);
				sb.append(textString.substring(startIndex));
				sb.append(REGEX_LITERAL_END);
			}

			return sb.toString();
		}

		return textString;
	}

	void showFindReplaceDialog()
	{
		// It's important that the combo has the focus.
		// Doing the find (Ctrl+F) anywhere will put the focus on the combo, but if the combo
		// has the focus, it'll show the default find dialog.
		// @see: com.aptana.editor.findbar.impl.ShowFindBarAction
		textFind.setFocus();
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

	public static void updateFromEclipseFindSettings()
	{
		eclipseFindSettings.readConfiguration();

		findBarConfiguration.updateFromEclipseFindSettings();
		findBarEntriesHelper.updateFromEclipseFindSettings();
	}

	public FindBarConfiguration getConfiguration()
	{
		return findBarConfiguration;
	}

	void layout()
	{
		Rectangle clientRect = composite.getClientArea();
		Rectangle hSashBounds = sash.getBounds();
		Rectangle separatorBounds = sashSeparator.getBounds();

		editorContent.setBounds(0, 0, clientRect.width, hSashBounds.y - separatorBounds.height);
		sashSeparator.setBounds(0, hSashBounds.y - separatorBounds.height, clientRect.width, separatorBounds.height);
		findBar.setBounds(0, hSashBounds.y + hSashBounds.height, clientRect.width, clientRect.height
				- (hSashBounds.y + hSashBounds.height));

		// Set the height hint so that upon resize the previous height is honored
		findBarGridData.heightHint = clientRect.height - (hSashBounds.y + hSashBounds.height);

	}

	String getLineDelimiter(IDocument document)
	{
		InstanceScope scope = EclipseUtil.instanceScope();
		IEclipsePreferences node = scope.getNode(Platform.PI_RUNTIME);
		String separator = node.get(Platform.PREF_LINE_SEPARATOR, StringUtil.EMPTY);
		if (StringUtil.isEmpty(separator))
		{
			separator = TextUtilities.getDefaultLineDelimiter(document);
		}

		return separator;
	}

	String getReplaceText()
	{
		return textReplace == null || textReplace.isDisposed() || DISABLED_COLOR.equals(textReplace.getForeground()) ? StringUtil.EMPTY
				: textReplace.getText();
	}

	void inputNewline(Text text)
	{
		StringBuilder sb = new StringBuilder(text.getText());
		Point selection = text.getSelection();
		String delimiter = Text.DELIMITER;
		sb.replace(selection.x, selection.y, delimiter);
		text.setText(sb.toString());
		text.setSelection(selection.x + delimiter.length());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.findbar.api.IFindBarDecorator#activateContexts(java.lang.String[])
	 */
	public void activateContexts(String[] contextIds)
	{
		findBarActions.activateContexts(contextIds);
	}
}
