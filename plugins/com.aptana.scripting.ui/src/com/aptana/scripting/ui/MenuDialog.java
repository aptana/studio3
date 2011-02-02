/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ui.MenuDialogItem;
import com.aptana.ui.QuickMenuDialog;

public class MenuDialog extends QuickMenuDialog
{

	private static final String TITLE = "title"; //$NON-NLS-1$
	private static final String SEPARATOR = "separator"; //$NON-NLS-1$
	private static final String IMAGE = "image"; //$NON-NLS-1$
	private static final String DISPLAY = "display"; //$NON-NLS-1$

	/**
	 * Second argument is expected to be a List<Map<String,Object>>. The map is either "title" =>
	 * "some_value_to_display", or {"display" => "display_name", "image" => "filename_of_image.png"}.
	 * 
	 * @param parent
	 * @param menuItems
	 */
	public MenuDialog(Shell parent, Map<String, Object>... menuItems)
	{
		super(parent);
		setInput(createItems(Arrays.asList(menuItems)));
	}

	private List<MenuDialogItem> createItems(final List<Map<String, Object>> partialMatches)
	{
		List<MenuDialogItem> items = new ArrayList<MenuDialogItem>();

		Map<String, Object> rep = partialMatches.iterator().next();
		if (rep.containsKey(TITLE))
		{
			for (Map<String, Object> map : partialMatches)
			{
				if (map.containsKey(SEPARATOR))
				{
					items.add(MenuDialogItem.SEPARATOR);
					continue;
				}
				String title = (String) map.get(TITLE);
				if (title.trim().equals("---")) //$NON-NLS-1$
				{
					items.add(MenuDialogItem.SEPARATOR);
					continue;
				}
				items.add(new MenuDialogItem(title));
			}
		}
		else
		{
			// image, display, insert, tool_tip
			for (Map<String, Object> map : partialMatches)
			{
				if (map.containsKey(SEPARATOR))
				{
					items.add(MenuDialogItem.SEPARATOR);
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
				items.add(new MenuDialogItem((String) map.get(DISPLAY), image));
			}
		}
		return items;
	}
}
