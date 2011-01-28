/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.filesystem.s3;

import java.text.MessageFormat;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;

/**
 * @author Max Stepanov
 */
public final class Policy
{

	/**
	 * 
	 */
	private Policy()
	{
	}

	public static String generateAuthId(String proto, IBaseRemoteConnectionPoint connectionPoint)
	{
		return generateAuthId(proto, connectionPoint.getLogin(), connectionPoint.getHost());
	}

	protected static String generateAuthId(String proto, String login, String host)
	{
		if (host != null && host.length() > 0 && login != null && login.length() > 0)
		{
			return MessageFormat.format("{0}/{1}@{2}", new Object[] { //$NON-NLS-1$
					proto, login, host });
		}
		return null;
	}
}