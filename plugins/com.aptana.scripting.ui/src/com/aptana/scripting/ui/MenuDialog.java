package com.aptana.scripting.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class MenuDialog extends PopupDialog
{
	private static final String MNEMONICS = "123456789"; //$NON-NLS-1$

	private static final String TITLE = "title"; //$NON-NLS-1$
	private static final String SEPARATOR = "separator"; //$NON-NLS-1$
	private static final String IMAGE = "image"; //$NON-NLS-1$

	private List<Map<String, Object>> menuItems;
	private Table completionsTable;

	public MenuDialog(Shell parent, Map<String, Object>... menuItems)
	{
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, null, null);
		this.menuItems = Arrays.asList(menuItems);
	}

	/**
	 * Creates the content area for the key assistant. This creates a table and places it inside the composite. The
	 * composite will contain a list of all the key bindings.
	 * 
	 * @param parent
	 *            The parent composite to contain the dialog area; must not be <code>null</code>.
	 */
	protected final Control createDialogArea(final Composite parent)
	{
		// Create a composite for the dialog area.
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginHeight = 0;
		compositeLayout.marginWidth = 0;
		composite.setLayout(compositeLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setBackground(parent.getBackground());

		if (menuItems.isEmpty())
		{
			createEmptyDialogArea(composite);
		}
		else
		{
			createTableDialogArea(composite, menuItems);
		}
		return composite;
	}

	/**
	 * Creates an empty dialog area with a simple message saying there were no matches. This is used if no partial
	 * matches could be found. This should not really ever happen, but might be possible if the commands are changing
	 * while waiting for this dialog to open.
	 * 
	 * @param parent
	 *            The parent composite for the dialog area; must not be <code>null</code>.
	 */
	private final void createEmptyDialogArea(final Composite parent)
	{
		final Label noMatchesLabel = new Label(parent, SWT.NULL);
		noMatchesLabel.setText(Messages.MenuDialog_NoMatchesFound);
		noMatchesLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		noMatchesLabel.setBackground(parent.getBackground());
	}

	/**
	 * Creates a dialog area with a table of the partial matches for the current key binding state. The table will be
	 * either the minimum width, or <code>previousWidth</code> if it is not <code>NO_REMEMBERED_WIDTH</code>.
	 * 
	 * @param parent
	 *            The parent composite for the dialog area; must not be <code>null</code>.
	 * @param partialMatches
	 *            The lexicographically sorted map of partial matches for the current state; must not be
	 *            <code>null</code> or empty.
	 */
	private final void createTableDialogArea(final Composite parent, final List<Map<String, Object>> partialMatches)
	{
		// TODO Need to add accelerator 0-9 keys like Textmate does and our own keybinding hook does!
		// Layout the table.
		completionsTable = new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE);
		final GridData gridData = new GridData(GridData.FILL_BOTH);
		completionsTable.setLayoutData(gridData);
		completionsTable.setBackground(parent.getBackground());
		completionsTable.setLinesVisible(true);

		List<TableColumn> columns = new ArrayList<TableColumn>();

		// Initialize the columns and rows.
		Map<String, Object> rep = partialMatches.iterator().next();
		int mnemonic = 0;
		int index = -1;
		if (rep.containsKey(TITLE))
		{
			// just a list
			columns.add(new TableColumn(completionsTable, SWT.LEFT, 0));
			columns.add(new TableColumn(completionsTable, SWT.CENTER, 1));

			for (Map<String, Object> map : partialMatches)
			{
				index++;
				if (map.containsKey(SEPARATOR))
				{
					// TODO Insert a separator
					continue;
				}
				String title = (String) map.get(TITLE);
				if (title.trim().equals("---")) //$NON-NLS-1$
				{
					// TODO Insert a separator
					continue;
				}
				final TableItem item = new TableItem(completionsTable, SWT.NULL);
				item.setText(0, title);
				item.setData("mnemonic", mnemonic); //$NON-NLS-1$
				item.setText(1, mnemonic < MNEMONICS.length() ? String.valueOf(MNEMONICS.charAt(mnemonic++)) : ""); //$NON-NLS-1$
				item.setData("index", index); //$NON-NLS-1$
			}
		}
		else
		{
			// image, display, insert, tool_tip
			columns.add(new TableColumn(completionsTable, SWT.LEFT, 0));
			columns.add(new TableColumn(completionsTable, SWT.LEFT, 1));
			columns.add(new TableColumn(completionsTable, SWT.CENTER, 2));
			for (Map<String, Object> map : partialMatches)
			{
				index++;
				if (map.containsKey(SEPARATOR))
				{
					// TODO Insert a separator
					continue;
				}
				final TableItem item = new TableItem(completionsTable, SWT.NULL);
				String filename = (String) map.get(IMAGE);
				Image image = null;
				if (filename != null && filename.trim().length() > 0)
				{
					try
					{
						image = new Image(Display.getCurrent(), filename);

					}
					catch (Exception e)
					{
						// TODO Log?
					}
				}
				if (image != null)
				{
					// TODO Listen for disposal and dispose of these images
					item.setImage(0, image);
				}

				item.setText(1, (String) map.get("display")); //$NON-NLS-1$
				item.setData("mnemonic", mnemonic); //$NON-NLS-1$ // FIXME This is really off by one, but we expect it to be later. Funky code from Sandip. Juts use real value maybe?
				item.setText(2, (mnemonic < MNEMONICS.length() ? String.valueOf(MNEMONICS.charAt(mnemonic++)) : "")); //$NON-NLS-1$
				item.setData("index", index); //$NON-NLS-1$
			}
		}

		Dialog.applyDialogFont(parent);
		for (TableColumn tableColumn : columns)
		{
			tableColumn.pack();
		}
		// FIXME Need to limit vertical size of list! If we have 100 items we shouldn't let this dialog grow to take up
		// entire vertical space of screen/IDE!

		/*
		 * If you double-click on the table, it should execute the selected command.
		 */
		completionsTable.addListener(SWT.DefaultSelection, new Listener()
		{
			public final void handleEvent(final Event event)
			{
				select();
			}
		});

		completionsTable.addKeyListener(new KeyListener()
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
				int index = MNEMONICS.indexOf(e.character);
				if (index != -1)
				{
					if (index < completionsTable.getItemCount())
					{
						e.doit = false;
						// I need to return the index of the item as it was in partialMatches!
						int returnCode = index;
						for (TableItem item : completionsTable.getItems())
						{
							Object data = item.getData("mnemonic"); //$NON-NLS-1$
							if (data instanceof Integer)
							{
								Integer value = (Integer) data;
								if (value == index) // does mnemonic match?
								{
									// OK We found the table item that was assigned this mnemonic, now we need to find
									// it's index in partialMatches!
									returnCode = (Integer) item.getData("index"); //$NON-NLS-1$
									break;
								}
							}
						}
						setReturnCode(returnCode);
						close();
					}
				}
			}
		});
	}

	protected void select()
	{
		int index = completionsTable.getSelectionIndex();
		setReturnCode(index);
		close();
	}

	@Override
	public int open()
	{
		setBlockOnOpen(true);
		super.open();

		// run the event loop if specified
		runEventLoop(getShell());

		return getReturnCode();
	}

	/**
	 * Runs the event loop for the given shell.
	 * 
	 * @param loopShell
	 *            the shell
	 */
	private void runEventLoop(Shell loopShell)
	{
		// Use the display provided by the shell if possible
		Display display = loopShell.getDisplay();
		while (!loopShell.isDisposed())
		{
			try
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
			catch (Throwable e)
			{
				// FIXME Handle exception in some way
				// exceptionHandler.handleException(e);
			}
		}
		if (!display.isDisposed())
			display.update();
	}
}
