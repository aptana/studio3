package net.contentobjects.jnotify;

@SuppressWarnings("nls")
public class Util
{
	public static String getMaskDesc(int mask)
	{
		StringBuffer s = new StringBuffer();
		if ((mask & IJNotify.FILE_CREATED) != 0)
		{
			s.append("FILE_CREATED|");
		}
		if ((mask & IJNotify.FILE_DELETED) != 0)
		{
			s.append("FILE_DELETED|");
		}
		if ((mask & IJNotify.FILE_MODIFIED) != 0)
		{
			s.append("FILE_MODIFIED|");
		}
		if ((mask & IJNotify.FILE_RENAMED) != 0)
		{
			s.append("FILE_RENAMED|");
		}
		if (s.length() > 0)
		{
			return s.substring(0, s.length() - 1);
		}
		return "UNKNOWN";
	}
}
