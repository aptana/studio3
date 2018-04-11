/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.findbar.FindBarPlugin;

/**
 * Option for the find bar (i.e.: case sensitive, whole word, etc).
 * 
 * @author Fabio Zadrozny
 */
abstract class FindBarOption extends SelectionAdapter implements SelectionListener
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
	 * Should it appear as a menu item?
	 */
	boolean createMenuItem = true;

	/**
	 * The key used for it in the Find bar preferences (null means it's not checkable).
	 */
	public final String preferencesKey;

	/**
	 * if > 0, it means that we're updating the option from the preferences (so, no action should take place, only the
	 * gui should be updated).
	 */
	private int internalUpdate = 0;

	/**
	 * Listener to update the gui (check state) when the property changes.
	 */
	private IPropertyChangeListener fPropertyChangeListener;

	/**
	 * Is it a check button (or just a push button)?
	 */
	boolean isCheckable()
	{
		return preferencesKey != null;
	}

	public FindBarOption(String fieldName, String image, String imageDisabled, String initialText,
			FindBarDecorator findBarDecorator, boolean initiallyEnabled, String preferencesKey)
	{
		this.fieldName = fieldName;
		this.image = image;
		this.imageDisabled = imageDisabled;
		this.initialText = initialText;
		this.findBarDecorator = new WeakReference<FindBarDecorator>(findBarDecorator);
		this.initiallyEnabled = initiallyEnabled;
		this.preferencesKey = preferencesKey;
		if (preferencesKey != null)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			preferenceStore.addPropertyChangeListener(getPropertyChangeListener());
		}
	}

	private IPropertyChangeListener getPropertyChangeListener()
	{
		if (fPropertyChangeListener == null)
		{
			fPropertyChangeListener = new IPropertyChangeListener()
			{

				public void propertyChange(PropertyChangeEvent event)
				{
					if (FindBarOption.this.preferencesKey.equals(event.getProperty()))
					{
						ToolItem item = getToolItemFromDecorator();
						if (item != null)
						{
							boolean val = Boolean.parseBoolean(StringUtil.getStringValue(event.getNewValue()));
							if (!item.isDisposed() && val != item.getSelection())
							{
								startInternalUpdate();
								try
								{
									item.setSelection(val);
								}
								finally
								{
									endInternalUpdate();
								}
							}
						}
					}
				}
			};
		}
		return fPropertyChangeListener;
	}

	private void endInternalUpdate()
	{
		internalUpdate -= 1;
	}

	private void startInternalUpdate()
	{
		internalUpdate += 1;
	}

	public FindBarOption(String fieldName, String image, String imageDisabled, String initialText,
			FindBarDecorator findBarDecorator, String preferencesKey)
	{
		this(fieldName, image, imageDisabled, initialText, findBarDecorator, true, preferencesKey);
	}

	private void execute()
	{
		FindBarDecorator dec = this.findBarDecorator.get();
		if (dec != null)
		{
			execute(dec);
		}
	}

	public void widgetSelected(SelectionEvent e)
	{
		if (internalUpdate > 0)
		{
			return; // Updating gui from a change in the preferences.
		}
		if (preferencesKey != null)
		{
			FindBarDecorator.findBarConfiguration.toggle(preferencesKey);
		}
		execute();
	}

	/**
	 * Subclasses must override to execute the action related to it.
	 */
	protected abstract void execute(FindBarDecorator dec);

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
		if (isCheckable())
		{
			item = new MenuItem(menu, SWT.CHECK);
			if (!StringUtil.isEmpty(preferencesKey))
			{
				IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
				item.setSelection(preferenceStore.getBoolean(preferencesKey));
			}
			else
			{
				item.setSelection(toolItem.getSelection());
			}
		}
		else
		{
			item = new MenuItem(menu, SWT.PUSH);
		}
		item.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (isCheckable())
				{
					if (!StringUtil.isEmpty(preferencesKey))
					{
						FindBarDecorator.findBarConfiguration.toggle(preferencesKey);
					}
					// Search Selection is a checkable but does not store the selection
					// in the preferences
					else
					{
						toolItem.setSelection(!(toolItem.getSelection()));
					}
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
		ToolItem item = isCheckable() ? new ToolItem(optionsToolBar, SWT.CHECK)
				: new ToolItem(optionsToolBar, SWT.PUSH);

		item.setImage(FindBarPlugin.getImage(this.image));
		if (imageDisabled != null)
		{
			item.setDisabledImage(FindBarPlugin.getImage(this.imageDisabled));
		}
		item.setToolTipText(this.initialText);
		if (preferencesKey != null)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			item.setSelection(preferenceStore.getBoolean(preferencesKey));
		}
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

	public void dispose()
	{
		if (fPropertyChangeListener != null)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			preferenceStore.removePropertyChangeListener(fPropertyChangeListener);
			fPropertyChangeListener = null;
		}
	}

}
