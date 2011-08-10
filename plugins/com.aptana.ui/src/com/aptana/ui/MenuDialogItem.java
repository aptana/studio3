/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.swt.graphics.Image;

public class MenuDialogItem
{

	public static final MenuDialogItem SEPARATOR = new MenuDialogItem("") //$NON-NLS-1$
	{
		public boolean isSeparator()
		{
			return true;
		};
	};

	private String text;
	private Image image;

	public MenuDialogItem(String text)
	{
		this(text, null);
	}

	public MenuDialogItem(String text, Image image)
	{
		this.text = text;
		this.image = image;
	}

	public String getText()
	{
		return text;
	}

	public Image getImage()
	{
		return image;
	}

	public boolean isSeparator()
	{
		return false;
	}
}
