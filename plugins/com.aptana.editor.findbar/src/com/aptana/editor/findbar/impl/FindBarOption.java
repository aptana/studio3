/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.editor.findbar.FindBarPlugin;

/**
 * Option for the find bar (i.e.: case sensitive, whole word, etc).
 * 
 * @author Fabio Zadrozny
 */
public abstract class FindBarOption extends SelectionAdapter implements SelectionListener
{
	/**
	 * The image to be gotten for the item.
	 */
	public final String image;

	/**
	 * The image to show when it's disabled (may be null).
	 */
	public final String imageDisabled;

	/**
	 * The initial text for the item (later on, the actual tooltip that'll use this text may have more information on
	 * the keybindings)
	 */
	public final String initialText;

	/**
	 * What's the name of the field set in the FindBarDecorator?
	 */
	public final String fieldName;

	/**
	 * Weak reference to our container.
	 */
	public final WeakReference<FindBarDecorator> findBarDecorator;

	/**
	 * Is the item initially enabled?
	 */
	public final boolean initiallyEnabled;

	/**
	 * Is it a check button (or just a push button)?
	 */
	public boolean isCheckable = true;

	/**
	 * Should it appear as a menu item?
	 */
	public boolean createMenuItem = true;

	public FindBarOption(String fieldName, String image, String imageDisabled, String initialText,
			FindBarDecorator findBarDecorator, boolean initiallyEnabled)
	{
		this.fieldName = fieldName;
		this.image = image;
		this.imageDisabled = imageDisabled;
		this.initialText = initialText;
		this.findBarDecorator = new WeakReference<FindBarDecorator>(findBarDecorator);
		this.initiallyEnabled = initiallyEnabled;
	}

	public FindBarOption(String fieldName, String image, String imageDisabled, String initialText,
			FindBarDecorator findBarDecorator)
	{
		this(fieldName, image, imageDisabled, initialText, findBarDecorator, true);
	}

	public void execute()
	{
		FindBarDecorator dec = this.findBarDecorator.get();
		if (dec != null)
		{
			execute(dec);
		}
	}

	public void widgetSelected(SelectionEvent e)
	{
		execute();
	}

	/**
	 * Subclasses must override to execute the action related to it.
	 */
	public abstract void execute(FindBarDecorator dec);

	/**
	 * Creates a menu item for this option (for a pop-up menu).
	 */
	public MenuItem createMenuItem(Menu menu)
	{
		if (!createMenuItem)
		{
			return null;
		}
		final ToolItem toolItem = (ToolItem) getToolItemFromDecorator();
		MenuItem item;
		if (isCheckable)
		{
			item = new MenuItem(menu, SWT.CHECK);
			item.setSelection(toolItem.getSelection());
		}
		else
		{
			item = new MenuItem(menu, SWT.PUSH);
		}
		item.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (isCheckable)
				{
					toolItem.setSelection(!toolItem.getSelection());
				}
				else
				{
					execute();
				}
			}
		});
		item.setImage(FindBarPlugin.getImage(this.image));
		item.setText("  " + toolItem.getToolTipText()); //$NON-NLS-1$
		return item;
	}

	public ToolItem createToolItem(ToolBar optionsToolBar)
	{
		if (!canCreateItem())
		{
			return null;
		}
		ToolItem item = isCheckable ? new ToolItem(optionsToolBar, SWT.CHECK) : new ToolItem(optionsToolBar, SWT.PUSH);

		item.setImage(FindBarPlugin.getImage(this.image));
		if (imageDisabled != null)
		{
			item.setDisabledImage(FindBarPlugin.getImage(this.imageDisabled));
		}
		item.setToolTipText(this.initialText);
		item.addSelectionListener(this);
		if (!this.initiallyEnabled)
		{
			item.setEnabled(false);
		}
		setToolItemInDecorator(item);
		return item;
	}

	private void setToolItemInDecorator(ToolItem value)
	{
		try
		{
			Field field = getField();
			FindBarDecorator dec = findBarDecorator.get();
			field.set(dec, value);
		}
		catch (Exception e)
		{
			FindBarPlugin.log(e);
			throw new RuntimeException(e); // should never really happen!
		}
	}

	private ToolItem getToolItemFromDecorator()
	{
		try
		{
			Field field = getField();
			FindBarDecorator dec = findBarDecorator.get();
			return (ToolItem) field.get(dec);
		}
		catch (Exception e)
		{
			FindBarPlugin.log(e);
			throw new RuntimeException(e); // should never really happen!
		}
	}

	/**
	 * Gets the class field for this option.
	 */
	private Field getField()
	{
		try
		{
			FindBarDecorator dec = findBarDecorator.get();
			return dec.getClass().getDeclaredField(this.fieldName);
		}
		catch (Exception e)
		{
			FindBarPlugin.log(e);
			throw new RuntimeException(e); // should never really happen!
		}
	}

	/**
	 * Subclasses may override to define if the item should be created.
	 */
	protected boolean canCreateItem()
	{
		return true;
	}

}
