package com.aptana.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.ui.UIPlugin;

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
	private boolean caseSensitiveSearch;
	private boolean regularExpressionSearch;

	private Client client;

	public SearchComposite(Composite parent, Client client)
	{
		this(parent, SWT.NONE, client);
	}

	public SearchComposite(Composite parent, int style, Client client)
	{
		super(parent, style);
		this.client = client;

		GridLayout searchGridLayout = new GridLayout(2, false);
		searchGridLayout.marginWidth = 2;
		searchGridLayout.marginHeight = 0;
		setLayout(searchGridLayout);

		searchText = new Text(this, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		searchText.setText(INITIAL_TEXT);
		searchText.setToolTipText(Messages.SingleProjectView_Wildcard);
		searchText.setForeground(searchText.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		searchText.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				if (searchText.getText().length() == 0)
				{
					searchText.setText(INITIAL_TEXT);
				}
				searchText.setForeground(searchText.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
			}

			@Override
			public void focusGained(FocusEvent e)
			{
				if (searchText.getText().equals(INITIAL_TEXT))
				{
					searchText.setText(""); //$NON-NLS-1$
				}
				searchText.setForeground(null);
			}
		});

		searchText.addKeyListener(new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (!e.doit)
				{
					return;
				}

				if (e.keyCode == 0x0D)
				{
					searchText();
					e.doit = false;
				}
			}
		});

		// Button for search options
		ToolBar toolbar = new ToolBar(this, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		final ToolItem caseSensitiveMenuItem = new ToolItem(toolbar, SWT.CHECK);
		caseSensitiveMenuItem.setImage(UIPlugin.getImage(CASE_SENSITIVE_ICON_PATH));
		caseSensitiveMenuItem.setToolTipText(Messages.SingleProjectView_CaseSensitive);
		caseSensitiveMenuItem.setSelection(caseSensitiveSearch);
		caseSensitiveMenuItem.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				caseSensitiveSearch = caseSensitiveMenuItem.getSelection();
				searchText.setFocus();
			}
		});

		final ToolItem regularExressionMenuItem = new ToolItem(toolbar, SWT.CHECK);
		regularExressionMenuItem.setImage(UIPlugin.getImage(REGULAR_EXPRESSION_ICON_PATH));
		regularExressionMenuItem.setToolTipText(Messages.SingleProjectView_RegularExpression);
		regularExressionMenuItem.setSelection(regularExpressionSearch);
		regularExressionMenuItem.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				regularExpressionSearch = regularExressionMenuItem.getSelection();
				searchText.setFocus();
			}
		});
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
		if (text.length() > 0 && client != null)
		{
			client.search(text, caseSensitiveSearch, regularExpressionSearch);
		}
	}
}
