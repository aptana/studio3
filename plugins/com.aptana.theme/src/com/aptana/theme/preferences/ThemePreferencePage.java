package com.aptana.theme.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.scripting.model.BundleManager;
import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.TextmateImporter;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemeExporter;
import com.aptana.theme.ThemePlugin;

public class ThemePreferencePage extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener,
		IInputValidator, IPropertyChangeListener
{

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

	protected String fSelectedTheme;

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

	private Button fInvasiveThemeCheckbox;
	private Button fAddThemeButton;
	private Button fImportButton;
	private Button fAddTokenButton;
	private Button fRemoveTokenButton;
	private Text fScopeText;

	private Button fExportButton;

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true));

		createThemeListControls(composite);
		createGlobalColorControls(composite);
		createTokenEditTable(composite);
		createInvasivePrefArea(composite);

		setTheme(getThemeManager().getCurrentTheme().getName());
		return composite;
	}

	private void createInvasivePrefArea(Composite composite)
	{
		Composite themesComp = new Composite(composite, SWT.NONE);
		themesComp.setLayout(new GridLayout(1, false));

		fInvasiveThemeCheckbox = new Button(themesComp, SWT.CHECK);
		fInvasiveThemeCheckbox.setText(Messages.ThemePreferencePage_InvasiveThemesLBL);
		fInvasiveThemeCheckbox.setSelection(Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID,
				IPreferenceConstants.INVASIVE_THEMES, false, null));
		fInvasiveThemeCheckbox.addSelectionListener(this);
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
		renameThemeButton.setText(Messages.ThemePreferencePage_RenameButtonLabel);
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
			@SuppressWarnings("unchecked")
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.FOREGROUND) != 0 && event.index == 0)
				{
					TableItem item = (TableItem) event.item;
					Entry<String, TextAttribute> token = (Entry<String, TextAttribute>) item.getData();
					if ((token.getValue().getStyle() & TextAttribute.UNDERLINE) != 0)
					{
						int y = event.getBounds().y + event.getBounds().height - 6;
						int x2 = event.getBounds().width;
						Color oldFG = event.gc.getForeground();
						event.gc.setForeground(token.getValue().getForeground());
						event.gc.drawLine(0, y, x2, y);
						event.gc.setForeground(oldFG);
						event.detail &= ~SWT.FOREGROUND;
					}
				}
			}
		});

		// Override selection color to match what is set in theme
		table.addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				// Don't draw the background of the first column for selections
				if ((event.detail & SWT.SELECTED) != 0 && event.index == 0)
				{
					event.detail &= ~SWT.SELECTED;
				}
			}
		});
		// Manual hack to draw the right bg color for the first column selection
		// FIXME This should be able to be done by listening to Erase/PaintItem events!
		table.addSelectionListener(new SelectionListener()
		{
			TableItem lastSelected;
			private Color lastSelectedColor;

			public void widgetSelected(SelectionEvent e)
			{
				if (lastSelected != null)
				{
					lastSelected.setBackground(0, lastSelectedColor);
				}
				TableItem item = (TableItem) e.item;
				lastSelectedColor = item.getBackground(0);
				lastSelected = item;
				item.setBackground(0, ThemePlugin.getDefault().getColorManager().getColor(getTheme().getSelection()));
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

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

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Object[] getElements(Object inputElement)
			{
				Map<String, TextAttribute> tokens = theme.getTokens();
				Object[] array = tokens.entrySet().toArray();
				// Sort by keys!
				Arrays.sort(array, new Comparator()
				{
					public int compare(Object o1, Object o2)
					{
						Entry<String, TextAttribute> e1 = (Entry<String, TextAttribute>) o1;
						Entry<String, TextAttribute> e2 = (Entry<String, TextAttribute>) o2;
						return e1.getKey().compareTo(e2.getKey());
					}
				});
				return array;
			}
		});
		tableViewer.setLabelProvider(new TokenLabelProvider());
		tableViewer.getTable().addMouseListener(new MouseAdapter()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void mouseDown(MouseEvent e)
			{
				Table table = tableViewer.getTable();
				// If user is clicking in the BG column when it's empty, pop open a color dialog
				int myX = table.getColumn(0).getWidth();
				myX += table.getColumn(1).getWidth();
				int width = table.getColumn(2).getWidth() + 2;
				if (!(e.x > myX && e.x < (myX + width)))
					return;
				TableItem tableItem = table.getItem(new Point(e.x, e.y));
				ColorDialog colorDialog = new ColorDialog(table.getShell());
				colorDialog.setRGB(getTheme().getBackground());
				RGB newRGB = colorDialog.open();
				if (newRGB == null)
					return;
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) tableItem.getData();
				Color fg = token.getValue().getForeground();
				Color bg = ThemePlugin.getDefault().getColorManager().getColor(newRGB);

				TextAttribute at = new TextAttribute(fg, bg, token.getValue().getStyle(), token.getValue().getFont());
				getTheme().update(token.getKey(), at);
				setTheme(fSelectedTheme);
			}
		});

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tokenName = column.getColumn();
		tokenName.setText("Element"); //$NON-NLS-1$
		layout.setColumnData(tokenName, new ColumnWeightData(100, true));
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@SuppressWarnings("unchecked")
			public String getText(Object element)
			{
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) element;
				return token.getKey();
			}

			@SuppressWarnings("unchecked")
			public Color getForeground(Object element)
			{
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) element;
				Color fg = token.getValue().getForeground();
				if (fg == null)
					return ThemePlugin.getDefault().getColorManager().getColor(getTheme().getForeground());
				return fg;
			}

			@SuppressWarnings("unchecked")
			public Color getBackground(Object element)
			{
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) element;
				Color bg = token.getValue().getBackground();
				if (bg == null)
				{
					return ThemePlugin.getDefault().getColorManager().getColor(getTheme().getBackground());
				}
				return bg;
			}

			@SuppressWarnings("unchecked")
			public Font getFont(Object element)
			{
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) element;
				Font font = token.getValue().getFont();
				if (font == null)
					font = JFaceResources.getTextFont();
				if (token.getValue().getStyle() == 0) // TODO Limit to only checking for bold or italic
					return font;
				return lazyFont(font, token.getValue().getStyle());
			}
		});

		column.setEditingSupport(new EditingSupport(tableViewer)
		{

			private ComboBoxCellEditor cellEditor;

			@SuppressWarnings("unchecked")
			@Override
			protected void setValue(Object element, Object value)
			{
				// FIXME What if user has edited the value but is trying to delete the row? check to see if the token
				// even exists in the theme before saving/updating
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) element;
				Integer selection = (Integer) value;
				String newName = null;
				if (selection.intValue() == -1)
				{
					try
					{
						// edited value, need to grab text of combo
						Field field = cellEditor.getClass().getDeclaredField("comboBox"); //$NON-NLS-1$
						field.setAccessible(true);
						CCombo combo = (CCombo) field.get(cellEditor);
						newName = combo.getText();
					}
					catch (Exception e)
					{
						ThemePlugin.logError(e);
					}
				}
				else
				{
					newName = cellEditor.getItems()[selection];
				}
				if (newName.equals(token.getKey()))
				{
					return;
				}
				Theme theme = getTheme();
				theme.remove(token.getKey());
				theme.update(newName, token.getValue());
				setTheme(fSelectedTheme);
			}

			@Override
			protected Object getValue(Object element)
			{
				return 0;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected CellEditor getCellEditor(Object element)
			{
				List<String> tokenTypes = new ArrayList<String>();
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) element;
				tokenTypes.add(token.getKey());
				tokenTypes.addAll(tokenTypeNames);
				cellEditor = new ComboBoxCellEditor(table, tokenTypes.toArray(new String[tokenTypes.size()]));
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

		fScopeText = new Text(textField, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		fScopeText.setLayoutData(data);
		fScopeText.setEditable(false);
		table.addSelectionListener(this);
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

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex)
		{
			Map.Entry<String, TextAttribute> commit = (Map.Entry<String, TextAttribute>) element;
			if (commit == null)
				return ""; //$NON-NLS-1$
			switch (columnIndex)
			{
				case 0:
					return commit.getKey();
				case 1:
					return commit.getValue().getForeground() == null ? "" : commit.getValue().getForeground() //$NON-NLS-1$
							.toString();
				case 2:
					return commit.getValue().getBackground() == null ? "" : commit.getValue().getBackground() //$NON-NLS-1$
							.toString();
				default:
					return commit.getValue().getFont() == null ? "" : commit.getValue().getFont().toString(); //$NON-NLS-1$
			}
		}

	}

	protected void setTheme(String themeName)
	{
		fSelectedTheme = themeName;
		Theme theme = getTheme();
		fgSelector.setColorValue(theme.getForeground());
		bgSelector.setColorValue(theme.getBackground());
		lineHighlightSelector.setColorValue(theme.getLineHighlight());
		caretSelector.setColorValue(theme.getCaret());
		selectionSelector.setColorValue(theme.getSelection());
		fThemeCombo.setText(themeName);
		tableViewer.setInput(theme);
		addCustomTableEditorControls();
		if (getThemeManager().isBuiltinTheme(themeName))
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

	@SuppressWarnings("unchecked")
	private void addCustomTableEditorControls()
	{
		clearTableEditors();

		final Table table = tableViewer.getTable();
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{

			Map.Entry<String, TextAttribute> commit = (Map.Entry<String, TextAttribute>) items[i].getData();
			createButton(table, items[i], 1, commit.getValue().getForeground());
			if (commit.getValue().getBackground() != null)
				createButton(table, items[i], 2, commit.getValue().getBackground());
			createFontStyle(table, items[i], commit.getValue());
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

	private void createFontStyle(final Table table, final TableItem item, TextAttribute text)
	{
		boolean isBold = (text.getStyle() & SWT.BOLD) != 0;
		boolean isItalic = (text.getStyle() & SWT.ITALIC) != 0;
		boolean isUnderline = (text.getStyle() & TextAttribute.UNDERLINE) != 0;
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
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) item.getData();
				int style = 0;
				if (u.getSelection())
					style |= TextAttribute.UNDERLINE;
				if (b.getSelection())
					style |= SWT.BOLD;
				if (italic.getSelection())
					style |= SWT.ITALIC;
				TextAttribute at = new TextAttribute(token.getValue().getForeground(),
						token.getValue().getBackground(), style, token.getValue().getFont());
				getTheme().update(token.getKey(), at);
				setTheme(fSelectedTheme);
			}
		};
		b.addSelectionListener(selectionAdapter);
		italic.addSelectionListener(selectionAdapter);
		u.addSelectionListener(selectionAdapter);
	}

	private void createButton(final Table table, final TableItem tableItem, final int index, final Color color)
	{
		TableEditor editor = new TableEditor(table);
		Button button = new Button(table, SWT.PUSH | SWT.FLAT);
		Image image = new Image(table.getDisplay(), 16, 16);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(0, 0, 16, 16);
		gc.dispose();
		button.setImage(image);
		button.pack();
		editor.minimumWidth = button.getSize().x - 4;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(button, tableItem, index);
		fTableEditors.add(editor);

		button.addSelectionListener(new SelectionAdapter()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ColorDialog colorDialog = new ColorDialog(table.getShell());
				colorDialog.setRGB(color.getRGB());
				RGB newRGB = colorDialog.open();
				if (newRGB == null)
					return;
				Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) tableItem.getData();
				Color fg = token.getValue().getForeground();
				Color bg = token.getValue().getBackground();
				if (index == 1)
				{
					fg = ThemePlugin.getDefault().getColorManager().getColor(newRGB);
				}
				else
				{
					bg = ThemePlugin.getDefault().getColorManager().getColor(newRGB);
				}

				TextAttribute at = new TextAttribute(fg, bg, token.getValue().getStyle(), token.getValue().getFont());
				getTheme().update(token.getKey(), at);
				setTheme(fSelectedTheme);
			}
		});
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	public boolean performOk()
	{
		getThemeManager().setCurrentTheme(getTheme());
		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		try
		{
			Theme theme = getTheme();
			theme.loadFromDefaults();
			setTheme(fSelectedTheme);
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
		super.performDefaults();
	}

	protected Theme getTheme()
	{
		return getThemeManager().getTheme(fSelectedTheme);
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
		super.dispose();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	@SuppressWarnings("unchecked")
	@Override
	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fInvasiveThemeCheckbox)
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
			prefs.putBoolean(IPreferenceConstants.INVASIVE_THEMES, fInvasiveThemeCheckbox.getSelection());
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e1)
			{
				ThemePlugin.logError(e1);
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
					Messages.ThemePreferencePage_NewThemeMsg, MessageFormat.format(
							Messages.ThemePreferencePage_NewThemeDefaultName, fSelectedTheme), this);
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
					Messages.ThemePreferencePage_RenameThemeMsg, fSelectedTheme, this);
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
			boolean ok = MessageDialog.openConfirm(getShell(), MessageFormat.format(
					Messages.ThemePreferencePage_DeleteThemeTitle, fSelectedTheme), MessageFormat.format(
					Messages.ThemePreferencePage_DeleteThemeMsg, fSelectedTheme));
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

			try
			{
				Theme theme = new TextmateImporter().convert(themeFile);
				getThemeManager().addTheme(theme);
				getThemeManager().setCurrentTheme(theme);
				loadThemeNames();
				setTheme(theme.getName());
			}
			catch (FileNotFoundException e1)
			{
				ThemePlugin.logError(e1);
			}
		}
		else if (source == fExportButton)
		{
			DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.SAVE);
			IDialogSettings editorSettings = ThemePlugin.getDefault().getDialogSettings();
			String value = editorSettings.get(THEME_EXPORT_DIRECTORY);
			if (value != null)
			{
				fileDialog.setFilterPath(value);
			}
			else
			{
				fileDialog.setFilterPath(BundleManager.getInstance().getUserBundlesPath());
			}
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
			String newName = "newToken"; //$NON-NLS-1$
			theme.addNewDefaultToken(newName);
			setTheme(fSelectedTheme);
			// Select the new token!
			TableItem[] items = tableViewer.getTable().getItems();
			int i = 0;
			for (TableItem tableItem : items)
			{
				Map.Entry<String, TextAttribute> entry = (Map.Entry<String, TextAttribute>) tableItem.getData();
				if (entry.getKey().equals(newName))
				{
					break;
				}
				i++;
			}
			// Have the new addition in an edit mode
			Object newElement = tableViewer.getElementAt(i);
			if (newElement != null)
			{
				tableViewer.editElement(newElement, 0);
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
				Map.Entry<String, TextAttribute> entry = (Map.Entry<String, TextAttribute>) tableItem.getData();
				theme.remove(entry.getKey());
			}
			theme.save();
			setTheme(fSelectedTheme);
		}
		else if (source == tableViewer.getTable())
		{
			TableItem item = (TableItem) e.item;
			Map.Entry<String, TextAttribute> token = (Map.Entry<String, TextAttribute>) item.getData();
			fScopeText.setText(token.getKey());
		}
	}

	@Override
	public String isValid(String newText)
	{
		IStatus status = getThemeManager().validateThemeName(newText);
		if (status.isOK())
		{
			return null;
		}
		return status.getMessage();
	}

	@Override
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
}
