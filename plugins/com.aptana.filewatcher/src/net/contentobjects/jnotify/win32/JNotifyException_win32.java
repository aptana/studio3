package net.contentobjects.jnotify.win32;

import net.contentobjects.jnotify.JNotifyException;

public class JNotifyException_win32 extends JNotifyException
{
	private static final long serialVersionUID = 1L;

	public JNotifyException_win32(String s, int systemErrorCode)
	{
		super(s, systemErrorCode);
	}

	public int getErrorCode()
	{
		return ERROR_UNSPECIFIED;
	}

}
