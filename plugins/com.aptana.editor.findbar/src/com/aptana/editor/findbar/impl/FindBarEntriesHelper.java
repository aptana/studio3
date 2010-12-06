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
package com.aptana.editor.findbar.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.findbar.FindBarPlugin;

/**
 * Helper class to deal with the entries for find and replace in the preferences.
 */
public class FindBarEntriesHelper
{

	public static Properties createPropertiesFromString(String asPortableString)
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

	public static String createStringFromProperties(Properties properties)
	{
		OutputStream out = new ByteArrayOutputStream();
		try
		{
			properties.store(out, ""); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return out.toString();
	}

	public static Properties createPropertiesFromList(List<String> list)
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
	public static List<String> createListFromProperties(Properties properties)
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

	public static List<String> addEntry(String entry, String preferenceName)
	{
		synchronized (lock)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();

			List<String> items = loadEntries(preferenceName);
			items.remove(entry); // Remove it if it already existed
			items.add(0, entry); // And always add it as the first
			while (items.size() > 50)
			{ // Hold at most 50 entries in the cache
				items.remove(items.size() - 1); // remove the last
			}
			Properties props = FindBarEntriesHelper.createPropertiesFromList(items);
			preferenceStore.setValue(preferenceName, FindBarEntriesHelper.createStringFromProperties(props));
			return items;
		}
	}

	public static List<String> loadEntries(String preferenceName)
	{
		synchronized (lock)
		{
			IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
			String current = preferenceStore.getString(preferenceName);
			if (current.trim().length() > 0)
			{
				Properties props = FindBarEntriesHelper.createPropertiesFromString(current);
				return FindBarEntriesHelper.createListFromProperties(props);
			}
			return new ArrayList<String>();
		}
	}

}
