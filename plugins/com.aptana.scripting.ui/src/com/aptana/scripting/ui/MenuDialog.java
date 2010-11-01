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
