package com.aptana.ide.syncing.ui.old.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SearchComposite extends Composite
{

	public static interface Client
	{
		public void search(String text, boolean isCaseSensitive, boolean isRegularExpression);
	}

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
		searchGridLayout.marginWidth = 10;
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
				searchText();
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (!e.doit)
				{
					return;
				}
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
		if (client != null)
		{
			client.search(text, caseSensitiveSearch, regularExpressionSearch);
		}
	}
}
