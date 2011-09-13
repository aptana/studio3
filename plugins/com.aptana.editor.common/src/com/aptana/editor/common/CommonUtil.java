/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 */
public final class CommonUtil
{

	/**
	 * 
	 */
	private CommonUtil()
	{
	}

	// TODO Ideally we generate a cache of tokens. We'd need a map with weak keys and soft values. Ideally we'd also
	// have some sort of reaper to clean up unused refs over time. Perhaps just use Google's Guava CacheBuilder?
	public static IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

}
