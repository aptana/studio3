/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.preferences;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.themes.ThemeElementHelper;
import org.eclipse.ui.internal.tweaklets.PreferencePageEnhancer;
import org.eclipse.ui.internal.tweaklets.Tweaklets;
import org.eclipse.ui.themes.ITheme;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.DelayedTextAttribute;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.RGBa;
import com.aptana.theme.TextmateImporter;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemeExporter;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.ThemeRule;

@SuppressWarnings("restriction")
public class ThemePreferencePage extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener,
		IInputValidator, IPropertyChangeListener
{

	/**
	 * ID of the pref page to use when opening/referring to it programmatically.
	 */
	public static final String ID = "com.aptana.theme.preferencePage"; //$NON-NLS-1$

	/**
	 * Key to store the dialog settings for the initial directory to open when importing themes (saves last directory).
	 */
	private static final String THEME_DIRECTORY = "themeDirectory"; //$NON-NLS-1$

	/**
	 * Key to store the dialog settings for the initial directory to open when exporting themes (saves last directory).
	 */
	private static final String THEME_EXPORT_DIRECTORY = "themeExportDirectory"; //$NON-NLS-1$

	/**
	 * The list of "standard" token types to set up for a theme.
	 */
	private static List<String> tokenTypeNames = new ArrayList<String>();
	static
	{
		tokenTypeNames.add("comment"); //$NON-NLS-1$
		tokenTypeNames.add("comment.block"); //$NON-NLS-1$
		tokenTypeNames.add("comment.documentation"); //$NON-NLS-1$
		tokenTypeNames.add("comment.line"); //$NON-NLS-1$
		tokenTypeNames.add("constant"); //$NON-NLS-1$
		tokenTypeNames.add("constant.character"); //$NON-NLS-1$
		tokenTypeNames.add("constant.language"); //$NON-NLS-1$
		tokenTypeNames.add("constant.numeric"); //$NON-NLS-1$
		tokenTypeNames.add("constant.other"); //$NON-NLS-1$
		tokenTypeNames.add(ConsoleThemer.CONSOLE_ERROR);
		tokenTypeNames.add(ConsoleThemer.CONSOLE_INPUT);
		tokenTypeNames.add(ConsoleThemer.CONSOLE_OUTPUT);
		tokenTypeNames.add(ConsoleThemer.CONSOLE_PROMPT);
		tokenTypeNames.add(ConsoleThemer.CONSOLE_WARNING);
		tokenTypeNames.add("entity.name"); //$NON-NLS-1$
		tokenTypeNames.add("entity.name.class"); //$NON-NLS-1$
		tokenTypeNames.add("entity.name.function"); //$NON-NLS-1$
		tokenTypeNames.add("entity.name.tag"); //$NON-NLS-1$
		tokenTypeNames.add("entity.other"); //$NON-NLS-1$
		tokenTypeNames.add("entity.other.attribute-name"); //$NON-NLS-1$
		tokenTypeNames.add("entity.other.inherited-class"); //$NON-NLS-1$
		tokenTypeNames.add("invalid"); //$NON-NLS-1$
		tokenTypeNames.add("invalid.deprecated"); //$NON-NLS-1$
		tokenTypeNames.add("invalid.illegal"); //$NON-NLS-1$
		tokenTypeNames.add("keyword"); //$NON-NLS-1$
		tokenTypeNames.add("keyword.control"); //$NON-NLS-1$
		tokenTypeNames.add("keyword.operator"); //$NON-NLS-1$
		tokenTypeNames.add("keyword.other"); //$NON-NLS-1$
		tokenTypeNames.add("storage"); //$NON-NLS-1$
		tokenTypeNames.add("storage.modifier"); //$NON-NLS-1$
		tokenTypeNames.add("storage.other"); //$NON-NLS-1$
		tokenTypeNames.add("storage.type"); //$NON-NLS-1$
		tokenTypeNames.add("string"); //$NON-NLS-1$
		tokenTypeNames.add("string.interpolated"); //$NON-NLS-1$
		tokenTypeNames.add("string.other"); //$NON-NLS-1$
		tokenTypeNames.add("string.quoted"); //$NON-NLS-1$
		tokenTypeNames.add("string.regexp"); //$NON-NLS-1$
		tokenTypeNames.add("string.unquoted"); //$NON-NLS-1$
		tokenTypeNames.add("support"); //$NON-NLS-1$
		tokenTypeNames.add("support.class"); //$NON-NLS-1$
		tokenTypeNames.add("support.constant"); //$NON-NLS-1$
		tokenTypeNames.add("support.function"); //$NON-NLS-1$
		tokenTypeNames.add("support.other"); //$NON-NLS-1$
		tokenTypeNames.add("support.type"); //$NON-NLS-1$
		tokenTypeNames.add("variable"); //$NON-NLS-1$
		tokenTypeNames.add("variable.language"); //$NON-NLS-1$
		tokenTypeNames.add("variable.other"); //$NON-NLS-1$
		tokenTypeNames.add("variable.parameter"); //$NON-NLS-1$
	}

	private static final int ROW_HEIGHT = 20;

	protected Theme fSelectedTheme;

	private ColorSelector fgSelector;
	private ColorSelector bgSelector;
	private ColorSelector lineHighlightSelector;
	private ColorSelector selectionSelector;
	private ColorSelector caretSelector;
	private Combo fThemeCombo;
	private TableViewer tableViewer;
	private Set<TableEditor> fTableEditors;
	private Button renameThemeButton;
	private Button deleteThemeButton;
	private HashMap<Integer, Font> fFonts;

	private Button fAptanaEditorsOnlyCheckbox;
	private Button fAddThemeButton;
	private Button fImportButton;
	private Button fAddTokenButton;
	private Button fRemoveTokenButton;
	private Combo fScopeText;

	private Button fExportButton;

	private Font fFont;
	private Text fFontText;

	private boolean reorderingRules = false;

	private ControlDecoration fScopeSelectorDecoration;

	private ComboViewer e4ThemeIdCombo;
	private String defaultE4Theme;
	private IThemeEngine e4ThemeEngine;
	private org.eclipse.e4.ui.css.swt.theme.ITheme currentE4Theme;

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createE4ThemeCombo(composite);

		Group group = new Group(composite, SWT.SHADOW_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(Messages.ThemePreferencePage_EditorTheme);

		createThemeListControls(group);
		createGlobalColorControls(group);
		createTokenEditTable(group);
		createFontArea(group);
		createInvasivePrefArea(group);

		setTheme(getThemeManager().getCurrentTheme().getName());
		return composite;
	}

	private void createFontArea(Composite composite)
	{
		Composite themesComp = new Composite(composite, SWT.NONE);
		themesComp.setLayout(new GridLayout(3, false));
		themesComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label label = new Label(themesComp, SWT.NONE);
		label.setText(Messages.ThemePreferencePage_FontNameLabel);

		fFont = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);
		fFontText = new Text(themesComp, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		fFontText.setText(toString(fFont));
		fFontText.setFont(fFont);
		fFontText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button selectFontButton = new Button(themesComp, SWT.PUSH);
		selectFontButton.setText(Messages.ThemePreferencePage_SelectFontButtonLabel);
		selectFontButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final FontDialog fontDialog = new FontDialog(getShell());
				fontDialog.setFontList(fFont.getFontData());
				final FontData data = fontDialog.open();
				if (data != null)
				{
					setFont(new Font(fFont.getDevice(), fontDialog.getFontList()));
				}
			}
		});
	}

	private static String toString(Font font)
	{
		if (font == null || font.getFontData() == null || font.getFontData().length <= 0)
		{
			return StringUtil.EMPTY;
		}
		FontData data = font.getFontData()[0];
		return MessageFormat.format(Messages.ThemePreferencePage_FontName, data.getName(), data.getHeight());
	}

	private void createInvasivePrefArea(Composite composite)
	{
		Composite themesComp = new Composite(composite, SWT.NONE);
		themesComp.setLayout(new GridLayout(1, false));

		// Do we apply the colors to editors that aren't ours?
		fAptanaEditorsOnlyCheckbox = new Button(themesComp, SWT.CHECK);
		fAptanaEditorsOnlyCheckbox.setText(Messages.ThemePreferencePage_ApplyToAllEditors);
		fAptanaEditorsOnlyCheckbox.setSelection(ThemePlugin.applyToAllEditors());
		fAptanaEditorsOnlyCheckbox.addSelectionListener(this);
	}

	private void createE4ThemeCombo(Composite themesComp)
	{
		Composite comp = new Composite(themesComp, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		new Label(comp, SWT.NONE).setText(Messages.ThemePreferencePage_OverallTheme);

		e4ThemeIdCombo = new ComboViewer(comp, SWT.READ_ONLY);
		e4ThemeIdCombo.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return ((org.eclipse.e4.ui.css.swt.theme.ITheme) element).getLabel();
			}
		});
		e4ThemeIdCombo.setContentProvider(new ArrayContentProvider());
		e4ThemeIdCombo.setInput(e4ThemeEngine.getThemes());
		e4ThemeIdCombo.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.currentE4Theme = e4ThemeEngine.getActiveTheme();
		if (this.currentE4Theme != null)
		{
			e4ThemeIdCombo.setSelection(new StructuredSelection(currentE4Theme));
		}
		e4ThemeIdCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				org.eclipse.e4.ui.css.swt.theme.ITheme selection = getSelection();
				e4ThemeEngine.setTheme(selection, false);
				((PreferencePageEnhancer) Tweaklets.get(PreferencePageEnhancer.KEY)).setSelection(selection);
			}
		});
	}

	/** @return the currently selected theme or null if there are no themes */
	private org.eclipse.e4.ui.css.swt.theme.ITheme getSelection()
	{
		return (org.eclipse.e4.ui.css.swt.theme.ITheme) ((IStructuredSelection) e4ThemeIdCombo.getSelection())
				.getFirstElement();
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	private void createThemeListControls(Composite composite)
	{
		Composite themesComp = new Composite(composite, SWT.NONE);
		themesComp.setLayout(new GridLayout(6, false));

		fThemeCombo = new Combo(themesComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		loadThemeNames();
		fThemeCombo.addSelectionListener(this);

		fAddThemeButton = new Button(themesComp, SWT.PUSH | SWT.FLAT);
		fAddThemeButton.setText(Messages.ThemePreferencePage_AddTokenLabel);
		fAddThemeButton.addSelectionListener(this);

		renameThemeButton = new Button(themesComp, SWT.PUSH | SWT.FLAT);
		renameThemeButton.setText(CoreStrings.RENAME);
		renameThemeButton.addSelectionListener(this);

		deleteThemeButton = new Button(themesComp, SWT.PUSH | SWT.FLAT);
		deleteThemeButton.setText(Messages.ThemePreferencePage_RemoveTokenLabel);
		deleteThemeButton.addSelectionListener(this);

		// Textmate Import
		fImportButton = new Button(themesComp, SWT.PUSH | SWT.FLAT);
		fImportButton.setText(Messages.ThemePreferencePage_ImportLabel);
		fImportButton.addSelectionListener(this);

		fExportButton = new Button(themesComp, SWT.PUSH | SWT.FLAT);
		fExportButton.setText(Messages.ThemePreferencePage_ExportLabel);
		fExportButton.addSelectionListener(this);
	}

	private void loadThemeNames()
	{
		fThemeCombo.removeAll();
		List<String> themeNames = new ArrayList<String>(getThemeManager().getThemeNames());
		Collections.sort(themeNames, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});
		for (String themeName : themeNames)
		{
			fThemeCombo.add(themeName);
		}
	}

	private void createGlobalColorControls(Composite composite)
	{
		Composite colors = new Composite(composite, SWT.NONE);
		colors.setLayout(new GridLayout(4, false));

		// TODO Make the ColorSelector buttons be SWT.FLAT, and let them handle alpha values as Textmate does
		Label label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_ForegroundLabel);
		fgSelector = new ColorSelector(colors);
		fgSelector.addListener(this);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_SelectionLabel);
		selectionSelector = new ColorSelector(colors);
		selectionSelector.addListener(this);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_BackgroundLabel);
		bgSelector = new ColorSelector(colors);
		bgSelector.addListener(this);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_LineHighlightLabel);
		lineHighlightSelector = new ColorSelector(colors);
		lineHighlightSelector.addListener(this);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_CaretLabel);
		caretSelector = new ColorSelector(colors);
		caretSelector.addListener(this);
	}

	private void createTokenEditTable(Composite composite)
	{
		// FIXME allow drag and drop to sort items in the table!
		Composite comp = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 200;
		comp.setLayoutData(gridData);
		TableColumnLayout layout = new TableColumnLayout();
		comp.setLayout(layout);
		final Table table = new Table(comp, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		// Hack to force a specific row height
		table.addListener(SWT.MeasureItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				event.height = ROW_HEIGHT;
			}
		});
		// Hack to draw the underline in first column
		table.addListener(SWT.PaintItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.FOREGROUND) != 0 && event.index == 0)
				{
					TableItem item = (TableItem) event.item;
					ThemeRule token = (ThemeRule) item.getData();
					if ((token.getTextAttribute().style & TextAttribute.UNDERLINE) != 0)
					{
						int y = event.getBounds().y + event.getBounds().height - 6;
						int x2 = event.getBounds().width;
						Color oldFG = event.gc.getForeground();
						Color fg;
						RGBa rgb = token.getTextAttribute().foreground;
						if (rgb == null)
						{
							fg = ThemePlugin.getDefault().getColorManager().getColor(getTheme().getForeground());
						}
						else
						{
							fg = ThemePlugin.getDefault().getColorManager().getColor(rgb.toRGB());
						}
						event.gc.setForeground(fg);
						event.gc.drawLine(0, y, x2, y);
						event.gc.setForeground(oldFG);
						event.detail &= ~SWT.FOREGROUND;
					}
				}
			}
		});

		Listener selectionOverride = new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Scrollable scrollable = (Scrollable) event.widget;
					Rectangle clientArea = scrollable.getClientArea();
					int clientWidth = clientArea.width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(ThemePlugin.getDefault().getColorManager()
							.getColor(getTheme().getSelectionAgainstBG()));
					gc.fillRectangle(clientArea.x, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;

					// force foreground color. Otherwise on dark themes we get black FG (all the time on Win, on
					// non-focus for Mac)
					gc.setForeground(ThemePlugin.getDefault().getColorManager().getColor(getTheme().getForeground()));
				}
			}
		};
		table.addListener(SWT.EraseItem, selectionOverride);

		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new IStructuredContentProvider()
		{

			private Theme theme;

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				this.theme = (Theme) newInput;
			}

			public void dispose()
			{

			}

			public Object[] getElements(Object inputElement)
			{
				return theme.getTokens().toArray();
			}
		});
		tableViewer.setLabelProvider(new TokenLabelProvider());
		tableViewer.getTable().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				Table table = tableViewer.getTable();
				// If user is clicking in the FG/BG column when it's empty, pop open a color dialog
				int fgColX = table.getColumn(0).getWidth(); // scope col width
				int fgColWidth = table.getColumn(1).getWidth(); // fg col width
				int bgColX = fgColX + fgColWidth;
				int bgColWidth = table.getColumn(2).getWidth() + 2;

				if (e.x > fgColX && e.x < (fgColX + fgColWidth))
				{
					// user clicked in FG column
					ColorDialog colorDialog = new ColorDialog(table.getShell());
					colorDialog.setRGB(getTheme().getForeground());
					RGB newRGB = colorDialog.open();
					if (newRGB == null)
					{
						return; // no color selected, don't change a thing!
					}
					TableItem tableItem = table.getItem(new Point(e.x, e.y));
					ThemeRule token = (ThemeRule) tableItem.getData();
					int index = table.indexOf(tableItem);
					getTheme().updateRule(index, token.updateFG(new RGBa(newRGB)));
				}
				else if (e.x > bgColX && e.x < (bgColX + bgColWidth)) // is user clicking in the BG column?
				{
					ColorDialog colorDialog = new ColorDialog(table.getShell());
					colorDialog.setRGB(getTheme().getBackground());
					RGB newRGB = colorDialog.open();
					if (newRGB == null)
					{
						return; // no color selected, don't change a thing!
					}
					TableItem tableItem = table.getItem(new Point(e.x, e.y));
					ThemeRule token = (ThemeRule) tableItem.getData();
					int index = table.indexOf(tableItem);
					getTheme().updateRule(index, token.updateBG(new RGBa(newRGB)));
				}
				else
				{
					return;
				}

				tableViewer.refresh();
				addCustomTableEditorControls();
			}
		});

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tokenName = column.getColumn();
		tokenName.setText("Element"); //$NON-NLS-1$
		layout.setColumnData(tokenName, new ColumnWeightData(100, true));
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				ThemeRule token = (ThemeRule) element;
				return token.getName();
			}

			public Color getForeground(Object element)
			{
				ThemeRule token = (ThemeRule) element;
				// TODO How do we handle alpha?
				RGBa fg = token.getTextAttribute().foreground;
				if (fg == null)
					return ThemePlugin.getDefault().getColorManager().getColor(getTheme().getForeground());
				return ThemePlugin.getDefault().getColorManager().getColor(fg.toRGB());
			}

			public Color getBackground(Object element)
			{
				ThemeRule token = (ThemeRule) element;
				// TODO How do we handle alpha?
				RGBa bg = token.getTextAttribute().background;
				if (bg == null)
					return ThemePlugin.getDefault().getColorManager().getColor(getTheme().getBackground());
				return ThemePlugin.getDefault().getColorManager().getColor(bg.toRGB());
			}

			public Font getFont(Object element)
			{
				ThemeRule token = (ThemeRule) element;
				if (token.getTextAttribute().style == 0) // TODO Limit to only checking for bold or italic
					return fFont;
				return lazyFont(fFont, token.getTextAttribute().style);
			}
		});

		column.setEditingSupport(new EditingSupport(tableViewer)
		{

			private TextCellEditor cellEditor;

			@Override
			protected void setValue(Object element, Object value)
			{
				ThemeRule token = (ThemeRule) element;
				String newName = (String) value;
				if (newName.equals(token.getName()))
				{
					return;
				}
				// update the token in the theme
				int index = getTheme().getTokens().indexOf(token);
				getTheme().updateRule(index, token.setName(newName));
				tableViewer.refresh();
			}

			@Override
			protected Object getValue(Object element)
			{
				ThemeRule token = (ThemeRule) element;
				return token.getName();
			}

			@Override
			protected CellEditor getCellEditor(Object element)
			{
				ThemeRule token = (ThemeRule) element;
				cellEditor = new TextCellEditor(table);
				cellEditor.setValue(token.getName());
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element)
			{
				return true;
			}
		});

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn foreground = column.getColumn();
		foreground.setResizable(false);
		foreground.setText(Messages.ThemePreferencePage_ForegroundColumnLabel);
		layout.setColumnData(foreground, new ColumnPixelData(30, false));
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return ""; //$NON-NLS-1$
			}
		});

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn background = column.getColumn();
		background.setResizable(false);
		background.setText(Messages.ThemePreferencePage_BackgroundColumnLabel);
		layout.setColumnData(background, new ColumnPixelData(30, false));
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return ""; //$NON-NLS-1$
			}
		});

		final TableColumn fontStyle = new TableColumn(table, SWT.NONE);
		fontStyle.setResizable(false);
		fontStyle.setText(Messages.ThemePreferencePage_FontStyleColumnLabel);
		layout.setColumnData(fontStyle, new ColumnPixelData(75, false));

		Composite editTokenList = new Composite(composite, SWT.NONE);
		editTokenList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout grid = new GridLayout(2, false);
		editTokenList.setLayout(grid);

		Composite buttons = new Composite(editTokenList, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout(2, true);
		buttonsLayout.marginWidth = 0;
		buttonsLayout.horizontalSpacing = 0;
		buttons.setLayout(buttonsLayout);

		fAddTokenButton = new Button(buttons, SWT.PUSH | SWT.FLAT);
		fAddTokenButton.setBounds(0, 0, 16, 16);
		fAddTokenButton.setLayoutData(new GridData(GridData.FILL_BOTH));
		fAddTokenButton.setText(Messages.ThemePreferencePage_AddTokenLabel);
		fAddTokenButton.addSelectionListener(this);
		fRemoveTokenButton = new Button(buttons, SWT.PUSH | SWT.FLAT);
		fRemoveTokenButton.setLayoutData(new GridData(GridData.FILL_BOTH));
		fRemoveTokenButton.setText(Messages.ThemePreferencePage_RemoveTokenLabel);
		fRemoveTokenButton.addSelectionListener(this);

		Composite textField = new Composite(editTokenList, SWT.NONE);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textField.setLayout(new GridLayout(2, false));
		Label addTokenLabel = new Label(textField, SWT.RIGHT);
		addTokenLabel.setText(Messages.ThemePreferencePage_ScopeSelectoreLabel);

		fScopeText = new Combo(textField, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		fScopeText.setLayoutData(data);
		for (String preset : tokenTypeNames)
		{
			fScopeText.add(preset);
		}
		table.addSelectionListener(this);
		fScopeSelectorDecoration = new ControlDecoration(fScopeText, SWT.RIGHT);
		fScopeText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				// Update the scope selector for the current token!
				TableItem[] selection = table.getSelection();
				if (selection == null || selection.length == 0)
				{
					return;
				}
				TableItem item = selection[0];
				ThemeRule rule = (ThemeRule) item.getData();

				String scopeSelectorText = fScopeText.getText();
				// Validate the value isn't a duplicate!
				ScopeSelector selector = new ScopeSelector(scopeSelectorText);
				ThemeRule match = getTheme().getRuleForSelector(selector);
				if (scopeSelectorText.length() > 0 && match != null && match != rule)
				{
					FieldDecoration dec = FieldDecorationRegistry.getDefault().getFieldDecoration(
							FieldDecorationRegistry.DEC_WARNING);
					fScopeSelectorDecoration.setImage(dec.getImage());
					fScopeSelectorDecoration.setDescriptionText(MessageFormat.format(
							Messages.ThemePreferencePage_DuplicateScopeSelectorRules, match.getName()));
					fScopeText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				}
				else
				{
					fScopeSelectorDecoration.setDescriptionText(null);
					fScopeSelectorDecoration.setImage(null);
					fScopeText.setForeground(null);
				}

				int index = table.indexOf(item);
				ThemeRule newRule = rule.setScopeSelector(selector);
				getTheme().updateRule(index, newRule);
				item.setData(newRule);
			}
		});

		addDNDToTable(table);
	}

	private void addDNDToTable(final Table table)
	{
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		DragSource source = new DragSource(table, DND.DROP_MOVE);
		source.setTransfer(types);

		source.addDragListener(new DragSourceAdapter()
		{
			@Override
			public void dragStart(DragSourceEvent event)
			{
				reorderingRules = true;
				super.dragStart(event);
			}

			@Override
			public void dragFinished(DragSourceEvent event)
			{
				reorderingRules = false;
				super.dragFinished(event);
			}

			public void dragSetData(DragSourceEvent event)
			{
				// Get the selected items in the drag source
				DragSource ds = (DragSource) event.widget;
				Table table = (Table) ds.getControl();
				int selected = table.getSelectionIndex();
				event.data = Integer.toString(selected);
			}
		});

		// Create the drop target
		DropTarget target = new DropTarget(table, DND.DROP_MOVE);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter()
		{
			public void dragEnter(DropTargetEvent event)
			{
				// Allow dropping text only
				for (int i = 0, n = event.dataTypes.length; i < n; i++)
				{
					if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i]))
					{
						event.currentDataType = event.dataTypes[i];
					}
				}
			}

			public void dragOver(DropTargetEvent event)
			{
				if (reorderingRules)
				{
					event.feedback = DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_INSERT_BEFORE | DND.FEEDBACK_SCROLL;
				}
				else
				{
					// Don't show insertion feedback if it's a button!
					event.feedback = DND.FEEDBACK_SCROLL;
				}
			}

			public void drop(DropTargetEvent event)
			{
				if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					String data = (String) event.data;
					// Handle drgagging of fg/bg color to remove it
					if (data.startsWith("button:")) //$NON-NLS-1$
					{
						String[] parts = data.split(":"); //$NON-NLS-1$
						int row = Integer.parseInt(parts[1]);
						int fgBg = Integer.parseInt(parts[2]);

						TableItem item = table.getItem(row);
						Rectangle bounds = item.getBounds(fgBg);
						bounds = event.display.map(table, null, bounds);
						if (bounds.contains(event.x, event.y))
						{
							// Hasn't been dragged out of it's area, don't do anything
							return;
						}
						// remove the fg or bg for the rule
						ThemeRule rule = (ThemeRule) item.getData();
						if (fgBg == 1)
						{
							getTheme().updateRule(row, rule.updateFG(null));
						}
						else
						{
							getTheme().updateRule(row, rule.updateBG(null));
						}
						tableViewer.refresh(true);

						event.display.asyncExec(new Runnable()
						{

							public void run()
							{
								addCustomTableEditorControls();
							}
						});
						return;
					}
					// It's not a button drag to remove the fg/bg, so...
					// Re-order rules
					DropTarget target = (DropTarget) event.widget;
					Table table = (Table) target.getControl();
					int selectionIndex = Integer.parseInt(data);

					TableItem item = (TableItem) event.item;
					int insertionIndex = table.indexOf(item);
					getTheme().reorderRule(selectionIndex, insertionIndex);
					tableViewer.refresh(true);
					addCustomTableEditorControls();

				}
			}
		});
	}

	protected Font lazyFont(Font font, int style)
	{
		if (fFonts == null)
			fFonts = new HashMap<Integer, Font>();
		Font returnFont = fFonts.get(style);
		if (returnFont == null)
		{
			returnFont = new Font(font.getDevice(), font.getFontData()[0].getName(), font.getFontData()[0].getHeight(),
					style);
			fFonts.put(style, returnFont);
		}
		return returnFont;
	}

	static class TokenLabelProvider extends BaseLabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			ThemeRule commit = (ThemeRule) element;
			if (commit == null)
				return ""; //$NON-NLS-1$
			switch (columnIndex)
			{
				case 0:
					return commit.getName();
				case 1:
					return commit.getTextAttribute().foreground == null ? "" : commit.getTextAttribute().foreground //$NON-NLS-1$
							.toString();
				case 2:
					return commit.getTextAttribute().background == null ? "" : commit.getTextAttribute().background //$NON-NLS-1$
							.toString();
				default:
					return ""; //$NON-NLS-1$
			}
		}

	}

	protected void setTheme(Theme newTheme)
	{
		fSelectedTheme = newTheme;
		newTheme.save();
		Theme theme = getTheme();
		fgSelector.setColorValue(theme.getForeground());
		bgSelector.setColorValue(theme.getBackground());
		lineHighlightSelector.setColorValue(theme.getLineHighlight().toRGB());
		caretSelector.setColorValue(theme.getCaret());
		selectionSelector.setColorValue(theme.getSelection().toRGB());
		fThemeCombo.setText(theme.getName());
		tableViewer.setInput(theme);
		addCustomTableEditorControls();
		if (getThemeManager().isBuiltinTheme(theme.getName()))
		{
			renameThemeButton.setEnabled(false);
			deleteThemeButton.setEnabled(false);
		}
		else
		{
			renameThemeButton.setEnabled(true);
			deleteThemeButton.setEnabled(true);
		}
	}

	private void addCustomTableEditorControls()
	{
		clearTableEditors();

		final Table table = tableViewer.getTable();
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{
			ThemeRule rule = (ThemeRule) items[i].getData();
			if (rule.getTextAttribute().foreground != null)
			{
				createButton(table, items[i], 1, rule.getTextAttribute().foreground);
			}
			if (rule.getTextAttribute().background != null)
			{
				createButton(table, items[i], 2, rule.getTextAttribute().background);
			}
			createFontStyle(table, items[i], rule.getTextAttribute());
		}
	}

	private void clearTableEditors()
	{
		if (fTableEditors == null)
			fTableEditors = new HashSet<TableEditor>();

		for (TableEditor tableEditor : fTableEditors)
		{
			tableEditor.getEditor().dispose();
			tableEditor.dispose();
		}
		fTableEditors.clear();
	}

	private void createFontStyle(final Table table, final TableItem item, DelayedTextAttribute text)
	{
		boolean isBold = (text.style & SWT.BOLD) != 0;
		boolean isItalic = (text.style & SWT.ITALIC) != 0;
		boolean isUnderline = (text.style & TextAttribute.UNDERLINE) != 0;
		TableEditor editor = new TableEditor(table);
		Composite buttons = new Composite(table, SWT.NONE);
		GridLayout grid = new GridLayout(3, true);
		grid.marginHeight = 0;
		grid.marginWidth = 0;
		grid.horizontalSpacing = 0;
		buttons.setLayout(grid);
		final Button b = new Button(buttons, SWT.TOGGLE | SWT.FLAT);
		b.setText(Messages.ThemePreferencePage_BoldButtonLabel);
		b.setSelection(isBold);
		b.setSize(16, 16);
		b.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Button italic = new Button(buttons, SWT.TOGGLE | SWT.FLAT);
		italic.setText(Messages.ThemePreferencePage_ItalicButtonLabel);
		italic.setSelection(isItalic);
		italic.setLayoutData(new GridData(GridData.FILL_BOTH));
		italic.setSize(16, 16);
		final Button u = new Button(buttons, SWT.TOGGLE | SWT.FLAT);
		u.setText(Messages.ThemePreferencePage_UnderlineButtonLabel);
		u.setSelection(isUnderline);
		u.setLayoutData(new GridData(GridData.FILL_BOTH));
		u.setSize(16, 16);

		buttons.pack();
		editor.minimumWidth = buttons.getSize().x;
		editor.horizontalAlignment = SWT.LEFT;
		editor.setEditor(buttons, item, 3);
		fTableEditors.add(editor);

		SelectionAdapter selectionAdapter = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ThemeRule token = (ThemeRule) item.getData();
				int style = 0;
				if (u.getSelection())
				{
					style |= TextAttribute.UNDERLINE;
				}
				if (b.getSelection())
				{
					style |= SWT.BOLD;
				}
				if (italic.getSelection())
				{
					style |= SWT.ITALIC;
				}

				int index = table.indexOf(item);
				getTheme().updateRule(index, token.updateFontStyle(style));
				tableViewer.refresh();
			}
		};
		b.addSelectionListener(selectionAdapter);
		italic.addSelectionListener(selectionAdapter);
		u.addSelectionListener(selectionAdapter);
	}

	private void createButton(final Table table, final TableItem tableItem, final int index, final RGBa color)
	{
		TableEditor editor = new TableEditor(table);
		Button button = new Button(table, SWT.PUSH | SWT.FLAT);
		Image image = createColorImage(table, color);
		button.setImage(image);
		button.pack();
		editor.minimumWidth = button.getSize().x - 4;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(button, tableItem, index);
		fTableEditors.add(editor);
		button.setData("color", color); //$NON-NLS-1$

		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ColorDialog colorDialog = new ColorDialog(table.getShell());
				Button self = ((Button) e.widget);
				RGBa theColor = (RGBa) self.getData("color"); //$NON-NLS-1$
				if (theColor == null)
				{
					theColor = color;
				}
				colorDialog.setRGB(theColor.toRGB());
				RGB newRGB = colorDialog.open();
				if (newRGB == null)
				{
					return;
				}
				ThemeRule token = (ThemeRule) tableItem.getData();
				RGBa newColor = new RGBa(newRGB);
				if (index == 1)
				{
					getTheme().updateRule(table.indexOf(tableItem), token.updateFG(newColor));
				}
				else
				{
					getTheme().updateRule(table.indexOf(tableItem), token.updateBG(newColor));
				}
				// Update the image for this button!
				self.setImage(createColorImage(table, newColor));
				self.setData("color", newColor); //$NON-NLS-1$
				tableViewer.refresh();
			}
		});

		// Allow dragging the button out of it's location to remove the fg/bg for the rule!
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		final DragSource source = new DragSource(button, DND.DROP_MOVE);
		source.setTransfer(types);

		source.addDragListener(new DragSourceAdapter()
		{
			public void dragSetData(DragSourceEvent event)
			{
				event.data = "button:" + table.indexOf(tableItem) + ":" + index; //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	protected Image createColorImage(final Table table, final RGBa color)
	{
		Image image = new Image(table.getDisplay(), 16, 16);
		GC gc = new GC(image);
		if (color != null)
		{
			gc.setBackground(ThemePlugin.getDefault().getColorManager().getColor(color.toRGB()));
		}
		gc.fillRectangle(0, 0, 16, 16);
		gc.dispose();
		return image;
	}

	public void init(IWorkbench workbench)
	{
		MApplication application = (MApplication) workbench.getService(MApplication.class);
		IEclipseContext context = application.getContext();
		defaultE4Theme = (String) context.get(E4Application.THEME_ID);
		e4ThemeEngine = context.get(IThemeEngine.class);
	}

	@Override
	public boolean performOk()
	{
		org.eclipse.e4.ui.css.swt.theme.ITheme selectedTheme = getSelection();
		if (selectedTheme != null)
		{
			e4ThemeEngine.setTheme(selectedTheme, true);
		}

		performOkFonts();
		getTheme().save();
		getThemeManager().setCurrentTheme(getTheme());
		return super.performOk();
	}

	protected void performOkFonts()
	{
		final String[] fontIds = new String[] { JFaceResources.TEXT_FONT,
				"org.eclipse.ui.workbench.texteditor.blockSelectionModeFont" }; //$NON-NLS-1$

		FontData[] data = fFont.getFontData();
		for (String fontId : fontIds)
		{
			setFont(fontId, data);
		}

		// Shrink by 2 for views!
		data = fFont.getFontData();
		FontData[] smaller = new FontData[data.length];
		int i = 0;
		for (FontData fd : data)
		{
			int height = fd.getHeight();
			if (height >= 12)
			{
				fd.setHeight(height - 2);
			}
			else if (height >= 10)
			{
				fd.setHeight(height - 1);
			}
			smaller[i++] = fd;
		}
		setFont(IThemeManager.VIEW_FONT_NAME, smaller);
	}

	private void setFont(String fontId, FontData[] data)
	{
		String fdString = PreferenceConverter.getStoredRepresentation(data);
		// Only set new values if they're different from existing!
		Font existing = JFaceResources.getFont(fontId);
		String existingString = ""; //$NON-NLS-1$
		if (!existing.isDisposed())
		{
			existingString = PreferenceConverter.getStoredRepresentation(existing.getFontData());
		}
		if (!existingString.equals(fdString))
		{
			// put in registry...
			JFaceResources.getFontRegistry().put(fontId, data);
			// Save to prefs...
			ITheme currentTheme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
			String key = ThemeElementHelper.createPreferenceKey(currentTheme, fontId);
			IPreferenceStore store = WorkbenchPlugin.getDefault().getPreferenceStore();
			store.setValue(key, fdString);
		}
	}

	@Override
	protected void performDefaults()
	{
		e4ThemeEngine.setTheme(defaultE4Theme, true);
		if (e4ThemeEngine.getActiveTheme() != null)
		{
			e4ThemeIdCombo.setSelection(new StructuredSelection(e4ThemeEngine.getActiveTheme()));
		}
		// Reset the font to what it was originally!
		setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
		try
		{
			Theme theme = getTheme();
			theme.loadFromDefaults();
			setTheme(fSelectedTheme);
		}
		catch (Exception e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
		super.performDefaults();
	}

	@Override
	public boolean performCancel()
	{
		if (currentE4Theme != null)
		{
			e4ThemeEngine.setTheme(currentE4Theme, false);
		}
		return super.performCancel();
	}

	protected Theme getTheme()
	{
		return fSelectedTheme;
	}

	@Override
	public void dispose()
	{
		if (fFonts != null)
		{
			for (Font font : fFonts.values())
			{
				font.dispose();
			}
			fFonts.clear();
			fFonts = null;
		}
		fFont = null;
		super.dispose();
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fAptanaEditorsOnlyCheckbox)
		{
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID);
			prefs.putBoolean(IPreferenceConstants.APPLY_TO_ALL_EDITORS, fAptanaEditorsOnlyCheckbox.getSelection());
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e1)
			{
				IdeLog.logError(ThemePlugin.getDefault(), e1);
			}
		}
		else if (source == fThemeCombo)
		{
			setTheme(fThemeCombo.getText());
		}
		else if (source == fAddThemeButton)
		{
			// Pop a dialog to ask for new name
			InputDialog dialog = new InputDialog(getShell(), Messages.ThemePreferencePage_NewThemeTitle,
					Messages.ThemePreferencePage_NewThemeMsg, getUniqueNewThemeName(fSelectedTheme.getName()), this);
			if (dialog.open() == Window.OK)
			{
				Theme newTheme = getTheme().copy(dialog.getValue());
				// Add theme to theme list, make current theme this one
				getThemeManager().setCurrentTheme(newTheme);
				loadThemeNames();
				setTheme(newTheme.getName());
			}
		}
		else if (source == renameThemeButton)
		{
			// Pop a dialog to ask for new name
			InputDialog dialog = new InputDialog(getShell(), Messages.ThemePreferencePage_RenameThemeTitle,
					Messages.ThemePreferencePage_RenameThemeMsg, fSelectedTheme.getName(), this);
			if (dialog.open() == Window.OK)
			{
				Theme oldTheme = getTheme();
				Theme newTheme = oldTheme.copy(dialog.getValue());
				getThemeManager().setCurrentTheme(newTheme);
				oldTheme.delete();
				loadThemeNames();
				setTheme(newTheme.getName());
			}
		}
		else if (source == deleteThemeButton)
		{
			boolean ok = MessageDialog.openConfirm(getShell(),
					MessageFormat.format(Messages.ThemePreferencePage_DeleteThemeTitle, fSelectedTheme.getName()),
					MessageFormat.format(Messages.ThemePreferencePage_DeleteThemeMsg, fSelectedTheme.getName()));
			if (ok)
			{
				getTheme().delete();
				loadThemeNames();
				setTheme(getThemeManager().getCurrentTheme().getName());
			}
		}
		else if (source == fImportButton)
		{
			FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
			IDialogSettings editorSettings = ThemePlugin.getDefault().getDialogSettings();
			String value = editorSettings.get(THEME_DIRECTORY);
			if (value != null)
			{
				fileDialog.setFilterPath(value);
			}
			fileDialog.setFilterExtensions(new String[] { "*.tmTheme" }); //$NON-NLS-1$
			String path = fileDialog.open();
			if (path == null)
			{
				return;
			}

			File themeFile = new File(path);
			editorSettings.put(THEME_DIRECTORY, themeFile.getParent());

			Theme theme = new TextmateImporter().convert(themeFile);
			if (theme != null)
			{
				getThemeManager().addTheme(theme);
				getThemeManager().setCurrentTheme(theme);
				loadThemeNames();
				setTheme(theme.getName());
			}
			else
			{
				// FIXME Show an error dialog?
			}

		}
		else if (source == fExportButton)
		{
			FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
			IDialogSettings editorSettings = ThemePlugin.getDefault().getDialogSettings();
			String value = editorSettings.get(THEME_EXPORT_DIRECTORY);
			if (value != null)
			{
				fileDialog.setFilterPath(value);
			}
			fileDialog.setFileName(getTheme().getName() + ".tmTheme"); //$NON-NLS-1$
			String path = fileDialog.open();
			if (path == null)
			{
				return;
			}

			File themeFile = new File(path);
			editorSettings.put(THEME_EXPORT_DIRECTORY, themeFile.getParent());

			new ThemeExporter().export(themeFile, getTheme());
		}
		else if (source == fAddTokenButton)
		{
			// Add a new row to the table by adding a basic token to the theme
			Theme theme = getTheme();
			String newName = "untitled"; //$NON-NLS-1$
			// Insert into the rules at the index after current selection in table!
			int index = tableViewer.getTable().getSelectionIndex();
			if (index == -1)
			{
				index = tableViewer.getTable().getItemCount();
			}
			else
			{
				index++;
			}
			theme.addNewDefaultToken(index, newName);
			setTheme(fSelectedTheme);
			// Have the new addition in an edit mode
			Object newElement = tableViewer.getElementAt(index);
			if (newElement != null)
			{
				tableViewer.editElement(newElement, 0);
				fScopeText.setText(""); //$NON-NLS-1$
			}
		}
		else if (source == fRemoveTokenButton)
		{
			TableItem[] items = tableViewer.getTable().getSelection();
			if (items == null || items.length == 0)
			{
				return;
			}
			Theme theme = getTheme();
			for (TableItem tableItem : items)
			{
				ThemeRule entry = (ThemeRule) tableItem.getData();
				theme.remove(entry);
			}
			theme.save();
			setTheme(fSelectedTheme);
		}
		else if (source == tableViewer.getTable())
		{
			TableItem item = (TableItem) e.item;
			ThemeRule token = (ThemeRule) item.getData();
			fScopeText.setText(token.getScopeSelector().toString());
		}
	}

	private String getUniqueNewThemeName(String themeName)
	{
		String newName = MessageFormat.format(Messages.ThemePreferencePage_NewThemeDefaultName, themeName);
		int index = 2;
		while (getThemeManager().getTheme(newName) != null)
		{
			newName = MessageFormat.format(Messages.ThemePreferencePage_NewThemeDefaultName_2, index++, themeName);
		}
		return newName;
	}

	private void setTheme(String text)
	{
		setTheme(getThemeManager().getTheme(text));
	}

	public String isValid(String newText)
	{
		IStatus status = getThemeManager().validateThemeName(newText);
		if (status.isOK())
		{
			return null;
		}
		return status.getMessage();
	}

	public void propertyChange(PropertyChangeEvent event)
	{
		Object value = event.getNewValue();
		if (value == null)
		{
			return;
		}

		RGB newColor = (RGB) value;
		Theme theme = getTheme();
		Object source = event.getSource();
		if (source == fgSelector)
		{
			theme.updateFG(newColor);
		}
		else if (source == selectionSelector)
		{
			theme.updateSelection(newColor);
		}
		else if (source == bgSelector)
		{
			theme.updateBG(newColor);
		}
		else if (source == lineHighlightSelector)
		{
			theme.updateLineHighlight(newColor);
		}
		else if (source == caretSelector)
		{
			theme.updateCaret(newColor);
		}
		setTheme(fSelectedTheme);
	}

	protected void setFont(Font font)
	{
		if (fFont.equals(font)) // TODO Also same if FontData arrays are equal!
		{
			return;
		}
		fFont = font;
		fFontText.setFont(fFont);
		fFontText.setText(ThemePreferencePage.toString(fFont));
		// Set the fFont on the table!
		if (fFonts != null)
		{
			fFonts.clear();
		}
		tableViewer.refresh();
	}
}
