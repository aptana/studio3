/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import java.util.regex.Pattern;

import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

@SuppressWarnings("restriction")
public class SearchComposite extends Composite
{

	public static interface Client
	{
		public void search(String text, boolean isCaseSensitive, boolean isRegularExpression);
	}

	private static final String CASE_SENSITIVE_ICON_PATH = "icons/full/elcl16/casesensitive.png"; //$NON-NLS-1$
	private static final String REGULAR_EXPRESSION_ICON_PATH = "icons/full/elcl16/regularexpression.png"; //$NON-NLS-1$
	private static final String INITIAL_TEXT = Messages.SingleProjectView_InitialFileFilterText;

	private Text searchText;
	private ToolItem caseSensitiveMenuItem;
	private ToolItem regularExressionMenuItem;
	private boolean searchOnEnter = true;
	private String initialText = INITIAL_TEXT;
	private String lastSearch = StringUtil.EMPTY;
	private boolean lastCaseSensitiveState;
	private boolean lastRegularExpressionState;

	private Client client;

	public SearchComposite(Composite parent, Client client)
	{
		this(parent, SWT.NONE, client);
	}

	public SearchComposite(Composite parent, int style, Client client)
	{
		this(parent, style, true, client);
	}

	public SearchComposite(Composite parent, int style, boolean showSearchIcon, Client client)
	{
		super(parent, style);
		this.client = client;

		GridLayout searchGridLayout = new GridLayout(2, false);
		searchGridLayout.marginWidth = 2;
		searchGridLayout.marginHeight = 0;
		setLayout(searchGridLayout);

		int searchTextStyle = SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL;
		if (showSearchIcon)
		{
			searchTextStyle |= SWT.ICON_SEARCH;
		}

		searchText = new Text(this, searchTextStyle);
		searchText.setText(initialText);
		searchText.setToolTipText(Messages.SingleProjectView_Wildcard);
		searchText.setForeground(searchText.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		layoutData.widthHint = 225;
		searchText.setLayoutData(layoutData);

		searchText.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				if (searchText.getText().length() == 0)
				{
					searchText.setText(initialText);
				}
				searchText.setForeground(searchText.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
			}

			public void focusGained(FocusEvent e)
			{
				if (searchText.getText().equals(initialText))
				{
					searchText.setText(""); //$NON-NLS-1$
				}
				searchText.setForeground(null);
			}
		});

		searchText.addKeyListener(new KeyListener()
		{
			public void keyReleased(KeyEvent e)
			{
			}

			public void keyPressed(KeyEvent e)
			{
				if (!e.doit)
				{
					return;
				}
				if (searchOnEnter && e.character == '\r')
				{
					searchText();
					e.doit = false;
				}
			}
		});
		searchText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!searchOnEnter) {
					searchText();
				}
			}
		});

		// Button for search options
		ToolBar toolbar = new ToolBar(this, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		caseSensitiveMenuItem = new ToolItem(toolbar, SWT.CHECK);
		caseSensitiveMenuItem.setImage(UIPlugin.getImage(CASE_SENSITIVE_ICON_PATH));
		caseSensitiveMenuItem.setToolTipText(Messages.SingleProjectView_CaseSensitive);
		caseSensitiveMenuItem.setSelection(lastCaseSensitiveState);
		caseSensitiveMenuItem.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				searchText.setFocus();
				if (!searchOnEnter) {
					searchText();
				}
			}
		});

		regularExressionMenuItem = new ToolItem(toolbar, SWT.CHECK);
		regularExressionMenuItem.setImage(UIPlugin.getImage(REGULAR_EXPRESSION_ICON_PATH));
		regularExressionMenuItem.setToolTipText(Messages.SingleProjectView_RegularExpression);
		regularExressionMenuItem.setSelection(lastRegularExpressionState);
		regularExressionMenuItem.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				searchText.setFocus();
				if (!searchOnEnter) {
					searchText();
				}
			}
		});
	}

	/**
	 * @param searchOnEnter the searchOnEnter to set
	 */
	public void setSearchOnEnter(boolean searchOnEnter) {
		this.searchOnEnter = searchOnEnter;
	}

	/**
	 * @param initialText the initialText to set
	 */
	public void setInitialText(String initialText) {
		if (searchText.getText().equals(this.initialText)) {
			this.initialText = initialText;
			searchText.setText(initialText);
		}
		this.initialText = initialText;
	}

	@Override
	public boolean setFocus()
	{
		return searchText.setFocus();
	}

	public Text getTextControl()
	{
		return searchText;
	}

	private void searchText()
	{
		String text = searchText.getText();
		if (initialText.equals(text)) {
			text = StringUtil.EMPTY;
		}
		if (client != null
				&& (searchOnEnter
						|| !text.equals(lastSearch)
						|| (caseSensitiveMenuItem.getSelection() != lastCaseSensitiveState)
						|| (regularExressionMenuItem.getSelection() != lastRegularExpressionState)))
		{
			lastSearch = text;
			lastCaseSensitiveState = caseSensitiveMenuItem.getSelection();
			lastRegularExpressionState = regularExressionMenuItem.getSelection();
			client.search(text, lastCaseSensitiveState, lastRegularExpressionState);
		}
	}

	/**
	 * Create a default search pattern taking into consideration case sensitivity and regular expression settings
	 * 
	 * @return
	 */
	public Pattern createSearchPattern()
	{
		return PatternConstructor.createPattern(searchText.getText(), lastCaseSensitiveState, lastRegularExpressionState);
	}
}
