package net.contentobjects.jnotify.macosx;

import net.contentobjects.jnotify.JNotifyException;

public class JNotifyException_macosx extends JNotifyException
{
	private static final long serialVersionUID = 1L;

	public JNotifyException_macosx(String s)
	{
		super(s, 1);
	}

	public int getErrorCode()
	{
		return 1;
	}

}
