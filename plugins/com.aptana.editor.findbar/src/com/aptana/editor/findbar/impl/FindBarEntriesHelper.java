/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.findbar.FindBarPlugin;

/**
 * Helper class to deal with the entries for find and replace in the preferences.
 * 
 * @author fabioz
 */
public class FindBarEntriesHelper
{
	/* default */static final String PREFERENCE_NAME_FIND = "FIND_BAR_DECORATOR_FIND_ENTRIES"; //$NON-NLS-1$
	/* default */static final String PREFERENCE_NAME_REPLACE = "FIND_BAR_DECORATOR_REPLACE_ENTRIES"; //$NON-NLS-1$

	/**
	 * Keep the items that we're controlling so that we can update the combo when the preferences change. Note that it's
	 * identity compared/hashed.
	 */
	static class EntriesControlHandle
	{

		final String preferenceName;
		final Text text;
		final IStartEndIgnore modifyListener;

		public EntriesControlHandle(String preferenceName, Text text, IStartEndIgnore modifyListener)
		{
			this.preferenceName = preferenceName;
			this.text = text;
			this.modifyListener = modifyListener;
		}
	}

	/**
	 * Map from the preference name > combos to be updated when the preference changes.
	 */
	private final Map<String, Set<EntriesControlHandle>> preferenceToTextAndListener = new HashMap<String, Set<EntriesControlHandle>>();

	private final EclipseFindSettings eclipseFindSettings;

	public FindBarEntriesHelper(EclipseFindSettings eclipseFindSettings)
	{
		this.eclipseFindSettings = eclipseFindSettings;
	}

	private Properties createPropertiesFromString(String asPortableString)
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new ByteArrayInputStream(asPortableString.getBytes()));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return properties;
	}

	private String createStringFromProperties(Properties properties)
	{
		OutputStream out = new ByteArrayOutputStream();
		try
		{
			properties.store(out, StringUtil.EMPTY);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return out.toString();
	}

	public Properties createPropertiesFromList(List<String> list)
	{
		Properties properties = new Properties();

		for (int i = 0; i < list.size(); i++)
		{
			properties.put(String.valueOf(i), list.get(i));
		}
		return properties;
	}

	/**
	 * Create a list from a property that was previously created from createPropertiesFromList.
	 */
	private List<String> createListFromProperties(Properties properties)
	{
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < properties.size(); i++)
		{
			Object object = properties.get(String.valueOf(i));
			if (object != null)
			{
				list.add(object.toString());
			}
		}
		return list;
	}

	private static final Object lock = new Object();

	/**
	 * When a search is entered, this method should be called to add the searched text to the list of available searches
	 * (and it'll be replicated for all the combos controlled).
	 */
	public void addEntry(String entry, String preferenceName)
	{
		if (entry.length() == 0)
		{
			return; // nothing to do in this case
		}

		synchronized (lock)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();

			List<String> items = loadEntries(preferenceName);
			items.remove(entry); // Remove it if it already existed
			items.add(0, entry); // And always add it as the first
			while (items.size() > 20)
			{ // Hold at most 20 entries in the cache
				items.remove(items.size() - 1); // remove the last
			}
			Properties props = createPropertiesFromList(items);
			preferenceStore.setValue(preferenceName, createStringFromProperties(props));
			if (preferenceName.equals(PREFERENCE_NAME_FIND))
			{
				eclipseFindSettings.addEntry(entry);
			}
		}
	}

	/**
	 * Load the available entries from a given preference name.
	 */
	public List<String> loadEntries(String preferenceName)
	{
		synchronized (lock)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			String current = preferenceStore.getString(preferenceName);
			if (current.trim().length() > 0)
			{
				Properties props = createPropertiesFromString(current);
				List<String> items = createListFromProperties(props);
				while (items.size() > 20)
				{ // Hold at most 20 entries in the cache
					items.remove(items.size() - 1); // remove the last
				}
				return items;
			}
			return new ArrayList<String>();
		}
	}

	/**
	 * Set the items available in the text (and ask it to ignore any changes while that's done).
	 */
	private void setTextText(Text text, IStartEndIgnore modifyListener, List<String> items)
	{
		modifyListener.startIgnore();
		try
		{
			if (!text.isDisposed() && !CollectionsUtil.isEmpty(items))
			{
				if (ObjectUtil.areNotEqual(items.get(0), text.getText()))
				{
					text.setText(items.get(0));
				}
				text.setForeground(null);
			}
		}
		finally
		{
			modifyListener.endIgnore();
		}
	}

	/**
	 * Start taking control of the text (i.e.: when the preference changes, update the text).
	 * 
	 * @return a handle that should be used to later unregister it.
	 */
	public EntriesControlHandle register(Text text, IStartEndIgnore modifyListener, final String preferenceName)
	{
		List<String> items = loadEntries(preferenceName);
		setTextText(text, modifyListener, items);
		Set<EntriesControlHandle> set = preferenceToTextAndListener.get(preferenceName);
		if (set == null)
		{
			set = new HashSet<EntriesControlHandle>();
			preferenceToTextAndListener.put(preferenceName, set);
			// preference that's still not treated: start to hear it.
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			final Set<EntriesControlHandle> usedInternal = set;
			preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
			{

				public void propertyChange(PropertyChangeEvent event)
				{
					if (preferenceName.equals(event.getProperty()))
					{
						List<String> entries = loadEntries(preferenceName);
						for (EntriesControlHandle entry : usedInternal)
						{
							setTextText(entry.text, entry.modifyListener, entries);
						}
					}
				}
			});
		}
		EntriesControlHandle handle = new EntriesControlHandle(preferenceName, text, modifyListener);
		set.add(handle);
		return handle;
	}

	/**
	 * No longer update the given combos (from the handles passed) when the preference changes.
	 */
	public void unregister(List<EntriesControlHandle> entriesControlHandles)
	{
		for (EntriesControlHandle entriesControlHandle : entriesControlHandles)
		{
			Set<EntriesControlHandle> set = preferenceToTextAndListener.get(entriesControlHandle.preferenceName);
			if (set != null)
			{
				set.remove(entriesControlHandle);
			}
		}
	}

	public void updateFromEclipseFindSettings()
	{
		List<String> items = eclipseFindSettings.fFindHistory;
		IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
		Properties props = createPropertiesFromList(items);
		preferenceStore.setValue(PREFERENCE_NAME_FIND, createStringFromProperties(props));
	}

}
