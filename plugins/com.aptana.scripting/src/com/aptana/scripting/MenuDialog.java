package com.aptana.scripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
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

	private static final String TITLE = "title"; //$NON-NLS-1$
	private static final String SEPARATOR = "separator"; //$NON-NLS-1$
	private static final String IMAGE = "image"; //$NON-NLS-1$

	@SuppressWarnings("unchecked")
	private List<Map> menuItems;
	private Table completionsTable;

	@SuppressWarnings("unchecked")
	public MenuDialog(Shell parent, Map... menuItems)
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
	@SuppressWarnings("unchecked")
	private final void createTableDialogArea(final Composite parent, final List<Map> partialMatches)
	{
		// Layout the table.
		completionsTable = new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE);
		final GridData gridData = new GridData(GridData.FILL_BOTH);
		completionsTable.setLayoutData(gridData);
		completionsTable.setBackground(parent.getBackground());
		completionsTable.setLinesVisible(true);

		List<TableColumn> columns = new ArrayList<TableColumn>();

		// Initialize the columns and rows.
		Map rep = partialMatches.iterator().next();
		if (rep.containsKey(TITLE))
		{
			// just a list
			columns.add(new TableColumn(completionsTable, SWT.LEFT, 0));
			for (Map map : partialMatches)
			{
				final TableItem item = new TableItem(completionsTable, SWT.NULL);
				if (map.containsKey(SEPARATOR))
				{
					// TODO Insert a separator
					continue;
				}
				item.setText((String) map.get(TITLE));
			}
		}
		else
		{
			// image, display, insert, tool_tip
			columns.add(new TableColumn(completionsTable, SWT.LEFT, 0));
			columns.add(new TableColumn(completionsTable, SWT.LEFT, 1));
			for (Map map : partialMatches)
			{
				final TableItem item = new TableItem(completionsTable, SWT.NULL);
				if (map.containsKey(SEPARATOR))
				{
					// TODO Insert a separator
					continue;
				}
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
			}
		}

		Dialog.applyDialogFont(parent);
		for (TableColumn tableColumn : columns)
		{
			tableColumn.pack();
		}

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
