/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * The ImagePopup class represents a selectable user interface object that combines a text field and a menu and issues
 * notification when an item is selected from the table.
 */
@SuppressWarnings("restriction")
public final class ImagePopup extends Composite implements ISelectableWidget
{

	private static final boolean gtk = "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$

	int visibleItemCount = 5;
	Shell popup;
	Button arrow;
	boolean hasFocus;
	Listener listener, filter;
	Color foreground, background;
	Font font;

	private Composite serverComposite;
	private Label popupText;
	private ToolBar toolBar;
	private ToolItem toolItem;
	private Menu menu;
	private Image popupImage;
	private MenuItem selectedItem;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its behavior and
	 * appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
	 * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description
	 * lists the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot be null)
	 * @param style
	 *            the style of widget to construct
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                </ul>
	 * @see SWT#BORDER
	 * @see SWT#READ_ONLY
	 * @see SWT#FLAT
	 * @see Widget#getStyle()
	 */
	public ImagePopup(final Composite parent, int style)
	{
		super(parent, style = checkStyle(style));

		final Composite inner = new Composite(this, SWT.BORDER);
		final Composite composite = this;

		GridLayout iLayout = new GridLayout(1, true);
		if(!Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			iLayout.marginWidth = 0;
		}
		iLayout.marginHeight = 0;
		
		this.setLayout(iLayout);

		MouseAdapter listener = new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{
				if (toolBar.isEnabled() && popupText.isEnabled())
				{
					Rectangle rect = inner.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = composite.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			}

		};

		inner.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		iLayout = new GridLayout(3, false);
		iLayout.marginHeight = 0;
		iLayout.marginWidth = 0;
		GridData iData = new GridData(SWT.FILL, SWT.FILL, true, true);
		iData.widthHint = 300;
		inner.setLayout(iLayout);
		inner.setLayoutData(iData);
		inner.addMouseListener(listener);

		serverComposite = new Composite(inner, SWT.NONE);
		serverComposite.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		serverComposite.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (popupImage != null)
				{
					e.gc.drawImage(popupImage, 2, 2);
				}
			}
		});

		serverComposite.addMouseListener(listener);
		GridData siData = new GridData(SWT.FILL, SWT.FILL, false, false);
		siData.heightHint = 16;
		siData.widthHint = 20;
		serverComposite.setLayoutData(siData);
		popupText = new Label(inner, SWT.LEFT);
		popupText.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		popupText.addMouseListener(listener);
		GridData stData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		popupText.setLayoutData(stData);
		toolBar = new ToolBar(inner, SWT.FLAT);
		toolBar.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout sbLayout = new GridLayout(1, false);
		sbLayout.marginHeight = 0;
		sbLayout.marginWidth = 0;
		sbLayout.horizontalSpacing = 0;
		toolBar.setLayout(sbLayout);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		Image arrow = WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_LCL_RENDERED_VIEW_MENU);
		toolItem.setImage(arrow);
		menu = new Menu(parent);

		toolItem.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = inner.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = composite.toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}

		});
	}

	static int checkStyle(int style)
	{
		int mask = gtk ? SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT : SWT.BORDER | SWT.READ_ONLY
				| SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		return style & mask;
	}

	/**
	 * Adds the argument to the end of the receiver's list.
	 * 
	 * @param string
	 *            the new item
	 * @param image
	 *            the new item image
	 * @param data
	 *            the data for the item
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 * @see #add(String,int)
	 */
	public void add(String string, Image image, Object data)
	{
		checkWidget();
		if (string == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		MenuItem newItem = new MenuItem(menu, SWT.PUSH);
		newItem.setData(data);
		newItem.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				selectedItem = (MenuItem) e.widget;
				updateText(selectedItem);
				notifyListeners(SWT.DefaultSelection, createEventFromSelectionEvent(e));
			}

			public void widgetSelected(SelectionEvent e)
			{
				selectedItem = (MenuItem) e.widget;
				updateText(selectedItem);
				notifyListeners(SWT.Selection, createEventFromSelectionEvent(e));
			}

		});
		newItem.setText(string);
		if (image != null)
			newItem.setImage(image);
	}

	/**
	 * Adds the argument to the receiver's list at the given zero-relative index.
	 * <p>
	 * Note: To add an item at the end of the list, use the result of calling <code>getItemCount()</code> as the index
	 * or use <code>add(String)</code>.
	 * </p>
	 * 
	 * @param string
	 *            the new item
	 * @param image
	 *            the new item image
	 * @param index
	 *            the index for the item
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list
	 *                (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 * @see #add(String)
	 */
	public void add(String string, Image image, int index)
	{
		add(string, image, null, index);
	}
	
	/**
	 * Adds the argument to the receiver's list at the given zero-relative index.
	 * <p>
	 * Note: To add an item at the end of the list, use the result of calling <code>getItemCount()</code> as the index
	 * or use <code>add(String)</code>.
	 * </p>
	 * 
	 * @param string
	 *            the new item
	 * @param image
	 *            the new item image
	 * @param data
	 *            the data for the item
	 * @param index
	 *            the index for the item
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list
	 *                (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 * @see #add(String)
	 */
	public void add(String string, Image image, Object data, int index)
	{
		checkWidget();
		if (string == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		MenuItem newItem = new MenuItem(menu, SWT.PUSH, index);
		newItem.setData(data);
		newItem.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
				selectedItem = (MenuItem) e.widget;
				updateText(selectedItem);
				notifyListeners(SWT.DefaultSelection, createEventFromSelectionEvent(e));
			}

			public void widgetSelected(SelectionEvent e)
			{
				selectedItem = (MenuItem) e.widget;
				updateText(selectedItem);
				notifyListeners(SWT.Selection, createEventFromSelectionEvent(e));
			}

		});
		if (image != null)
			newItem.setImage(image);
	}

	private Event createEventFromSelectionEvent(SelectionEvent e)
	{
		Event ev = new Event();
		ev.data = e.data;
		ev.widget = e.widget;
		ev.item = e.item;
		return ev;
	}

	/**
	 * Update the text
	 * 
	 * @param item
	 */
	private void updateText(MenuItem item)
	{
		popupText.setText(item.getText());
		popupImage = item.getImage();
		serverComposite.redraw();
		serverComposite.update();
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's selection changes, by
	 * sending it one of the messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the combo's list selection changes.
	 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
	 * </p>
	 * 
	 * @param listener
	 *            the listener which should be notified
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener)
	{
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	/**
	 * Returns the text of the control
	 */
	public String getText()
	{
		return popupText.getText();
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until an item is found that is equal to the
	 * argument, and returns the index of that item. If no item is found, returns -1.
	 * 
	 * @param string
	 *            the search item
	 * @return the index of the item
	 */
	public int indexOf(String string)
	{
		MenuItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++)
		{
			MenuItem menuItem = items[i];
			if (menuItem.getText().equals(string))
			{
				return menu.indexOf(menuItem);
			}
		}

		return -1;
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver's list. If the item at the index was already
	 * selected, it remains selected. Indices that are out of range are ignored.
	 * 
	 * @param index
	 *            the index of the item to select
	 */
	public void select(int selectionIndex)
	{
		MenuItem index = menu.getItem(selectionIndex);
		menu.setDefaultItem(index);
		selectedItem = index;
		updateText(index);
	}

	/**
	 * Sets the number of items that are visible in the drop down portion of the receiver's list.
	 * <p>
	 * Unsupported
	 * </p>
	 */
	public void setVisibleItemCount(int i)
	{
		// Can't set item count
	}

	/**
	 * Returns the zero-relative index of the item which is currently selected in the receiver's list, or -1 if no item
	 * is selected.
	 * 
	 * @return the index of the selected item
	 */
	public int getSelectionIndex()
	{
		if (selectedItem != null)
		{
			return menu.indexOf(selectedItem);
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver's list. Throws an exception if the index is
	 * out of range.
	 * 
	 * @param index
	 *            the index of the item to return
	 * @return the item at the given index
	 */
	public PopupItem getItem(int index)
	{
		MenuItem mi = menu.getItem(index);
		if (mi != null)
		{
			PopupItem item = new PopupItem(mi.getText(), mi.getImage(), mi.getData());
			return item;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Removes all of the items from the receiver's list and clear the contents of receiver's text field.
	 * <p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void removeAll()
	{
		MenuItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++)
		{
			MenuItem menuItem = items[i];
			menuItem.dispose();
		}
	}

	/**
	 * Notifies the listeners in the event of a modification.
	 * 
	 * Not implemented, as the text cannot be modified.
	 */
	public void addModifyListener(ModifyListener ml)
	{
		// TODO Auto-generated method stub
	}
}
