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
