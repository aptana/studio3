package com.aptana.editor.js.contentassist;

import java.text.MessageFormat;
import java.util.UUID;

import com.aptana.editor.js.JSTypeConstants;

public class JSContentAssistUtil
{
	/**
	 * JSContentAssistUtil
	 */
	private JSContentAssistUtil()
	{
	}
	
	/**
	 * getUniqueTypeName
	 * 
	 * @return
	 */
	public static String getUniqueTypeName()
	{
		UUID uuid = UUID.randomUUID();

		return MessageFormat.format("{0}{1}", JSTypeConstants.DYNAMIC_CLASS_PREFIX, uuid); //$NON-NLS-1$
	}
}
