/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.StringUtil;

/**
 * Pop up a quick menu. useful for commands when we want users to be able to select a value without having to take their
 * hands off the keyboard. Much better UI than a dialog with a combo!
 * 
 * @author cwilliams
 */
public class QuickMenuDialog extends PopupDialog
{

	/**
	 * Keys used to set internal values in TableItems data map.
	 */
	private static final String MNEMONIC = "mnemonic"; //$NON-NLS-1$
	private static final String INDEX = "index"; //$NON-NLS-1$
	private static final String IS_SEPARATOR = "isSeparator"; //$NON-NLS-1$

	private static final String MNEMONICS = "123456789"; //$NON-NLS-1$

	private Table fTable;
	private List<MenuDialogItem> menuItems = new ArrayList<MenuDialogItem>();

	public QuickMenuDialog(Shell parent)
	{
		this(parent, null);
	}

	public QuickMenuDialog(Shell parent, String title)
	{
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, title, null);
	}

	public void setInput(List<MenuDialogItem> items)
	{
		this.menuItems = items;
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

	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		Display display = getShell().getDisplay();
		if (display != null && !display.isDisposed())
		{
			return display.getCursorLocation();
		}
		return super.getInitialLocation(initialSize);
	}

	protected Color getBackground()
	{
		// TODO Use themes for colors?
		return getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
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
		noMatchesLabel.setText(Messages.QuickMenuDialog_NoMatchesFound);
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
	private final void createTableDialogArea(final Composite parent, final List<MenuDialogItem> partialMatches)
	{
		// Layout the table.
		fTable = new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE);
		final GridData gridData = new GridData(GridData.FILL_BOTH);
		fTable.setLayoutData(gridData);
		fTable.setBackground(parent.getBackground());
		fTable.setLinesVisible(false);

		List<TableColumn> columns = new ArrayList<TableColumn>();

		// Initialize the columns and rows.
		int mnemonic = 0;
		int index = -1;

		// image, display, insert, tool_tip
		columns.add(new TableColumn(fTable, SWT.LEFT, 0));
		columns.add(new TableColumn(fTable, SWT.LEFT, 1));
		columns.add(new TableColumn(fTable, SWT.CENTER, 2));

		for (MenuDialogItem item : partialMatches)
		{
			index++;
			if (item.isSeparator())
			{
				insertSeparator(3);
				continue;
			}
			final TableItem tableItem = new TableItem(fTable, SWT.NULL);
			Image image = item.getImage();
			if (image != null)
			{
				tableItem.setImage(0, image);
			}

			tableItem.setText(1, item.getText());
			tableItem.setData(MNEMONIC, mnemonic); // FIXME This is really off by one, but we expect it to be later.
													// Funky code from Sandip. Just use real value maybe?
			tableItem.setText(2, (mnemonic < MNEMONICS.length()) ? String.valueOf(MNEMONICS.charAt(mnemonic++))
					: StringUtil.EMPTY);
			tableItem.setData(INDEX, index);
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
		fTable.addListener(SWT.DefaultSelection, new Listener()
		{
			public final void handleEvent(final Event event)
			{
				select();
			}
		});

		fTable.addKeyListener(new KeyListener()
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
					if (index < fTable.getItemCount())
					{
						e.doit = false;
						// I need to return the index of the item as it was in partialMatches!
						int returnCode = index;
						for (TableItem item : fTable.getItems())
						{
							Object data = item.getData(MNEMONIC);
							if (data instanceof Integer)
							{
								Integer value = (Integer) data;
								if (value == index) // does mnemonic match?
								{
									// OK We found the table item that was assigned this mnemonic, now we need to find
									// it's index in partialMatches!
									returnCode = (Integer) item.getData(INDEX);
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

		// Don't ever draw separators as selected!
		fTable.addListener(SWT.EraseItem, new Listener()
		{

			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					TableItem item = (TableItem) event.item;
					if (isSeparator(item))
					{
						event.detail &= ~SWT.SELECTED;
						event.detail &= ~SWT.BACKGROUND;
					}
				}
			}
		});

		fTable.addTraverseListener(new TraverseListener()
		{

			public void keyTraversed(TraverseEvent e)
			{
				int selectionIndex = fTable.getSelectionIndex();
				final int initialIndex = selectionIndex;
				if (e.detail == SWT.TRAVERSE_ARROW_NEXT)
				{
					selectionIndex++;
					while (isSeparator(fTable.getItem(selectionIndex)))
					{
						selectionIndex++;
						if (selectionIndex >= fTable.getItemCount())
							return;
					}
					selectionIndex--;
				}
				else if (e.detail == SWT.TRAVERSE_ARROW_PREVIOUS)
				{
					selectionIndex--;
					while (isSeparator(fTable.getItem(selectionIndex)))
					{
						selectionIndex--;
						if (selectionIndex < 0)
						{
							// HACK have to run this in a job for some reason. Just setting selection index doesn't
							// work.
							new UIJob("retaining selection") //$NON-NLS-1$
							{

								@Override
								public IStatus runInUIThread(IProgressMonitor monitor)
								{
									fTable.setSelection(initialIndex);
									return Status.OK_STATUS;
								}
							}.schedule();
							e.doit = false;
							return;
						}
					}
					selectionIndex++;
				}
				else
				{
					return;
				}

				if (selectionIndex < fTable.getItemCount() && selectionIndex >= 0)
				{
					fTable.setSelection(selectionIndex);
					e.doit = false;
				}
			}
		});
	}

	protected boolean isSeparator(TableItem item)
	{
		Object obj = item.getData(IS_SEPARATOR);
		if (obj instanceof Boolean)
		{
			return (Boolean) obj;
		}
		return false;
	}

	protected void insertSeparator(int columns)
	{
		TableItem item = new TableItem(fTable, SWT.NULL);
		for (int i = 0; i < columns; i++)
		{
			TableEditor editor = new TableEditor(fTable);
			Label label = new Label(fTable, SWT.SEPARATOR | SWT.HORIZONTAL);
			editor.grabHorizontal = true;
			editor.setEditor(label, item, i);
		}
		item.setData(IS_SEPARATOR, true);
	}

	protected void select()
	{
		int index = fTable.getSelectionIndex();
		TableItem item = fTable.getItem(index);
		int returnCode = (Integer) item.getData(INDEX);
		setReturnCode(returnCode);
		close();
	}

	@Override
	public int open()
	{
		setReturnCode(-1); // set return code back to -1
		setBlockOnOpen(true);
		super.open();

		// run the event loop if specified
		runEventLoop(getShell());

		return getReturnCode();
	}

	@Override
	protected void handleShellCloseEvent()
	{
		super.handleShellCloseEvent();
		setReturnCode(-1);
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
