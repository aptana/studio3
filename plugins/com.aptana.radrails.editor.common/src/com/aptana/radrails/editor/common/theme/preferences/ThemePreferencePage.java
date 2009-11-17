package com.aptana.radrails.editor.common.theme.preferences;

import java.util.Set;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.radrails.editor.common.theme.Theme;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class ThemePreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	protected String fSelectedTheme;
	private ColorSelector fgSelector;
	private ColorSelector bgSelector;
	private ColorSelector lineHighlightSelector;
	private ColorSelector selectionSelector;
	private Combo fThemeCombo;

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true));

		// Group group = new Group(composite, SWT.SHADOW_OUT);
		// group.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true));

		fThemeCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		Set<String> themeNames = ThemeUtil.getThemeNames();
		for (String themeName : themeNames)
		{
			fThemeCombo.add(themeName);
		}

		fThemeCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setTheme(fThemeCombo.getText());
				super.widgetSelected(e);
			}
		});

		Composite colors = new Composite(composite, SWT.NONE);
		colors.setLayout(new GridLayout(4, false));

		Label label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_ForegroundLabel);
		fgSelector = new ColorSelector(colors);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_SelectionLabel);
		selectionSelector = new ColorSelector(colors);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_BackgroundLabel);
		bgSelector = new ColorSelector(colors);

		label = new Label(colors, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText(Messages.ThemePreferencePage_LineHighlightLabel);
		lineHighlightSelector = new ColorSelector(colors);

		setTheme(ThemeUtil.getActiveTheme().getName());

		return composite;
	}

	protected void setTheme(String themeName)
	{
		fSelectedTheme = themeName;
		Theme theme = ThemeUtil.getTheme(themeName);
		fgSelector.setColorValue(theme.getForeground());
		bgSelector.setColorValue(theme.getBackground());
		lineHighlightSelector.setColorValue(theme.getLineHighlight());
		selectionSelector.setColorValue(theme.getSelection());
		fThemeCombo.setText(themeName);
	}

	public void init(IWorkbench workbench)
	{

	}

	@Override
	public boolean performOk()
	{
		ThemeUtil.setActiveTheme(ThemeUtil.getTheme(fSelectedTheme));
		return super.performOk();
	}

}
